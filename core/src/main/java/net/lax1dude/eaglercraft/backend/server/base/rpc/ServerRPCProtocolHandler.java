package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.WrongRPCPacketException;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public abstract class ServerRPCProtocolHandler implements BasePlayerRPCManager.IMessageHandler {

	protected final BasePlayerRPCManager<?> rpcManager;

	public ServerRPCProtocolHandler(BasePlayerRPCManager<?> rpcManager) {
		this.rpcManager = rpcManager;
	}

	public BasePlayerInstance<?> getPlayer() {
		return rpcManager.getPlayer();
	}

	public EaglerXServer<?> getServer() {
		return rpcManager.getPlayer().getEaglerXServer();
	}

	@Override
	public void handleException(Exception ex) {
		getServer().logger().error("Exception thrown while handling backend RPC packet for \"" + getPlayer().getUsername() + "\"!", ex);
	}

	protected RuntimeException wrongPacket() {
		return new WrongRPCPacketException();
	}

}
