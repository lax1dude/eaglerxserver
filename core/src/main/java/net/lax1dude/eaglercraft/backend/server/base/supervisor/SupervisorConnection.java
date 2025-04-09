package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.SocketAddress;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorConnection;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvPing;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvProxyStatus;

public class SupervisorConnection implements ISupervisorConnection, INettyChannel.NettyUnsafe {

	private static final VarHandle LAST_PING_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			LAST_PING_HANDLE = l.findVarHandle(SupervisorConnection.class, "lastPing", long.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	final SupervisorService<?> service;
	final SupervisorPacketHandler handler;
	final SupervisorLookupHandler<?> lookupHandler;
	private final int nodeId;
	private int proxyPing;
	private int playerTotal;
	private int playerMax;

	public SupervisorConnection(SupervisorService<?> service, SupervisorPacketHandler handler, int nodeId) {
		this.service = service;
		this.handler = handler;
		this.nodeId = nodeId;
		this.lookupHandler = new SupervisorLookupHandler<>(service, this);
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return handler.getChannel().remoteAddress();
	}

	@Override
	public int getProtocolVersion() {
		return handler.getConnectionProtocol().vers;
	}

	@Override
	public int getNodeId() {
		return nodeId;
	}

	@Override
	public long getPing() {
		return proxyPing;
	}

	public int getPlayerTotal() {
		return playerTotal;
	}

	public int getPlayerMax() {
		return playerMax;
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return handler.getChannel();
	}

	@Override
	public SocketAddress getSocketAddress() {
		return handler.getChannel().remoteAddress();
	}

	public void sendSupervisorPacket(EaglerSupervisorPacket msg) {
		handler.channelWrite(msg);
	}

	void onPongPacket() {
		long result = (long)LAST_PING_HANDLE.getAndSet(this, 0l);
		if(result != 0l) {
			proxyPing = (int)(Util.steadyTime() - result);
		}
	}

	void onPlayerCount(int playerTotal, int playerMax) {
		this.playerTotal = playerTotal;
		this.playerMax = playerMax;
	}

	void updatePing() {
		if(LAST_PING_HANDLE.compareAndSet(0l, Util.steadyTime())) {
			handler.channelWrite(new CPacketSvPing());
		}
		handler.channelWrite(new CPacketSvProxyStatus(System.currentTimeMillis(),
				service.getEaglerXServer().getPlatform().getPlayerTotal()));
	}

}
