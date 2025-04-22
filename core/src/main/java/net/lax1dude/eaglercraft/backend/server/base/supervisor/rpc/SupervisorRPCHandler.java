/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import java.util.ArrayList;
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
import net.lax1dude.eaglercraft.backend.server.api.collect.IntSet;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorProc;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorRPCHandler;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.NodeResult;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ProcedureDesc;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.SupervisorDataVoid;
import net.lax1dude.eaglercraft.backend.server.base.collect.IntHashSet;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.SupervisorConnection;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.SupervisorService;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvRPCExecuteAll;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvRPCExecuteNode;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvRPCExecutePlayerName;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvRPCExecutePlayerUUID;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvRPCResultFail;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvRPCResultSuccess;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvRPCResultFail;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvRPCResultMulti;

public class SupervisorRPCHandler implements ISupervisorRPCHandler {

	private static final Consumer<? super ISupervisorData> NOP = new Consumer<Object>() {
		@Override
		public void accept(Object t) {
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
		if(desc == null) {
			throw new NullPointerException("desc");
		}
		if(proc == null) {
			throw new NullPointerException("proc");
		}
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
		if(desc == null) {
			throw new NullPointerException("desc");
		}
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

	private SupervisorProcedure getProcedureQuiet(String procName) {
		mapLock.readLock().lock();
		try {
			return procNameToImplMap.get(procName);
		}finally {
			mapLock.readLock().unlock();
		}
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
						acceptSafe(output, null, true);
					}
					@Override
					protected void onResultComplete(Out data) {
						acceptSafe(output, data, false);
					}
				}));
			}else {
				SupervisorConnection handler = service.getConnection();
				if(handler != null) {
					UUID uuid = UUID.randomUUID();
					long now = System.nanoTime();
					addWaitingCallback(now, new ProcedureCallback(uuid, waitingProcedures, now + (timeout + 5000) * 1000000l) {
						@Override
						protected void onResultFail(int type) {
							if(isLogWarningForResult(type)) {
								logWarningForResult(type, procedure.name, "player name \"" + username + "\"");
							}
							acceptSafe(output, null, true);
						}
						@Override
						protected void onResultSuccess(ByteBuf dataBuffer) {
							Out res;
							try {
								res = (Out)InjectedRPCPayload.deserialize(dataBuffer, procedure.outputType);
							} catch (Exception e) {
								logIOWarningForResult(procedure.name, e);
								acceptSafe(output, null, true);
								return;
							}
							acceptSafe(output, res, true);
						}
						@Override
						protected void onResultMulti(Collection<SPacketSvRPCResultMulti.ResultEntry> list) {
							logIOWarningForMultiResult(procedure.name);
							acceptSafe(output, null, true);
						}
					});
					handler.sendSupervisorPacket(new CPacketSvRPCExecutePlayerName(uuid, timeout, username,
							new InjectedRPCPayload(procedure.name, input)));
				}else {
					acceptSafe(output, null, false);
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
		if(timeout <= 0 && output != null) {
			throw new IllegalArgumentException("Invalid timeout: " + timeout);
		}
		SupervisorProcedure procedure = getProcedure(desc);
		if(input == null) {
			throw new NullPointerException("Input must not be null!");
		}
		if(input.getClass() != procedure.inputType.clazz) {
			throw new IllegalArgumentException("Input object is the wrong type, " + input.getClass().getName() + " != "
					+ procedure.inputType.clazz.getName());
		}
		if(output != null) {
			if(service.getEaglerXServer().getPlatform().getPlayer(player) != null) {
				long now = System.nanoTime();
				((ISupervisorProc<In, Out>) procedure.proc).call(service.getNodeId(), input,
						setLocalTimeout(now, new LocalTimeout<Out>(waitingLocalTimeouts, now + timeout * 1000000l) {
					@Override
					protected void onResultTimeout() {
						logWarningForResult(SPacketSvRPCResultFail.FAILURE_TIMEOUT, procedure.name,
								"player uuid \"" + player + "\"");
						acceptSafe(output, null, true);
					}
					@Override
					protected void onResultComplete(Out data) {
						acceptSafe(output, data, false);
					}
				}));
			}else {
				SupervisorConnection handler = service.getConnection();
				if(handler != null) {
					UUID uuid = UUID.randomUUID();
					long now = System.nanoTime();
					addWaitingCallback(now, new ProcedureCallback(uuid, waitingProcedures, now + (timeout + 5000) * 1000000l) {
						@Override
						protected void onResultFail(int type) {
							if(isLogWarningForResult(type)) {
								logWarningForResult(type, procedure.name, "player uuid \"" + player + "\"");
							}
							acceptSafe(output, null, true);
						}
						@Override
						protected void onResultSuccess(ByteBuf dataBuffer) {
							Out res;
							try {
								res = (Out)InjectedRPCPayload.deserialize(dataBuffer, procedure.outputType);
							} catch (Exception e) {
								logIOWarningForResult(procedure.name, e);
								acceptSafe(output, null, true);
								return;
							}
							acceptSafe(output, res, true);
						}
						@Override
						protected void onResultMulti(Collection<SPacketSvRPCResultMulti.ResultEntry> list) {
							logIOWarningForMultiResult(procedure.name);
							acceptSafe(output, null, true);
						}
					});
					handler.sendSupervisorPacket(new CPacketSvRPCExecutePlayerUUID(uuid, timeout, player,
							new InjectedRPCPayload(procedure.name, input)));
				}else {
					acceptSafe(output, null, false);
				}
			}
		}else {
			if(service.getEaglerXServer().getPlatform().getPlayer(player) != null) {
				((ISupervisorProc<In, Out>)procedure.proc).call(service.getNodeId(), input, (Consumer<Out>)NOP);
			}else {
				SupervisorConnection handler = service.getConnection();
				if(handler != null) {
					handler.sendSupervisorPacket(new CPacketSvRPCExecutePlayerUUID(null, 0, player,
							new InjectedRPCPayload(procedure.name, input)));
				}
			}
		}
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void invokeAtNode(int nodeId,
			ProcedureDesc<In, Out> desc, int timeout, In input, Consumer<? super Out> output) {
		if(timeout <= 0 && output != null) {
			throw new IllegalArgumentException("Invalid timeout: " + timeout);
		}
		SupervisorProcedure procedure = getProcedure(desc);
		if(input == null) {
			throw new NullPointerException("Input must not be null!");
		}
		if(input.getClass() != procedure.inputType.clazz) {
			throw new IllegalArgumentException("Input object is the wrong type, " + input.getClass().getName() + " != "
					+ procedure.inputType.clazz.getName());
		}
		if(output != null) {
			if(nodeId == -1) {
				long now = System.nanoTime();
				((ISupervisorProc<In, Out>) procedure.proc).call(-1, input,
						setLocalTimeout(now, new LocalTimeout<Out>(waitingLocalTimeouts, now + timeout * 1000000l) {
					@Override
					protected void onResultTimeout() {
						logWarningForResult(SPacketSvRPCResultFail.FAILURE_TIMEOUT, procedure.name, "node [self]");
						acceptSafe(output, null, true);
					}
					@Override
					protected void onResultComplete(Out data) {
						acceptSafe(output, data, false);
					}
				}));
			}else {
				SupervisorConnection handler = service.getConnection();
				if(handler != null) {
					if(nodeId == handler.getNodeId()) {
						long now = System.nanoTime();
						((ISupervisorProc<In, Out>) procedure.proc).call(nodeId, input,
								setLocalTimeout(now, new LocalTimeout<Out>(waitingLocalTimeouts, now + timeout * 1000000l) {
							@Override
							protected void onResultTimeout() {
								logWarningForResult(SPacketSvRPCResultFail.FAILURE_TIMEOUT, procedure.name, "node [self]");
								acceptSafe(output, null, true);
							}
							@Override
							protected void onResultComplete(Out data) {
								acceptSafe(output, data, false);
							}
						}));
					}else {
						UUID uuid = UUID.randomUUID();
						long now = System.nanoTime();
						addWaitingCallback(now, new ProcedureCallback(uuid, waitingProcedures, now + (timeout + 5000) * 1000000l) {
							@Override
							protected void onResultFail(int type) {
								if(isLogWarningForResult(type)) {
									logWarningForResult(type, procedure.name, "node " + nodeId);
								}
								acceptSafe(output, null, true);
							}
							@Override
							protected void onResultSuccess(ByteBuf dataBuffer) {
								Out res;
								try {
									res = (Out)InjectedRPCPayload.deserialize(dataBuffer, procedure.outputType);
								} catch (Exception e) {
									logIOWarningForResult(procedure.name, e);
									acceptSafe(output, null, true);
									return;
								}
								acceptSafe(output, res, true);
							}
							@Override
							protected void onResultMulti(Collection<SPacketSvRPCResultMulti.ResultEntry> list) {
								logIOWarningForMultiResult(procedure.name);
								acceptSafe(output, null, true);
							}
						});
						handler.sendSupervisorPacket(new CPacketSvRPCExecuteNode(uuid, timeout, nodeId,
								new InjectedRPCPayload(procedure.name, input)));
					}
				}else {
					acceptSafe(output, null, false);
				}
			}
		}else {
			if(nodeId == -1) {
				((ISupervisorProc<In, Out>)procedure.proc).call(-1, input, (Consumer<Out>)NOP);
			}else {
				SupervisorConnection handler = service.getConnection();
				if(handler != null) {
					if(nodeId == handler.getNodeId()) {
						((ISupervisorProc<In, Out>)procedure.proc).call(nodeId, input, (Consumer<Out>)NOP);
					}else {
						if(handler != null) {
							handler.sendSupervisorPacket(new CPacketSvRPCExecuteNode(null, 0, nodeId,
									new InjectedRPCPayload(procedure.name, input)));
						}
					}
				}
			}
		}
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void invokeAllNodes(ProcedureDesc<In, Out> desc,
			int timeout, In input, Consumer<? super Collection<NodeResult<Out>>> output) {
		if(timeout <= 0 && output != null) {
			throw new IllegalArgumentException("Invalid timeout: " + timeout);
		}
		SupervisorProcedure procedure = getProcedure(desc);
		if(input == null) {
			throw new NullPointerException("Input must not be null!");
		}
		if(input.getClass() != procedure.inputType.clazz) {
			throw new IllegalArgumentException("Input object is the wrong type, " + input.getClass().getName() + " != "
					+ procedure.inputType.clazz.getName());
		}
		SupervisorConnection handler = service.getConnection();
		int selfId = handler.getNodeId();
		if(output != null) {
			InvokeAllNodesHelper<Out> invokeAll = new InvokeAllNodesHelper<Out>(output,
					service.getEaglerXServer().getPlatform().getScheduler(), service.logger());
			long now = System.nanoTime();
			((ISupervisorProc<In, Out>) procedure.proc).call(selfId, input,
					setLocalTimeout(now, new LocalTimeout<Out>(waitingLocalTimeouts, now + timeout * 1000000l) {
				@Override
				protected void onResultTimeout() {
					onResultComplete(null);
				}
				@Override
				protected void onResultComplete(Out data) {
					invokeAll.acceptLocal(NodeResult.create(selfId, data));
				}
			}));
			invokeAllOtherNodes0(procedure, handler, timeout, input, false, invokeAll);
		}else {
			invokeAllOtherNodes0(procedure, handler, 0, input, false, null);
			((ISupervisorProc<In, Out>)procedure.proc).call(selfId, input, (Consumer<Out>)NOP);
		}
	}

	@Override
	public <In extends ISupervisorData, Out extends ISupervisorData> void invokeAllOtherNodes(
			ProcedureDesc<In, Out> desc, int timeout, In input, Consumer<? super Collection<NodeResult<Out>>> output) {
		if(timeout <= 0 && output != null) {
			throw new IllegalArgumentException("Invalid timeout: " + timeout);
		}
		SupervisorProcedure procedure = getProcedure(desc);
		if(input == null) {
			throw new NullPointerException("Input must not be null!");
		}
		if(input.getClass() != procedure.inputType.clazz) {
			throw new IllegalArgumentException("Input object is the wrong type, " + input.getClass().getName() + " != "
					+ procedure.inputType.clazz.getName());
		}
		invokeAllOtherNodes0(procedure, service.getConnection(), timeout, input, true, output);
	}

	private <In extends ISupervisorData, Out extends ISupervisorData> void invokeAllOtherNodes0(
			SupervisorProcedure procedure, SupervisorConnection handler, int timeout, In input, boolean async,
			Consumer<? super Collection<NodeResult<Out>>> output) {
		if(output != null) {
			if(handler != null) {
				UUID uuid = UUID.randomUUID();
				long now = System.nanoTime();
				addWaitingCallback(now, new ProcedureCallback(uuid, waitingProcedures, now + (timeout + 5000) * 1000000l) {
					@Override
					protected void onResultFail(int type) {
						if(isLogWarningForResult(type)) {
							logWarningForResult(type, procedure.name, "supervisor");
						}
						acceptSafe(output, null, async);
					}
					@Override
					protected void onResultSuccess(ByteBuf dataBuffer) {
						service.logger().warn("Parsing result for procedure \"" + procedure.name + "\" failed, received unexpected non-multi-result");
						acceptSafe(output, null, async);
					}
					@Override
					protected void onResultMulti(Collection<SPacketSvRPCResultMulti.ResultEntry> list) {
						Collection<NodeResult<Out>> ret = new ArrayList<>(list.size());
						for(SPacketSvRPCResultMulti.ResultEntry etr : list) {
							if(etr.status == 0) {
								Out res;
								try {
									res = (Out)InjectedRPCPayload.deserialize(etr.dataBuffer, procedure.outputType);
								} catch (Exception e) {
									logIOWarningForResult(procedure.name, e);
									acceptSafe(output, null, async);
									return;
								}
								ret.add(NodeResult.create(etr.nodeId, res));
							}else {
								int type = etr.status - 1;
								if(isLogWarningForResult(type)) {
									logWarningForResult(type, procedure.name, "node " + etr.nodeId);
								}
								ret.add(NodeResult.create(etr.nodeId, null));
							}
						}
						acceptSafe(output, ret, async);
					}
				});
				handler.sendSupervisorPacket(new CPacketSvRPCExecuteAll(uuid, timeout,
						new InjectedRPCPayload(procedure.name, input)));
			}else {
				acceptSafe(output, null, false);
			}
		}else {
			if(handler != null) {
				handler.sendSupervisorPacket(new CPacketSvRPCExecuteAll(null, 0,
						new InjectedRPCPayload(procedure.name, input)));
			}
		}
	}

	private <T> void acceptSafe(Consumer<T> consumer, T value, boolean async) {
		if(async) {
			service.getEaglerXServer().getPlatform().getScheduler().executeAsync(() -> {
				try {
					consumer.accept(value);
				}catch(Exception ex) {
					service.logger().error("Caught exception from RPC result callback", ex);
				}
			});
		}else {
			try {
				consumer.accept(value);
			}catch(Exception ex) {
				service.logger().error("Caught exception from RPC result callback", ex);
			}
		}
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

	public void onRPCExecute(SupervisorConnection conn, UUID requestUUID, int sourceNodeId, String name,
			ByteBuf dataBuffer) {
		SupervisorProcedure proc = getProcedureQuiet(name);
		if(proc != null) {
			ISupervisorData data;
			try {
				data = InjectedRPCPayload.deserialize(dataBuffer, proc.inputType);
			}catch(Exception ex) {
				service.logger().error("Could not deserialize type " + proc.inputType.clazz.getName(), ex);
				return;
			}
			service.getEaglerXServer().getPlatform().getScheduler().executeAsync(() -> {
				try {
					((ISupervisorProc<ISupervisorData, ISupervisorData>)proc.proc).call(sourceNodeId, data, (res) -> {
						if(conn.getChannel().isActive()) {
							if(res != null) {
								conn.getChannel().eventLoop().execute(() -> {
									ByteBuf buf = conn.getChannel().alloc().buffer();
									try {
										InjectedRPCPayload.serialize(buf, res);
										conn.sendSupervisorPacket(new CPacketSvRPCResultSuccess(requestUUID, buf.retain()));
									}finally {
										buf.release();
									}
								});
							}else {
								conn.sendSupervisorPacket(new CPacketSvRPCResultFail(requestUUID));
							}
						}
					});
				}catch(Exception ex) {
					service.logger().error("Could not invoke procedure \"" + name + "\"", ex);
				}
			});
		}else {
			service.logger().warn("Supervisor attempted to invoke unknown procedure \"" + name + "\"");
		}
	}

	public void onRPCExecuteVoid(int sourceNodeId, String name, ByteBuf dataBuffer) {
		SupervisorProcedure proc = getProcedureQuiet(name);
		if(proc != null) {
			ISupervisorData data;
			try {
				data = InjectedRPCPayload.deserialize(dataBuffer, proc.inputType);
			}catch(Exception ex) {
				service.logger().error("Could not deserialize type " + proc.inputType.clazz.getName(), ex);
				return;
			}
			service.getEaglerXServer().getPlatform().getScheduler().executeAsync(() -> {
				try {
					((ISupervisorProc<ISupervisorData, ISupervisorData>)proc.proc).call(sourceNodeId, data, (Consumer<ISupervisorData>)NOP);
				}catch(Exception ex) {
					service.logger().error("Could not invoke procedure \"" + name + "\"", ex);
				}
			});
		}else {
			service.logger().warn("Supervisor attempted to invoke unknown procedure \"" + name + "\"");
		}
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

	private void logIOWarningForResult(String procName, Exception ex) {
		service.logger().warn("Parsing result for procedure \"" + procName + "\" failed", ex);
	}

	private void logIOWarningForMultiResult(String procName) {
		service.logger().warn("Parsing result for procedure \"" + procName + "\" failed, received unexpected multi-result");
	}

	@Override
	public IntSet toIntSet(Collection<NodeResult<SupervisorDataVoid>> collection) {
		if(collection == null) {
			return null;
		}
		IntSet ret = new IntHashSet(collection.size());
		for (NodeResult<? extends SupervisorDataVoid> o : collection) {
			if (o.isSuccessful()) {
				ret.add(o.getNodeId());
			}
		}
		return ret;
	}

}
