package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCResponseTypeError;

public class ServerV1RPCProtocolHandler extends ServerRPCProtocolHandler {

	public ServerV1RPCProtocolHandler(BasePlayerRPCContext<?> rpcContext) {
		super(rpcContext);
	}

	public void handleClient(CPacketRPCRequestPlayerInfo packet) {
		try {
			switch(packet.requestType) {
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REAL_UUID:
				rpcContext.handleRequestRealUUID(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REAL_IP:
				rpcContext.handleRequestRealIP(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_ORIGIN:
				rpcContext.handleRequestOrigin(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_USER_AGENT:
				rpcContext.handleRequestUserAgent(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_SKIN_DATA:
				rpcContext.handleRequestSkinData(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CAPE_DATA:
				rpcContext.handleRequestCapeData(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_COOKIE:
				rpcContext.handleRequestCookie(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_STR:
				rpcContext.handleRequestBrandOld(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_VERSION_STR:
				rpcContext.handleRequestVersionOld(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_VERSION_STR:
				rpcContext.handleRequestBrandVersionOld(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_UUID:
				rpcContext.handleRequestBrandUUID(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_VOICE_STATUS:
				rpcContext.handleRequestVoiceStatus(packet.requestID);
				break;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS:
				rpcContext.handleRequestWebViewStatus(packet.requestID);
				break;
			default:
				rpcContext.sendRPCPacket(new SPacketRPCResponseTypeError(packet.requestID, "Unknown request type"));
				break;
			}
		}catch(Exception ex) {
			rpcContext.sendRPCPacket(new SPacketRPCResponseTypeError(packet.requestID, ex.toString()));
			rpcContext.manager().handleException(ex);
		}
	}

	public void handleClient(CPacketRPCSubscribeEvents packet) {
		rpcContext.handleSetSubscribeWebViewOpenClose((packet.eventsToEnable & CPacketRPCSubscribeEvents.SUBSCRIBE_EVENT_WEBVIEW_OPEN_CLOSE) != 0);
		rpcContext.handleSetSubscribeWebViewMessage((packet.eventsToEnable & CPacketRPCSubscribeEvents.SUBSCRIBE_EVENT_WEBVIEW_MESSAGE) != 0);
		rpcContext.handleSetSubscribeToggleVoice((packet.eventsToEnable & CPacketRPCSubscribeEvents.SUBSCRIBE_EVENT_TOGGLE_VOICE) != 0);
	}

	public void handleClient(CPacketRPCSetPlayerSkin packet) {
		rpcContext.handleSetPlayerSkin(packet.skinPacket, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerCape packet) {
		rpcContext.handleSetPlayerCape(packet.capePacket, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerCookie packet) {
		rpcContext.handleSetPlayerCookie(packet.cookieData, (long) packet.expires & 0xFFFFFFFFL,
				packet.revokeQuerySupported, packet.saveToDisk);
	}

	public void handleClient(CPacketRPCSetPlayerFNAWEn packet) {
		rpcContext.handleSetPlayerFNAWEn(packet.enable, packet.force);
	}

	public void handleClient(CPacketRPCRedirectPlayer packet) {
		rpcContext.handleRedirectPlayer(packet.redirectURI);
	}

	public void handleClient(CPacketRPCResetPlayerMulti packet) {
		rpcContext.handleResetPlayerMulti(packet.resetSkin, packet.resetCape, packet.resetFNAWForce, packet.notifyOtherPlayers);
	}

	public void handleClient(CPacketRPCSendWebViewMessage packet) {
		rpcContext.handleSendWebViewMessage(packet.channelName, packet.messageType, packet.messageContent);
	}

	public void handleClient(CPacketRPCSetPauseMenuCustom packet) {
		rpcContext.handleSetPauseMenuCustom(packet);
	}

	public void handleClient(CPacketRPCNotifIconRegister packet) {
		rpcContext.handleNotifIconRegister(packet.notifIcons);
	}

	public void handleClient(CPacketRPCNotifIconRelease packet) {
		rpcContext.handleNotifIconRelease(packet.iconsToRelease);
	}

	public void handleClient(CPacketRPCNotifBadgeShow packet) {
		rpcContext.handleNotifBadgeShow(packet);
	}

	public void handleClient(CPacketRPCNotifBadgeHide packet) {
		rpcContext.handleNotifBadgeHide(packet.badgeUUID);
	}

	public void handleClient(CPacketRPCDisabled packet) {
		rpcContext.handleDisabled();
	}

	public void handleClient(CPacketRPCSendRawMessage packet) {
		rpcContext.handleSendRawMessage(packet.messageChannel, packet.messageData);
	}

}
