package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCEnabled;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledFailure;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;

public abstract class BasePlayerRPCManager<PlayerObject> {

	protected final BackendRPCService<PlayerObject> service;
	protected BasePlayerRPCContext<PlayerObject> context;

	BasePlayerRPCManager(BackendRPCService<PlayerObject> service) {
		this.service = service;
	}

	public abstract BasePlayerInstance<PlayerObject> getPlayer();

	public abstract boolean isEaglerPlayer();

	public void sendRPCInitPacket(EaglerBackendRPCPacket packet) {
		byte[] data;
		try {
			data = service.handshakeCtx.serialize(packet);
		} catch (IOException e) {
			handleException(e);
			return;
		}
		getPlayer().getPlatformPlayer().sendDataBackend(service.getRPCChannel(), data);
	}

	public void sendRPCPacket(EaglerBackendRPCPacket packet) {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			byte[] data;
			try {
				data = ctx.serialize(packet);
			} catch (IOException e) {
				handleException(e);
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
				handleException(e);
				return;
			}
			try {
				packet.handlePacket(ctx.packetHandler());
			} catch(Exception e) {
				handleException(e);
			}
		}else {
			EaglerBackendRPCPacket packet;
			try {
				packet = service.handshakeCtx.deserialize(data);
			} catch (IOException e) {
				handleException(e);
				return;
			}
			if(packet instanceof CPacketRPCEnabled pkt) {
				boolean V1 = false, V2 = false;
				for(int i : pkt.supportedProtocols) {
					if(i == 1) V1 = true;
					if(i == 2) V2 = true;
					if(V2) break;
				}
				if(V2) {
					handleEnabled(EaglerBackendRPCProtocol.V2);
				}else if(V1) {
					handleEnabled(EaglerBackendRPCProtocol.V1);
				}else {
					sendRPCInitPacket(new SPacketRPCEnabledFailure(SPacketRPCEnabledFailure.FAILURE_CODE_OUTDATED_SERVER));
				}
			}else {
				handleException(new IllegalStateException("Unexpected packet type for handshake: " + packet.getClass().getName()));
				sendRPCInitPacket(new SPacketRPCEnabledFailure(SPacketRPCEnabledFailure.FAILURE_CODE_INTERNAL_ERROR));
			}
		}
	}

	BasePlayerRPCContext<PlayerObject> context() {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			return ctx;
		}else {
			throw new IllegalStateException();
		}
	}

	void handleException(Exception ex) {
		getPlayer().getEaglerXServer().logger().error("Exception thrown while handling backend RPC packet for \"" + getPlayer().getUsername() + "\"!", ex);
	}

	protected abstract void handleEnabled(EaglerBackendRPCProtocol ver);

	protected void handleEnableContext(BasePlayerRPCContext<PlayerObject> ctx) {
		context = ctx;
	}

	void handleDisabled() {
		context = null;
	}

	public void handleServerPreConnect() {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			ctx.handleDisabled();
		}
	}

	public void handleServerPostConnect() {
		sendReadyMessage();
	}

	protected abstract void sendReadyMessage();

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

	public void fireToggleVoice(EnumVoiceState oldVoiceState, EnumVoiceState newVoiceState) {
		BasePlayerRPCContext<PlayerObject> ctx = context;
		if(ctx != null) {
			ctx.fireToggleVoice(oldVoiceState, newVoiceState);
		}
	}

}
