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
import net.lax1dude.eaglercraft.backend.server.base.DataSerializationContext;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.InternUtils;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingSkin;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public abstract class BasePlayerRPCContext<PlayerObject> extends SerializationContext {

	private final EaglerBackendRPCHandler packetHandler;

	BasePlayerRPCContext(EaglerBackendRPCProtocol protocol, DataSerializationContext dataCtx) {
		super(protocol, dataCtx);
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
		boolean legacy = getProtocol() == EaglerBackendRPCProtocol.V1;
		if(!legacy) {
			if(!skin.isSuccess()) {
				sendRPCPacket(new SPacketRPCResponseTypeIntegerSingleV2(requestID, -1));
				return;
			}else if(skin.isSkinPreset()) {
				int id = skin.getPresetSkinId();
				if(id == -1) {
					id = 0;
				}
				sendRPCPacket(new SPacketRPCResponseTypeIntegerSingleV2(requestID, id));
				return;
			}
		}
		sendRPCPacket(new SPacketRPCResponseTypeBytes(requestID, TextureDataHelper.encodeSkinData(skin, legacy)));
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
		if(getProtocol() != EaglerBackendRPCProtocol.V1) {
			if(!cape.isSuccess()) {
				sendRPCPacket(new SPacketRPCResponseTypeIntegerSingleV2(requestID, -1));
				return;
			}else if(cape.isCapePreset()) {
				int id = cape.getPresetCapeId();
				if(id == -1) {
					id = 0;
				}
				sendRPCPacket(new SPacketRPCResponseTypeIntegerSingleV2(requestID, id));
				return;
			}
		}
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
			completeRequestTextureData(requestID, skin, cape);
		}else {
			skinMgr.resolvePlayerTextures((resolvedSkin, resolvedCape) -> {
				completeRequestTextureData(requestID, resolvedSkin, resolvedCape);
			});
		}
	}

	private void completeRequestTextureData(int requestID, IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
		boolean a = !skin.isSuccess();
		boolean b = !cape.isSuccess();
		if((a || skin.isSkinPreset()) && (b || cape.isCapePreset())) {
			int i1, i2;
			if(a) {
				i1 = -1;
			}else {
				i1 = skin.getPresetSkinId();
				if(i1 == -1) {
					i1 = 0;
				}
			}
			if(b) {
				i2 = -1;
			}else {
				i2 = cape.getPresetCapeId();
				if(i2 == -1) {
					i2 = 0;
				}
			}
			sendRPCPacket(new SPacketRPCResponseTypeIntegerTupleV2(requestID, i1, i2));
		}else {
			sendRPCPacket(new SPacketRPCResponseTypeBytes(requestID, TextureDataHelper.encodeTexturesData(skin, cape)));
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

	void fireToggleVoice(EnumVoiceState oldVoiceState, EnumVoiceState newVoiceState) {
		
	}

	void handleSetPlayerSkin(byte[] skinPacket, boolean notifyOthers) {
		IEaglerPlayerSkin skin = TextureDataHelper.decodeSkinData(skinPacket, getProtocol() == EaglerBackendRPCProtocol.V1);
		if(skin == null) {
			throw new WrongRPCPacketException("Invalid skin texture data recieved");
		}
		manager().getPlayer().getSkinManager().changePlayerSkin(skin, notifyOthers);
	}

	void handleSetPlayerSkinPreset(int presetSkinId, boolean notifyOthers) {
		manager().getPlayer().getSkinManager().changePlayerSkin(
				presetSkinId != -1 ? InternUtils.getPresetSkin(presetSkinId) : MissingSkin.MISSING_SKIN, notifyOthers);
	}

	void handleSetPlayerCape(byte[] capePacket, boolean notifyOthers) {
		IEaglerPlayerCape cape = TextureDataHelper.decodeCapeData(capePacket, getProtocol() == EaglerBackendRPCProtocol.V1);
		if(cape == null) {
			throw new WrongRPCPacketException("Invalid cape texture data recieved");
		}
		manager().getPlayer().getSkinManager().changePlayerCape(cape, notifyOthers);
	}

	void handleSetPlayerCapePreset(int presetCapeId, boolean notifyOthers) {
		manager().getPlayer().getSkinManager().changePlayerCape(
				presetCapeId != -1 ? InternUtils.getPresetCape(presetCapeId) : MissingCape.MISSING_CAPE, notifyOthers);
	}

	void handleSetPlayerTextures(byte[] texturesPacket, boolean notifyOthers) {
		IEaglerPlayerSkin skin = TextureDataHelper.decodeTexturesSkinData(texturesPacket);
		if(skin == null) {
			throw new WrongRPCPacketException("Invalid skin texture data recieved");
		}
		IEaglerPlayerCape cape = TextureDataHelper.decodeTexturesCapeData(texturesPacket, skin);
		if(cape == null) {
			throw new WrongRPCPacketException("Invalid cape texture data recieved");
		}
		manager().getPlayer().getSkinManager().changePlayerTextures(skin, cape, notifyOthers);
	}

	void handleSetPlayerTexturesPreset(int presetSkinId, int presetCapeId, boolean notifyOthers) {
		manager().getPlayer().getSkinManager().changePlayerTextures(
				presetSkinId != -1 ? InternUtils.getPresetSkin(presetSkinId) : MissingSkin.MISSING_SKIN,
				presetCapeId != -1 ? InternUtils.getPresetCape(presetCapeId) : MissingCape.MISSING_CAPE, notifyOthers);
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
