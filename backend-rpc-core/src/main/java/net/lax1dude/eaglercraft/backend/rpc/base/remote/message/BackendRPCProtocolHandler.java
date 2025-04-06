package net.lax1dude.eaglercraft.backend.rpc.base.remote.message;

import net.lax1dude.eaglercraft.backend.rpc.base.remote.BasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.WrongRPCPacketException;

public abstract class BackendRPCProtocolHandler implements EaglerBackendRPCHandler {

	protected final BasePlayerRPC<?> rpcContext;

	public BackendRPCProtocolHandler(BasePlayerRPC<?> rpcContext) {
		this.rpcContext = rpcContext;
	}

	protected RuntimeException wrongPacket() {
		return new WrongRPCPacketException();
	}

}
