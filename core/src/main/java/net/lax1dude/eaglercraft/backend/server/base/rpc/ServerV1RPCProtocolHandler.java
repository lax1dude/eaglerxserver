package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;

public class ServerV1RPCProtocolHandler extends ServerRPCProtocolHandler {

	public ServerV1RPCProtocolHandler(BasePlayerRPCManager<?> rpcManager) {
		super(rpcManager);
	}

	public void handleClient(CPacketRPCRequestPlayerInfo packet) {
		switch(packet.requestType) {
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REAL_UUID:
			rpcManager.context().handleRequestRealUUID(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REAL_IP:
			rpcManager.context().handleRequestRealIP(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_ORIGIN:
			rpcManager.context().handleRequestOrigin(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_USER_AGENT:
			rpcManager.context().handleRequestUserAgent(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_SKIN_DATA:
			rpcManager.context().handleRequestSkinData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CAPE_DATA:
			rpcManager.context().handleRequestCapeData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_COOKIE:
			rpcManager.context().handleRequestCookie(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_STR:
			rpcManager.context().handleRequestBrandOld(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_VERSION_STR:
			rpcManager.context().handleRequestVersionOld(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_VERSION_STR:
			rpcManager.context().handleRequestBrandVersionOld(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_UUID:
			rpcManager.context().handleRequestBrandUUID(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_VOICE_STATUS:
			rpcManager.context().handleRequestVoiceStatus(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS:
			rpcManager.context().handleRequestWebViewStatus(packet.requestID);
			break;
		}
	}

	public void handleClient(CPacketRPCSubscribeEvents packet) {
		rpcManager.context().handleSetSubscribeWebViewOpenClose((packet.eventsToEnable & CPacketRPCSubscribeEvents.SUBSCRIBE_EVENT_WEBVIEW_OPEN_CLOSE) != 0);
		rpcManager.context().handleSetSubscribeWebViewMessage((packet.eventsToEnable & CPacketRPCSubscribeEvents.SUBSCRIBE_EVENT_WEBVIEW_MESSAGE) != 0);
		rpcManager.context().handleSetSubscribeToggleVoice((packet.eventsToEnable & CPacketRPCSubscribeEvents.SUBSCRIBE_EVENT_TOGGLE_VOICE) != 0);
	}

	public void handleClient(CPacketRPCSetPlayerSkin packet) {
		rpcManager.context().handleSetPlayerSkin(packet.skinPacket, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerCape packet) {
		rpcManager.context().handleSetPlayerCape(packet.capePacket, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerCookie packet) {
		rpcManager.context().handleSetPlayerCookie(packet.cookieData, packet.expires, packet.revokeQuerySupported, packet.saveToDisk);
	}

	public void handleClient(CPacketRPCSetPlayerFNAWEn packet) {
		rpcManager.context().handleSetPlayerFNAWEn(packet.enable, packet.force);
	}

	public void handleClient(CPacketRPCRedirectPlayer packet) {
		rpcManager.context().handleRedirectPlayer(packet.redirectURI);
	}

	public void handleClient(CPacketRPCResetPlayerMulti packet) {
		rpcManager.context().handleResetPlayerMulti(packet.resetSkin, packet.resetCape, packet.resetFNAWForce, packet.notifyOtherPlayers);
	}

	public void handleClient(CPacketRPCSendWebViewMessage packet) {
		rpcManager.context().handleSendWebViewMessage(packet.channelName, packet.messageType, packet.messageContent);
	}

	public void handleClient(CPacketRPCSetPauseMenuCustom packet) {
		rpcManager.context().handleSetPauseMenuCustom(packet);
	}

	public void handleClient(CPacketRPCNotifIconRegister packet) {
		rpcManager.context().handleNotifIconRegister(packet.notifIcons);
	}

	public void handleClient(CPacketRPCNotifIconRelease packet) {
		rpcManager.context().handleNotifIconRelease(packet.iconsToRelease);
	}

	public void handleClient(CPacketRPCNotifBadgeShow packet) {
		rpcManager.context().handleNotifBadgeShow(packet);
	}

	public void handleClient(CPacketRPCNotifBadgeHide packet) {
		rpcManager.context().handleNotifBadgeHide(packet.badgeUUID);
	}

	public void handleClient(CPacketRPCDisabled packet) {
		rpcManager.context().handleDisabled();
	}

	public void handleClient(CPacketRPCSendRawMessage packet) {
		rpcManager.context().handleSendRawMessage(packet.messageChannel, packet.messageData);
	}

}
