package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.WrongRPCPacketException;

public abstract class ServerRPCProtocolHandler implements EaglerBackendRPCHandler {

	protected final BasePlayerRPCContext<?> rpcContext;

	public ServerRPCProtocolHandler(BasePlayerRPCContext<?> rpcContext) {
		this.rpcContext = rpcContext;
	}

	protected RuntimeException wrongPacket() {
		return new WrongRPCPacketException();
	}

}
