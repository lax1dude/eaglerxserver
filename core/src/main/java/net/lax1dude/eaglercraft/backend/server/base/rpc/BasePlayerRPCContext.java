package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.util.Collection;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.WrongRPCPacketException;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.*;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public abstract class BasePlayerRPCContext<PlayerObject> extends SerializationContext {

	private final EaglerBackendRPCHandler packetHandler;

	BasePlayerRPCContext(EaglerBackendRPCProtocol protocol) {
		super(protocol);
		switch(protocol) {
		case V1:
			packetHandler = new ServerV1RPCProtocolHandler(this);
			break;
		case V2:
			packetHandler = new ServerV2RPCProtocolHandler(this);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	protected abstract BasePlayerRPCManager<PlayerObject> manager();

	EaglerBackendRPCHandler packetHandler() {
		return packetHandler;
	}

	protected final void sendRPCPacket(EaglerBackendRPCPacket packet) {
		manager().sendRPCPacket(packet);
	}

	protected final RuntimeException notEaglerPlayer() {
		return new WrongRPCPacketException("Unexpected RPC operation type for non-eagler player");
	}

	void handleRequestRealUUID(int requestID) {
		sendRPCPacket(new SPacketRPCResponseTypeUUID(requestID, manager().getPlayer().getUniqueId()));
	}

	void handleRequestRealIP(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestOrigin(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestUserAgent(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestSkinData(int requestID) {
		ISkinManagerBase<PlayerObject> skinMgr = manager().getPlayer().getSkinManager();
		IEaglerPlayerSkin skin = skinMgr.getPlayerSkinIfLoaded();
		if(skin != null) {
			completeRequestSkinData(requestID, skin);
		}else {
			skinMgr.resolvePlayerSkin((resolvedSkin) -> {
				completeRequestSkinData(requestID, resolvedSkin);
			});
		}
	}

	private void completeRequestSkinData(int requestID, IEaglerPlayerSkin skin) {
		sendRPCPacket(new SPacketRPCResponseTypeBytes(requestID, TextureDataHelper.encodeSkinData(skin)));
	}

	void handleRequestCapeData(int requestID) {
		ISkinManagerBase<PlayerObject> skinMgr = manager().getPlayer().getSkinManager();
		IEaglerPlayerCape cape = skinMgr.getPlayerCapeIfLoaded();
		if(cape != null) {
			completeRequestCapeData(requestID, cape);
		}else {
			skinMgr.resolvePlayerCape((resolvedCape) -> {
				completeRequestCapeData(requestID, resolvedCape);
			});
		}
	}

	private void completeRequestCapeData(int requestID, IEaglerPlayerCape cape) {
		sendRPCPacket(new SPacketRPCResponseTypeBytes(requestID, TextureDataHelper.encodeCapeData(cape)));
	}

	void handleRequestCookie(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestBrandOld(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestVersionOld(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestBrandVersionOld(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestBrandUUID(int requestID) {
		sendRPCPacket(new SPacketRPCResponseTypeUUID(requestID, manager().getPlayer().getEaglerBrandUUID()));
	}

	void handleRequestVoiceStatus(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestWebViewStatus(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestTextureData(int requestID) {
		ISkinManagerBase<PlayerObject> skinMgr = manager().getPlayer().getSkinManager();
		IEaglerPlayerSkin skin = skinMgr.getPlayerSkinIfLoaded();
		IEaglerPlayerCape cape = skinMgr.getPlayerCapeIfLoaded();
		if(skin != null && cape != null) {
			sendRPCPacket(new SPacketRPCResponseTypeBytes(requestID, TextureDataHelper.encodeTexturesData(skin, cape)));
		}else {
			skinMgr.resolvePlayerTextures((resolvedSkin, resolvedCape) -> {
				sendRPCPacket(new SPacketRPCResponseTypeBytes(requestID,
						TextureDataHelper.encodeTexturesData(resolvedSkin, resolvedCape)));
			});
		}
	}

	void handleRequestBrandData(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestMinecraftBrand(int requestID) {
		String brand = manager().getPlayer().getMinecraftBrand();
		if(brand != null) {
			sendRPCPacket(new SPacketRPCResponseTypeString(requestID, brand));
		}else {
			sendRPCPacket(new SPacketRPCResponseTypeNull(requestID));
		}
	}

	void handleRequestAuthUsername(int requestID) {
		throw notEaglerPlayer();
	}

	void handleRequestWebViewStatusV2(int requestID) {
		throw notEaglerPlayer();
	}

	void handleSetSubscribeWebViewOpenClose(boolean enable) {
		throw notEaglerPlayer();
	}

	void fireWebViewOpenClose(boolean open, String channel) {
		
	}

	void handleSetSubscribeWebViewMessage(boolean enable) {
		throw notEaglerPlayer();
	}

	void fireWebViewMessage(String channel, boolean binary, byte[] data) {
		
	}

	void handleSetSubscribeToggleVoice(boolean enable) {
		throw notEaglerPlayer();
	}

	void fireToggleVoice(EnumVoiceState voiceState) {
		
	}

	void handleSetPlayerSkin(byte[] skinPacket, boolean notifyOthers) {
		IEaglerPlayerSkin skin = TextureDataHelper.decodeSkinData(skinPacket);
		if(skin == null) {
			throw new WrongRPCPacketException("Invalid skin texture data recieved");
		}
		manager().getPlayer().getSkinManager().changePlayerSkin(skin, notifyOthers);
	}

	void handleSetPlayerCape(byte[] capePacket, boolean notifyOthers) {
		IEaglerPlayerCape cape = TextureDataHelper.decodeCapeData(capePacket);
		if(cape == null) {
			throw new WrongRPCPacketException("Invalid cape texture data recieved");
		}
		manager().getPlayer().getSkinManager().changePlayerCape(cape, notifyOthers);
	}

	void handleSetPlayerCookie(byte[] cookieData, long expiresSec, boolean saveToDisk, boolean revokeQuerySupported) {
		throw notEaglerPlayer();
	}

	void handleSetPlayerFNAWEn(boolean enable, boolean force) {
		throw notEaglerPlayer();
	}

	void handleRedirectPlayer(String redirectURI) {
		throw notEaglerPlayer();
	}

	void handleResetPlayerMulti(boolean resetSkin, boolean resetCape, boolean resetFNAWForce, boolean notifyOthers) {
		if(resetSkin || resetCape) {
			ISkinManagerBase<PlayerObject> skinMgr = manager().getPlayer().getSkinManager();
			if(resetSkin && resetCape) {
				skinMgr.resetPlayerTextures(notifyOthers);
			}else {
				if(resetSkin) {
					skinMgr.resetPlayerSkin(notifyOthers);
				}else {
					skinMgr.resetPlayerCape(notifyOthers);
				}
			}
		}
	}

	void handleSendWebViewMessage(String channelName, int messageType, byte[] messageContent) {
		throw notEaglerPlayer();
	}

	void handleSetPauseMenuCustom(CPacketRPCSetPauseMenuCustom packet) {
		throw notEaglerPlayer();
	}

	void handleNotifIconRegister(Collection<CPacketRPCNotifIconRegister.RegisterIcon> notifIcons) {
		throw notEaglerPlayer();
	}

	void handleNotifIconRelease(Collection<UUID> icons) {
		throw notEaglerPlayer();
	}

	void handleNotifBadgeShow(CPacketRPCNotifBadgeShow packet) {
		throw notEaglerPlayer();
	}

	void handleNotifBadgeHide(UUID badge) {
		throw notEaglerPlayer();
	}

	void handleSendRawMessage(String channel, byte[] data) {
		manager().getPlayer().getPlatformPlayer().sendDataClient(channel, data);
	}

	void handleInjectRawBinaryFrame(byte[] data) {
		throw notEaglerPlayer();
	}

	void handleDisabled() {
		manager().handleDisabled();
	}

}
