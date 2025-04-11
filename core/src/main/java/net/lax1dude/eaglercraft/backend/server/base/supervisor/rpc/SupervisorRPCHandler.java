package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import com.google.common.collect.MapMaker;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorProc;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorRPCHandler;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.NodeResult;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ProcedureDesc;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.SupervisorConnection;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.SupervisorService;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvRPCExecutePlayerName;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvRPCResultFail;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvRPCResultMulti;

public class SupervisorRPCHandler implements ISupervisorRPCHandler {

	private static final Consumer<? super ISupervisorData> NOP = new Consumer<ISupervisorData>() {
		@Override
		public void accept(ISupervisorData t) {
		}
	};

	private final SupervisorService<?> service;

	private final ReadWriteLock mapLock = new ReentrantReadWriteLock();
	private final Map<String, ProcedureDesc<? extends ISupervisorData, ? extends ISupervisorData>> procNameToDesc = new HashMap<>(256);
	private final Map<String, SupervisorProcedure> procNameToImplMap = new HashMap<>(256);
	private final Map<ProcedureDesc<? extends ISupervisorData, ? extends ISupervisorData>, SupervisorProcedure> procDescToImplMap = new IdentityHashMap<>(256);

	private final ConcurrentMap<UUID, ProcedureCallback> waitingProcedures = (new MapMaker()).initialCapacity(256)
			.concurrencyLevel(16).makeMap();
	private final Set<LocalTimeout<? extends Object>> waitingLocalTimeouts = Collections
			.newSetFromMap((new MapMaker()).initialCapacity(256).concurrencyLevel(16).makeMap());

	public SupervisorRPCHandler(SupervisorService<?> supervisorService) {
		this.service = supervisorService;
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void registerProcedure(ProcedureDesc<In, Out> desc,
			ISupervisorProc<? super In, ? extends Out> proc) {
		String name = desc.getName();
		SupervisorProcedure old = null;
		SupervisorProcedure newProc = new SupervisorProcedure(desc, proc);
		mapLock.writeLock().lock();
		try {
			ProcedureDesc<? extends ISupervisorData, ? extends ISupervisorData> oldDesc = procNameToDesc.put(name, desc);
			if(oldDesc != null) {
				procNameToImplMap.remove(oldDesc.getName());
				old = procDescToImplMap.remove(oldDesc);
			}
			procNameToImplMap.put(name, newProc);
			procDescToImplMap.put(desc, newProc);
		}finally {
			mapLock.writeLock().unlock();
		}
		if(old != null) {
			service.logger().error("Name conflict for supervisor procedure \"" + name + "\"",
					new RuntimeException("Stack trace"));
		}
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void unregisterProcedure(
			ProcedureDesc<In, Out> desc) {
		mapLock.writeLock().lock();
		try {
			ProcedureDesc<? extends ISupervisorData, ? extends ISupervisorData> oldDesc = procNameToDesc.remove(desc.getName());
			if(oldDesc != null) {
				procNameToImplMap.remove(oldDesc.getName());
				procDescToImplMap.remove(oldDesc);
			}
		}finally {
			mapLock.writeLock().unlock();
		}
	}

	private SupervisorProcedure getProcedure(String procName) {
		SupervisorProcedure ret;
		mapLock.readLock().lock();
		try {
			ret = procNameToImplMap.get(procName);
		}finally {
			mapLock.readLock().unlock();
		}
		if(ret == null) {
			throw new IllegalArgumentException("Unknown procedure name: " + procName);
		}
		return ret;
	}

	private SupervisorProcedure getProcedure(ProcedureDesc<?, ?> procDesc) {
		SupervisorProcedure ret;
		mapLock.readLock().lock();
		try {
			ret = procDescToImplMap.get(procDesc);
			if(ret != null) {
				return ret;
			}else {
				ret = procNameToImplMap.get(procDesc.getName());
			}
		}finally {
			mapLock.readLock().unlock();
		}
		if(ret == null) {
			throw new IllegalArgumentException("Unknown procedure name: " + procDesc.getName());
		}
		return ret;
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void invokeAtPlayer(String usernameIn,
			ProcedureDesc<In, Out> desc, int timeout, In input, Consumer<? super Out> output) {
		if(timeout <= 0 && output != null) {
			throw new IllegalArgumentException("Invalid timeout: " + timeout);
		}
		SupervisorProcedure procedure = getProcedure(desc);
		if(input == null) {
			throw new NullPointerException("Input must not be null!");
		}
		final String username = usernameIn.toLowerCase(Locale.US);
		if(output != null) {
			if(service.getEaglerXServer().getPlatform().getPlayer(username) != null) {
				long now = System.nanoTime();
				((ISupervisorProc<In, Out>) procedure.proc).call(service.getNodeId(), input,
						setLocalTimeout(now, new LocalTimeout<Out>(waitingLocalTimeouts, now + timeout * 1000000l) {
					@Override
					protected void onResultTimeout() {
						logWarningForResult(SPacketSvRPCResultFail.FAILURE_TIMEOUT, procedure.name,
								"player name \"" + username + "\"");
						output.accept(null);
					}
					@Override
					protected void onResultComplete(Out data) {
						output.accept(data);
					}
				}));
			}else {
				SupervisorConnection handler = service.getConnection();
				if(handler != null) {
					UUID uuid = UUID.randomUUID();
					long now = System.nanoTime();
					addWaitingCallback(now, new ProcedureCallback(uuid, waitingProcedures, now + timeout * 1000000l) {
						@Override
						protected void onResultFail(int type) {
							if(isLogWarningForResult(type)) {
								logWarningForResult(type, procedure.name, "player name \"" + username + "\"");
							}
							output.accept(null);
						}
						@Override
						protected void onResultSuccess(ByteBuf dataBuffer) {
							Out res;
							try {
								res = (Out)InjectedRPCPayload.deserialize(dataBuffer, procedure.outputType);
							} catch (Exception e) {
								logIOWarningForResult(procedure.name, e, "player name \"" + username + "\"");
								output.accept(null);
								return;
							}
							output.accept(res);
						}
						@Override
						protected void onResultMulti(Collection<SPacketSvRPCResultMulti.ResultEntry> list) {
							logIOWarningForMultiResult(procedure.name, "player name \"" + username + "\"");
							output.accept(null);
						}
					});
					handler.sendSupervisorPacket(new CPacketSvRPCExecutePlayerName(uuid, timeout, username,
							new InjectedRPCPayload(procedure.name, input)));
				}else {
					output.accept(null);
				}
			}
		}else {
			if(service.getEaglerXServer().getPlatform().getPlayer(username) != null) {
				((ISupervisorProc<In, Out>)procedure.proc).call(service.getNodeId(), input, (Consumer<Out>)NOP);
			}else {
				SupervisorConnection handler = service.getConnection();
				if(handler != null) {
					handler.sendSupervisorPacket(new CPacketSvRPCExecutePlayerName(null, 0, username,
							new InjectedRPCPayload(procedure.name, input)));
				}
			}
		}
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void invokeAtPlayer(UUID player,
			ProcedureDesc<In, Out> desc, int timeout, In input, Consumer<? super Out> output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void invokeAtNode(int nodeId,
			ProcedureDesc<In, Out> desc, int timeout, In input, Consumer<? super Out> output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void invokeAllNodes(ProcedureDesc<In, Out> desc,
			int timeout, In input, Consumer<Collection<NodeResult<? super Out>>> output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void invokeAllOtherNodes(
			ProcedureDesc<In, Out> desc, int timeout, In input, Consumer<Collection<NodeResult<? super Out>>> output) {
		// TODO Auto-generated method stub
		
	}

	private void addWaitingCallback(long now, ProcedureCallback callback) {
		waitingProcedures.put(callback.key, callback);
		service.timeoutLoop().addFuture(now, callback);
	}

	private <T extends Object> LocalTimeout<T> setLocalTimeout(long now, LocalTimeout<T> callback) {
		waitingLocalTimeouts.add(callback);
		service.timeoutLoop().addFuture(now, callback);
		return callback;
	}

	public void onRPCExecute(UUID requestUUID, int sourceNodeId, String name, ByteBuf dataBuffer) {
		// TODO Auto-generated method stub
		
	}

	public void onRPCExecuteVoid(int sourceNodeId, String name, ByteBuf dataBuffer) {
		// TODO Auto-generated method stub
		
	}

	public void onRPCResultSuccess(UUID uuid, ByteBuf dataBuffer) {
		ProcedureCallback cb = waitingProcedures.remove(uuid);
		if(cb != null) {
			cb.onResultSuccess(dataBuffer);
		}else {
			service.logger().warn("Received success result for unknown/expired RPC " + uuid);
		}
	}

	public void onRPCResultMulti(UUID uuid, Collection<SPacketSvRPCResultMulti.ResultEntry> list) {
		ProcedureCallback cb = waitingProcedures.remove(uuid);
		if(cb != null) {
			cb.onResultMulti(list);
		}else {
			service.logger().warn("Received multi result for unknown/expired RPC " + uuid);
		}
	}

	public void onRPCResultFail(UUID uuid, int type) {
		ProcedureCallback cb = waitingProcedures.remove(uuid);
		if(cb != null) {
			cb.onResultFail(type);
		}else {
			service.logger().warn("Received failure result for unknown/expired RPC " + uuid);
		}
	}

	private static boolean isLogWarningForResult(int type) {
		return type != SPacketSvRPCResultFail.FAILURE_PROCEDURE;
	}

	private void logWarningForResult(int type, String procName, String str) {
		String str2;
		switch(type) {
		case SPacketSvRPCResultFail.FAILURE_NOT_FOUND:
			str2 = "Target not found";
			break;
		case SPacketSvRPCResultFail.FAILURE_TIMEOUT:
			str2 = "Reached timeout";
			break;
		default:
			str2 = "Unknown";
			break;
		}
		service.logger().warn("Procedure \"" + procName + "\" failed for " + str + ", reason: " + str2);
	}

	private void logIOWarningForResult(String procName, Exception ex, String str) {
		service.logger().warn("Parsing result for procedure \"" + procName + "\" failed for " + str, ex);
	}

	private void logIOWarningForMultiResult(String procName, String str) {
		service.logger().warn("Parsing result for procedure \"" + procName + "\" failed for " + str + ", received unexpected multi-result");
	}

}
