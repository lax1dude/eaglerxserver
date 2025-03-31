package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public abstract class BasePlayerRPCManager<PlayerObject> {

	public interface IExceptionCallback {
		void handleException(Exception ex);
	}

	public interface IMessageHandler extends EaglerBackendRPCHandler, IExceptionCallback {
	}

	protected final BackendRPCService<PlayerObject> service;
	protected BasePlayerRPCContext<PlayerObject> context;
	protected EaglerBackendRPCHandler packetHandler;
	protected IExceptionCallback exceptionHandler;

	BasePlayerRPCManager(BackendRPCService<PlayerObject> service) {
		this.service = service;
	}

	public abstract BasePlayerInstance<PlayerObject> getPlayer();

	public abstract boolean isEaglerPlayer();

	public void sendRPCPacket(EaglerBackendRPCPacket packet) {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			byte[] data;
			try {
				data = ctx.serialize(packet);
			} catch (IOException e) {
				onException(e);
				return;
			}
			getPlayer().getPlatformPlayer().sendDataBackend(service.getRPCChannel(), data);
		}
	}

	public void handleRPCPacketData(byte[] data) {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			EaglerBackendRPCPacket packet;
			try {
				packet = ctx.deserialize(data);
			} catch (IOException e) {
				onException(e);
				return;
			}
			handleRPCPacket(packet);
		}else {
			//TODO
		}
	}

	public void handleRPCPacket(EaglerBackendRPCPacket packet) {
		try {
			packet.handlePacket(packetHandler);
		}catch(Exception ex) {
			onException(ex);
		}
	}

	protected void onException(Exception ex) {
		exceptionHandler.handleException(ex);
	}

	BasePlayerRPCContext<PlayerObject> context() {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			return ctx;
		}else {
			throw new IllegalStateException();
		}
	}

	void handleDisabled() {
		context = null;
	}

	public void handleSwitchServers() {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			ctx.handleDisabled();
		}
	}

	public void fireWebViewOpenClose(boolean open, String channel) {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			ctx.fireWebViewOpenClose(open, channel);
		}
	}

	public void fireWebViewMessage(String channel, boolean binary, byte[] data) {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			ctx.fireWebViewMessage(channel, binary, data);
		}
	}

	public void fireToggleVoice(EnumVoiceState voiceState) {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			ctx.fireToggleVoice(voiceState);
		}
	}

}
