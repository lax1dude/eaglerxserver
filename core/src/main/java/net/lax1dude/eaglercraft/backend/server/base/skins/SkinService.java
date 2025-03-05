package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingSkin;
import net.lax1dude.eaglercraft.backend.skin_cache.ISkinCacheService;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapeCustomEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SkinPacketVersionCache;

public class SkinService<PlayerObject> implements ISkinService<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final ISkinCacheService skinCache;

	public SkinService(EaglerXServer<PlayerObject> server, ISkinCacheService skinCache) {
		this.server = server;
		this.skinCache = skinCache;
	}

	@Override
	public boolean isSkinDownloadEnabled() {
		return skinCache != null;
	}

	@Override
	public IEaglerPlayerSkin getSkinNotFound(UUID playerUUID) {
		return playerUUID != null && (playerUUID.hashCode() & 1) == 1 ? MissingSkin.MISSING_SKIN_ALEX : MissingSkin.MISSING_SKIN;
	}

	@Override
	public IEaglerPlayerCape getCapeNotFound() {
		return MissingCape.MISSING_CAPE;
	}

	@Override
	public void resolvePlayerSkin(UUID playerUUID, Consumer<IEaglerPlayerSkin> callback) {
		BasePlayerInstance<PlayerObject> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			player.getSkinManager().resolvePlayerSkin(callback);
		}else {
			callback.accept(MissingSkin.forPlayerUUID(playerUUID));
			//TODO: supervisor
		}
	}

	@Override
	public void resolvePlayerCape(UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
		BasePlayerInstance<PlayerObject> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			player.getSkinManager().resolvePlayerCape(callback);
		}else {
			callback.accept(MissingCape.MISSING_CAPE);
			//TODO: supervisor
		}
	}

	@Override
	public void loadCacheSkinFromURL(String skinURL, EnumSkinModel modelId, Consumer<IEaglerPlayerSkin> callback) {
		if(skinCache != null) {
			skinCache.resolveSkinByURL(skinURL, (data) -> {
				if(data != null) {
					callback.accept(new EaglerPlayerSkin(SkinPacketVersionCache.createCustomV4(0, 0, modelId.getId(), data)));
				}else {
					callback.accept(MissingSkin.MISSING_SKIN);
				}
			});
		}else {
			callback.accept(MissingSkin.MISSING_SKIN);
		}
	}

	@Override
	public void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback) {
		if(skinCache != null) {
			skinCache.resolveCapeByURL(capeURL, (data) -> {
				if(data != null) {
					callback.accept(new EaglerPlayerCape(new SPacketOtherCapeCustomEAG(0, 0, data)));
				}else {
					callback.accept(MissingCape.MISSING_CAPE);
				}
			});
		}else {
			callback.accept(MissingCape.MISSING_CAPE);
		}
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public void setEaglerPlayersVanillaSkin(String texturesPropertyValue, String texturesPropertySignature) {
		// TODO Auto-generated method stub
		
	}

	public ISkinManagerBase<PlayerObject> createVanillaSkinManager(BasePlayerInstance<PlayerObject> playerInstance) {
		return null;
	}

	public ISkinManagerEagler<PlayerObject> createEaglerSkinManager(EaglerPlayerInstance<PlayerObject> playerInstance,
			NettyPipelineData.ProfileDataHolder profileData) {
		IEaglerPlayerSkin skin = null;
		IEaglerPlayerCape cape = null;

		if(profileData.skinDataV2Init != null) {
			skin = SkinHandshake.loadSkinDataV2(this, profileData.skinDataV2Init);
		}else if(profileData.skinDataV1Init != null) {
			skin = SkinHandshake.loadSkinDataV1(this, profileData.skinDataV1Init);
		}

		if(profileData.capeDataInit != null) {
			cape = SkinHandshake.loadCapeDataV1(this, profileData.capeDataInit);
		}

		if(skin == null) {
			skin = loadPresetSkin(0);
		}

		if(cape == null) {
			cape = loadPresetNoCape();
		}

		return new SkinManagerEagler<>(playerInstance, skin, cape, true);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData64x64(int[] pixelsARGB8, EnumSkinModel modelId) {
		return SkinImageLoader.loadSkinImageData64x64(pixelsARGB8, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData64x32(int[] pixelsARGB8, EnumSkinModel modelId) {
		return SkinImageLoader.loadSkinImageData64x32(pixelsARGB8, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(File imageFile, EnumSkinModel modelId) throws IOException {
		return SkinImageLoader.loadSkinImageData(imageFile, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(InputStream inputStream, EnumSkinModel modelId) throws IOException {
		return SkinImageLoader.loadSkinImageData(inputStream, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(BufferedImage image, EnumSkinModel modelId) {
		return SkinImageLoader.loadSkinImageData(image, modelId);
	}

	@Override
	public IEaglerPlayerSkin rewriteCustomSkinModelId(IEaglerPlayerSkin skin, EnumSkinModel modelId) {
		return SkinImageLoader.rewriteCustomSkinModelId(skin, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadPresetSkin(UUID playerUUID) {
		return SkinImageLoader.loadPresetSkin(playerUUID);
	}

	@Override
	public IEaglerPlayerSkin loadPresetSkin(EnumPresetSkins presetSkin) {
		return SkinImageLoader.loadPresetSkin(presetSkin);
	}

	@Override
	public IEaglerPlayerSkin loadPresetSkin(int presetSkin) {
		return SkinImageLoader.loadPresetSkin(presetSkin);
	}

	@Override
	public IEaglerPlayerCape loadPresetNoCape() {
		return SkinImageLoader.loadPresetNoCape();
	}

	@Override
	public IEaglerPlayerCape loadPresetCape(EnumPresetCapes presetCape) {
		return SkinImageLoader.loadPresetCape(presetCape);
	}

	@Override
	public IEaglerPlayerCape loadPresetCape(int presetCape) {
		return SkinImageLoader.loadPresetCape(presetCape);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData64x32(int[] pixelsARGB8) {
		return SkinImageLoader.loadCapeImageData64x32(pixelsARGB8);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData32x32(int[] pixelsARGB8) {
		return SkinImageLoader.loadCapeImageData32x32(pixelsARGB8);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(File imageFile) throws IOException {
		return SkinImageLoader.loadCapeImageData(imageFile);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(InputStream inputStream) throws IOException {
		return SkinImageLoader.loadCapeImageData(inputStream);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(BufferedImage image) {
		return SkinImageLoader.loadCapeImageData(image);
	}

}
