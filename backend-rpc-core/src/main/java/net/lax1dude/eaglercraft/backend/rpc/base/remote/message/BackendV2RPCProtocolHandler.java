package net.lax1dude.eaglercraft.backend.rpc.base.remote.message;

import net.lax1dude.eaglercraft.backend.rpc.api.data.BrandData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.CookieData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewStateData;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.BasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.*;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public class BackendV2RPCProtocolHandler extends BackendRPCProtocolHandler {

	public BackendV2RPCProtocolHandler(BasePlayerRPC<?> rpcContext) {
		super(rpcContext);
	}

	public void handleServer(SPacketRPCResponseTypeNull packet) {
		rpcContext.handleResponseComplete(packet.requestID, null);
	}

	public void handleServer(SPacketRPCResponseTypeBytes packet) {
		rpcContext.handleResponseComplete(packet.requestID, packet.response);
	}

	public void handleServer(SPacketRPCResponseTypeString packet) {
		rpcContext.handleResponseComplete(packet.requestID, packet.response);
	}

	public void handleServer(SPacketRPCResponseTypeUUID packet) {
		rpcContext.handleResponseComplete(packet.requestID, packet.uuid);
	}

	public void handleServer(SPacketRPCResponseTypeCookie packet) {
		rpcContext.handleResponseComplete(packet.requestID,
				packet.cookiesEnabled ? CookieData.create(packet.cookieData) : CookieData.disabled());
	}

	public void handleServer(SPacketRPCResponseTypeVoiceStatus packet) {
		EnumVoiceState state;
		switch(packet.voiceState) {
		case SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_SERVER_DISABLE:
		default:
			state = EnumVoiceState.SERVER_DISABLE;
			break;
		case SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_DISABLED:
			state = EnumVoiceState.DISABLED;
			break;
		case SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_ENABLED:
			state = EnumVoiceState.ENABLED;
			break;
		}
		rpcContext.handleResponseComplete(packet.requestID, state);
	}

	public void handleServer(SPacketRPCResponseTypeError packet) {
		rpcContext.handleResponseError(packet.requestID, packet.errorMessage);
	}

	public void handleServer(SPacketRPCEventWebViewOpenClose packet) {
		//TODO
	}

	public void handleServer(SPacketRPCEventWebViewMessage packet) {
		//TODO
	}

	public void handleServer(SPacketRPCEventToggledVoice packet) {
		//TODO
	}

	public void handleServer(SPacketRPCResponseTypeBrandDataV2 packet) {
		rpcContext.handleResponseComplete(packet.requestID,
				BrandData.create(packet.brand, packet.version, packet.uuid));
	}

	public void handleServer(SPacketRPCResponseTypeWebViewStatusV2 packet) {
		rpcContext.handleResponseComplete(packet.requestID, WebViewStateData.create(packet.webViewAllowed,
				packet.channelAllowed, packet.openChannels));
	}

	public void handleServer(SPacketRPCResponseTypeIntegerSingleV2 packet) {
		rpcContext.handleResponseComplete(packet.requestID, packet);
	}

	public void handleServer(SPacketRPCResponseTypeIntegerTupleV2 packet) {
		rpcContext.handleResponseComplete(packet.requestID, packet);
	}

}
