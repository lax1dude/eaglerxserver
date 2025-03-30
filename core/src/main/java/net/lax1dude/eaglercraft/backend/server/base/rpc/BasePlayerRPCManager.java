package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;

public abstract class BasePlayerRPCManager<PlayerObject> {

	public interface IExceptionCallback {
		void handleException(Exception ex);
	}

	public interface IMessageHandler extends EaglerBackendRPCHandler, IExceptionCallback {
	}

	protected BasePlayerRPCContext<PlayerObject> context;
	protected EaglerBackendRPCHandler packetHandler;
	protected IExceptionCallback exceptionHandler;

	BasePlayerRPCManager() {
	}

	public abstract BasePlayerInstance<PlayerObject> getPlayer();

	public abstract boolean isEaglerPlayer();

	public void sendRPCPacket(EaglerBackendRPCPacket packet) {
		
	}

	BasePlayerRPCContext<PlayerObject> context() {
		if(context != null) {
			return context;
		}else {
			throw new IllegalStateException();
		}
	}

	void handleDisabledLegacy() {
		
	}

}
