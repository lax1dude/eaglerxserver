package net.lax1dude.eaglercraft.backend.rpc.base.remote.message;

import net.lax1dude.eaglercraft.backend.rpc.api.data.BrandData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.CookieData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.VoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewMessageEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewOpenCloseEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewStateData;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.BasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.WrongRPCPacketException;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.*;

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
		rpcContext.handleResponseComplete(packet.requestID, switch(packet.voiceState) {
		case SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_SERVER_DISABLE -> EnumVoiceState.SERVER_DISABLE;
		case SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_DISABLED -> EnumVoiceState.DISABLED;
		case SPacketRPCResponseTypeVoiceStatus.VOICE_STATE_ENABLED -> EnumVoiceState.ENABLED;
		default -> EnumVoiceState.SERVER_DISABLE;
		});
	}

	public void handleServer(SPacketRPCResponseTypeError packet) {
		rpcContext.handleResponseError(packet.requestID, packet.errorMessage);
	}

	public void handleServer(SPacketRPCEventWebViewOpenClose packet) {
		rpcContext.fireRemoteEvent(WebViewOpenCloseEvent.create(packet.channelName, packet.channelOpen));
	}

	public void handleServer(SPacketRPCEventWebViewMessage packet) {
		switch(packet.messageType) {
		case SPacketRPCEventWebViewMessage.MESSAGE_TYPE_STRING:
			rpcContext.fireRemoteEvent(WebViewMessageEvent.string(packet.channelName, packet.messageContent));
			break;
		case SPacketRPCEventWebViewMessage.MESSAGE_TYPE_BINARY:
			rpcContext.fireRemoteEvent(WebViewMessageEvent.binary(packet.channelName, packet.messageContent));
			break;
		default:
			throw new WrongRPCPacketException("Unknown WebView message type");
		}
	}

	public void handleServer(SPacketRPCEventToggledVoice packet) {
		rpcContext.fireRemoteEvent(VoiceChangeEvent.create(mapVoiceState(packet.oldVoiceState), mapVoiceState(packet.newVoiceState)));
	}

	private EnumVoiceState mapVoiceState(int i) {
		return switch(i) {
		default -> EnumVoiceState.SERVER_DISABLE;
		case SPacketRPCEventToggledVoice.VOICE_STATE_DISABLED -> EnumVoiceState.DISABLED;
		case SPacketRPCEventToggledVoice.VOICE_STATE_ENABLED -> EnumVoiceState.ENABLED;
		};
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
