package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCNotifBadgeShow;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPauseMenuCustom;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCResponseTypeCookie;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCResponseTypeNull;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCResponseTypeString;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData;

public class EaglerPlayerRPCContext<PlayerObject> extends BasePlayerRPCContext<PlayerObject> {

	protected final EaglerPlayerRPCManager<PlayerObject> manager;

	EaglerPlayerRPCContext(EaglerPlayerRPCManager<PlayerObject> manager) {
		this.manager = manager;
	}

	@Override
	protected EaglerPlayerRPCManager<PlayerObject> manager() {
		return manager;
	}

	void handleRequestRealIP(int requestID) {
		String realIP = manager().getPlayer().getRealAddress();
		if(realIP != null) {
			sendRPCPacket(new SPacketRPCResponseTypeString(requestID, realIP));
		}else {
			sendRPCPacket(new SPacketRPCResponseTypeNull(requestID));
		}
	}

	void handleRequestOrigin(int requestID) {
		String origin = manager().getPlayer().getWebSocketHeader(EnumWebSocketHeader.HEADER_ORIGIN);
		if(origin != null) {
			sendRPCPacket(new SPacketRPCResponseTypeString(requestID, origin));
		}else {
			sendRPCPacket(new SPacketRPCResponseTypeNull(requestID));
		}
	}

	void handleRequestUserAgent(int requestID) {
		String origin = manager().getPlayer().getWebSocketHeader(EnumWebSocketHeader.HEADER_USER_AGENT);
		if(origin != null) {
			sendRPCPacket(new SPacketRPCResponseTypeString(requestID, origin));
		}else {
			sendRPCPacket(new SPacketRPCResponseTypeNull(requestID));
		}
	}

	void handleRequestCookie(int requestID) {
		EaglerPlayerInstance<PlayerObject> player = manager().getPlayer();
		sendRPCPacket(new SPacketRPCResponseTypeCookie(requestID, player.isCookieEnabled(), player.getCookieData()));
	}

	void handleRequestBrandOld(int requestID) {
		sendRPCPacket(new SPacketRPCResponseTypeString(requestID, manager().getPlayer().getEaglerBrandString()));
	}

	void handleRequestVersionOld(int requestID) {
		sendRPCPacket(new SPacketRPCResponseTypeString(requestID, manager().getPlayer().getEaglerVersionString()));
	}

	void handleRequestBrandVersionOld(int requestID) {
		EaglerPlayerInstance<PlayerObject> player = manager().getPlayer();
		sendRPCPacket(new SPacketRPCResponseTypeString(requestID, player.getEaglerBrandString() + " " + player.getEaglerVersionString()));
	}

	void handleRequestVoiceStatus(int requestID) {
		
	}

	void handleRequestWebViewStatus(int requestID) {
		
	}

	void handleRequestBrandData(int requestID) {
		
	}

	void handleRequestAuthUsername(int requestID) {
		
	}

	void handleSetSubscribeWebViewOpenClose(boolean enable) {
		
	}

	void handleSetSubscribeWebViewMessage(boolean enable) {
		
	}

	void handleSetSubscribeToggleVoice(boolean enable) {
		
	}

	void handleSetPlayerCookie(byte[] cookieData, int expires, boolean saveToDisk, boolean revokeQuerySupported) {
		
	}

	void handleSetPlayerFNAWEn(boolean enable, boolean force) {
		
	}

	void handleRedirectPlayer(String redirectURI) {
		
	}

	void handleSendWebViewMessage(String channelName, int messageType, byte[] messageContent) {
		
	}

	void handleSetPauseMenuCustom(CPacketRPCSetPauseMenuCustom packet) {
		
	}

	void handleNotifIconRegister(Map<UUID, PacketImageData> icons) {
		
	}

	void handleNotifIconRelease(Collection<UUID> icons) {
		
	}

	void handleNotifBadgeShow(CPacketRPCNotifBadgeShow packet) {
		
	}

	void handleNotifBadgeHide(UUID badge) {
		
	}

	void handleInjectRawBinaryFrame(byte[] data) {
		
	}

	void handleInjectRawEaglerFrame(int pkt, byte[] data) {
		
	}

}
