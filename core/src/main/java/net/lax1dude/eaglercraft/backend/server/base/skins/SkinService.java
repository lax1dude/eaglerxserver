package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomCapeGeneric;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomCapePlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomSkinGeneric;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomSkinPlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingSkin;
import net.lax1dude.eaglercraft.backend.skin_cache.ISkinCacheService;

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
					callback.accept(CustomSkinGeneric.createV3(modelId.getId(), data));
				}else {
					callback.accept(MissingSkin.MISSING_SKIN);
				}
			});
		}else {
			callback.accept(MissingSkin.MISSING_SKIN);
		}
	}

	void loadPlayerSkinFromURL(String skinURL, UUID playerUUID, EnumSkinModel modelId, Consumer<IEaglerPlayerSkin> callback) {
		if(skinCache != null) {
			skinCache.resolveSkinByURL(skinURL, (data) -> {
				if(data != null) {
					callback.accept(CustomSkinPlayer.createV3(playerUUID.getMostSignificantBits(),
							playerUUID.getLeastSignificantBits(), modelId.getId(), data));
				} else {
					callback.accept(MissingSkin.forPlayerUUID(playerUUID));
				}
			});
		}else {
			callback.accept(MissingSkin.forPlayerUUID(playerUUID));
		}
	}

	@Override
	public void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback) {
		if(skinCache != null) {
			skinCache.resolveCapeByURL(capeURL, (data) -> {
				if(data != null) {
					callback.accept(new CustomCapeGeneric(data));
				}else {
					callback.accept(MissingCape.MISSING_CAPE);
				}
			});
		}else {
			callback.accept(MissingCape.MISSING_CAPE);
		}
	}

	void loadPlayerCapeFromURL(String capeURL, UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
		if(skinCache != null) {
			skinCache.resolveCapeByURL(capeURL, (data) -> {
				if(data != null) {
					callback.accept(new CustomCapePlayer(playerUUID.getMostSignificantBits(),
							playerUUID.getLeastSignificantBits(), data));
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

	@Override
	public ISkinImageLoader getSkinLoader(boolean cacheEnabled) {
		return cacheEnabled ? SkinImageLoaderCacheOn.INSTANCE : SkinImageLoaderCacheOff.INSTANCE;
	}

	public ISkinManagerBase<PlayerObject> createVanillaSkinManager(BasePlayerInstance<PlayerObject> playerInstance) {
		if(skinCache != null) {
			String prop = playerInstance.getPlatformPlayer().getTexturesProperty();
			if(prop != null) {
				GameProfileUtil props = GameProfileUtil.extractSkinAndCape(prop);
				if(props != null) {
					return new SkinManagerVanillaOnline<PlayerObject>(playerInstance, props.skinURL,
							"slim".equals(props.skinModel) ? EnumSkinModel.ALEX : EnumSkinModel.STEVE, props.capeURL);
				}
			}
		}
		return new SkinManagerVanillaOffline<PlayerObject>(playerInstance);
	}

	public void createEaglerSkinManager(EaglerPlayerInstance<PlayerObject> playerInstance,
			NettyPipelineData.ProfileDataHolder profileData, Consumer<ISkinManagerEagler<PlayerObject>> onComplete) {
		IEaglerPlayerSkin skin;
		if(profileData.skinDataV2Init != null) {
			skin = SkinHandshake.loadSkinDataV2(playerInstance.getUniqueId(), profileData.skinDataV2Init);
		}else {
			skin = SkinHandshake.loadSkinDataV1(playerInstance.getUniqueId(), profileData.skinDataV1Init);
		}
		IEaglerPlayerCape cape = SkinHandshake.loadCapeDataV1(playerInstance.getUniqueId(), profileData.capeDataInit);
		handleRegisterSkin(playerInstance, skin, cape, (skin2, cape2) -> {
			onComplete.accept(new SkinManagerEagler<>(playerInstance, skin2, cape2, true));
		});
	}

	private void handleRegisterSkin(EaglerPlayerInstance<PlayerObject> conn, IEaglerPlayerSkin skin,
			IEaglerPlayerCape cape, BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> onComplete) {
		RegisterSkinDelegate handle = new RegisterSkinDelegate(skin, cape) {
			@Override
			protected String resolveTexturesProperty() {
				return conn.getPlatformPlayer().getTexturesProperty();
			}
		};
		server.eventDispatcher().dispatchRegisterSkinEvent(conn, handle, (evt, err) -> {
			if(err != null) {
				server.logger().error("Uncaught exception in register skin event", err);
				onComplete.accept(skin, cape);
			}else {
				if(handle.skinURL == null && handle.capeURL == null) {
					onComplete.accept(handle.skin, handle.cape);
				} else {
					(new RegisterSkinDownloader(this, conn, handle, onComplete)).run();
				}
			}
		});
	}

}
