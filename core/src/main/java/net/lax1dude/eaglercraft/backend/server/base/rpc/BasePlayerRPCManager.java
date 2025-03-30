package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCNotifBadgeShow;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPauseMenuCustom;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;

public abstract class BasePlayerRPCManager<PlayerObject> {

	public interface IExceptionCallback {
		void handleException(Exception ex);
	}

	public interface IMessageHandler extends EaglerBackendRPCHandler, IExceptionCallback {
	}

	BasePlayerRPCManager() {
	}

	public abstract BasePlayerInstance<PlayerObject> getPlayer();

	public abstract boolean isEaglerPlayer();

	void handleRequestRealUUID(int requestID) {
		
	}

	void handleRequestRealIP(int requestID) {
		
	}

	void handleRequestOrigin(int requestID) {
		
	}

	void handleRequestUserAgent(int requestID) {
		
	}

	void handleRequestSkinData(int requestID) {
		
	}

	void handleRequestCapeData(int requestID) {
		
	}

	void handleRequestCookie(int requestID) {
		
	}

	void handleRequestBrandOld(int requestID) {
		
	}

	void handleRequestVersionOld(int requestID) {
		
	}

	void handleRequestBrandVersionOld(int requestID) {
		
	}

	void handleRequestBrandUUID(int requestID) {
		
	}

	void handleRequestVoiceStatus(int requestID) {
		
	}

	void handleRequestWebViewStatus(int requestID) {
		
	}

	void handleRequestTextureData(int requestID) {
		
	}

	void handleRequestBrandData(int requestID) {
		
	}

	void handleRequestMinecraftBrand(int requestID) {
		
	}

	void handleRequestAuthUsername(int requestID) {
		
	}

	void handleSetSubscribeWebViewOpenClose(boolean enable) {
		
	}

	void handleSetSubscribeWebViewMessage(boolean enable) {
		
	}

	void handleSetSubscribeToggleVoice(boolean enable) {
		
	}

	void handleSetPlayerSkin(byte[] skinPacket, boolean notifyOthers) {
		
	}

	void handleSetPlayerCape(byte[] capePacket, boolean notifyOthers) {
		
	}

	void handleSetPlayerCookie(byte[] cookieData, int expires, boolean saveToDisk, boolean revokeQuerySupported) {
		
	}

	void handleSetPlayerFNAWEn(boolean enable, boolean force) {
		
	}

	void handleRedirectPlayer(String redirectURI) {
		
	}

	void handleResetPlayerMulti(boolean resetSkin, boolean resetCape, boolean resetFNAWForce) {
		
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

	void handleSendRawMessage(String channel, byte[] data) {
		
	}

	void handleInjectRawBinaryFrame(byte[] data) {
		
	}

	void handleInjectRawEaglerFrame(int pkt, byte[] data) {
		
	}

	void handleDisabled() {
		
	}

}
