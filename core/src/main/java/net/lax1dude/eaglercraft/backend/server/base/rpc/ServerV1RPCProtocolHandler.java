package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;

public class ServerV1RPCProtocolHandler extends ServerRPCProtocolHandler {

	public ServerV1RPCProtocolHandler(BasePlayerRPCManager<?> rpcManager) {
		super(rpcManager);
	}

	public void handleClient(CPacketRPCRequestPlayerInfo packet) {
		switch(packet.requestType) {
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REAL_UUID:
			rpcManager.handleRequestRealUUID(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REAL_IP:
			rpcManager.handleRequestRealIP(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_ORIGIN:
			rpcManager.handleRequestOrigin(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_USER_AGENT:
			rpcManager.handleRequestUserAgent(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_SKIN_DATA:
			rpcManager.handleRequestSkinData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CAPE_DATA:
			rpcManager.handleRequestCapeData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_COOKIE:
			rpcManager.handleRequestCookie(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_STR:
			rpcManager.handleRequestBrandOld(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_VERSION_STR:
			rpcManager.handleRequestVersionOld(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_VERSION_STR:
			rpcManager.handleRequestBrandVersionOld(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_UUID:
			rpcManager.handleRequestBrandUUID(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_VOICE_STATUS:
			rpcManager.handleRequestVoiceStatus(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS:
			rpcManager.handleRequestWebViewStatus(packet.requestID);
			break;
		}
	}

	public void handleClient(CPacketRPCSubscribeEvents packet) {
		rpcManager.handleSetSubscribeWebViewOpenClose((packet.eventsToEnable & CPacketRPCSubscribeEvents.SUBSCRIBE_EVENT_WEBVIEW_OPEN_CLOSE) != 0);
		rpcManager.handleSetSubscribeWebViewMessage((packet.eventsToEnable & CPacketRPCSubscribeEvents.SUBSCRIBE_EVENT_WEBVIEW_MESSAGE) != 0);
		rpcManager.handleSetSubscribeToggleVoice((packet.eventsToEnable & CPacketRPCSubscribeEvents.SUBSCRIBE_EVENT_TOGGLE_VOICE) != 0);
	}

	public void handleClient(CPacketRPCSetPlayerSkin packet) {
		rpcManager.handleSetPlayerSkin(packet.skinPacket, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerCape packet) {
		rpcManager.handleSetPlayerCape(packet.capePacket, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerCookie packet) {
		rpcManager.handleSetPlayerCookie(packet.cookieData, packet.expires, packet.saveToDisk, packet.revokeQuerySupported);
	}

	public void handleClient(CPacketRPCSetPlayerFNAWEn packet) {
		rpcManager.handleSetPlayerFNAWEn(packet.enable, packet.force);
	}

	public void handleClient(CPacketRPCRedirectPlayer packet) {
		rpcManager.handleRedirectPlayer(packet.redirectURI);
	}

	public void handleClient(CPacketRPCResetPlayerMulti packet) {
		rpcManager.handleResetPlayerMulti(packet.resetSkin, packet.resetCape, packet.resetFNAWForce);
	}

	public void handleClient(CPacketRPCSendWebViewMessage packet) {
		rpcManager.handleSendWebViewMessage(packet.channelName, packet.messageType, packet.messageContent);
	}

	public void handleClient(CPacketRPCSetPauseMenuCustom packet) {
		rpcManager.handleSetPauseMenuCustom(packet);
	}

	public void handleClient(CPacketRPCNotifIconRegister packet) {
		rpcManager.handleNotifIconRegister(packet.notifIcons);
	}

	public void handleClient(CPacketRPCNotifIconRelease packet) {
		rpcManager.handleNotifIconRelease(packet.iconsToRelease);
	}

	public void handleClient(CPacketRPCNotifBadgeShow packet) {
		rpcManager.handleNotifBadgeShow(packet);
	}

	public void handleClient(CPacketRPCNotifBadgeHide packet) {
		rpcManager.handleNotifBadgeHide(packet.badgeUUID);
	}

	public void handleClient(CPacketRPCDisabled packet) {
		rpcManager.handleDisabled();
	}

	public void handleClient(CPacketRPCSendRawMessage packet) {
		rpcManager.handleSendRawMessage(packet.messageChannel, packet.messageData);
	}

}
