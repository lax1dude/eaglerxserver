package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesResult;
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
import net.lax1dude.eaglercraft.backend.server.base.supervisor.ISupervisorServiceImpl;
import net.lax1dude.eaglercraft.backend.skin_cache.ISkinCacheService;

public class SkinService<PlayerObject> implements ISkinService<PlayerObject>, ISkinsRestorerListener<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final ISkinCacheService skinCache;
	private final Predicate<String> fnawSkinsEnabled;
	private final ISupervisorServiceImpl<PlayerObject> supervisor;
	private final boolean downloadEnabled;
	private final boolean keyedLookupHelper;
	private ISkinsRestorerHelper<PlayerObject> skinsRestorerHelper;
	private IPlatformTask skinCacheTick;

	public SkinService(EaglerXServer<PlayerObject> server, ISkinCacheService skinCache,
			Predicate<String> fnawSkinsEnabled, boolean downloadEnabled) {
		this.server = server;
		this.skinCache = skinCache;
		this.fnawSkinsEnabled = fnawSkinsEnabled;
		ISupervisorServiceImpl<PlayerObject> service = server.getSupervisorService();
		this.supervisor = service.isSupervisorEnabled() ? service : null;
		this.downloadEnabled = downloadEnabled;
		this.keyedLookupHelper = downloadEnabled && supervisor == null;
	}

	@Override
	public boolean isSkinDownloadEnabled() {
		return downloadEnabled;
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
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		BasePlayerInstance<PlayerObject> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			player.getSkinManager().resolvePlayerSkin(callback);
		}else {
			if(supervisor != null) {
				supervisor.getRemoteOnlyResolver().resolvePlayerSkin(playerUUID, callback);
			}else {
				callback.accept(MissingSkin.forPlayerUUID(playerUUID));
			}
		}
	}

	@Override
	public void resolvePlayerCape(UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		BasePlayerInstance<PlayerObject> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			player.getSkinManager().resolvePlayerCape(callback);
		}else {
			if(supervisor != null) {
				supervisor.getRemoteOnlyResolver().resolvePlayerCape(playerUUID, callback);
			}else {
				callback.accept(MissingCape.MISSING_CAPE);
			}
		}
	}

	@Override
	public void loadCacheSkinFromURL(String skinURL, EnumSkinModel modelId, Consumer<IEaglerPlayerSkin> callback) {
		if(skinURL == null) {
			throw new NullPointerException("skinURL");
		}
		if(modelId == null) {
			throw new NullPointerException("modelId");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		if(downloadEnabled) {
			if(supervisor != null) {
				supervisor.getRemoteOnlyResolver().loadCacheSkinFromURL(skinURL, modelId, callback);
				return;
			}else if(skinCache != null) {
				skinCache.resolveSkinByURL(skinURL, (data) -> {
					if(data != ISkinCacheService.ERROR) {
						callback.accept(CustomSkinGeneric.createV4(modelId.getId(), data));
					}else {
						callback.accept(MissingSkin.MISSING_SKIN);
					}
				});
				return;
			}
		}
		callback.accept(MissingSkin.MISSING_SKIN);
	}

	void loadCacheSkinFromURLKeyed(SkinManagerEagler<PlayerObject> requester, String skinURL, EnumSkinModel modelId,
			Consumer<IEaglerPlayerSkin> callback) {
		if(downloadEnabled) {
			if(supervisor != null) {
				supervisor.getRemoteOnlyResolver().resolveForeignSkinKeyed(requester.player.getUniqueId(),
						modelId.getId(), skinURL, callback);
				return;
			}else if(skinCache != null) {
				Consumer<IEaglerPlayerSkin> callback2 = requester.keyedSkinLookupHelper.add(skinURL, callback);
				if(callback2 != null) {
					skinCache.resolveSkinByURL(skinURL, (data) -> {
						if(data != ISkinCacheService.ERROR) {
							callback2.accept(CustomSkinGeneric.createV4(modelId.getId(), data));
						}else {
							callback2.accept(MissingSkin.MISSING_SKIN);
						}
					});
				}
				return;
			}
		}
		callback.accept(MissingSkin.MISSING_SKIN);
	}

	void loadPlayerSkinFromURL(String skinURL, UUID playerUUID, EnumSkinModel modelId, Consumer<IEaglerPlayerSkin> callback) {
		if(downloadEnabled) {
			if(supervisor != null) {
				supervisor.getRemoteOnlyResolver().loadCacheSkinFromURL(skinURL, modelId, callback);
				return;
			}else if(skinCache != null) {
				skinCache.resolveSkinByURL(skinURL, (data) -> {
					if(data != ISkinCacheService.ERROR) {
						callback.accept(CustomSkinPlayer.createV4(playerUUID.getMostSignificantBits(),
								playerUUID.getLeastSignificantBits(), modelId.getId(), data));
					} else {
						callback.accept(MissingSkin.forPlayerUUID(playerUUID));
					}
				});
				return;
			}
		}
		callback.accept(MissingSkin.forPlayerUUID(playerUUID));
	}

	@Override
	public void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback) {
		if(capeURL == null) {
			throw new NullPointerException("capeURL");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		if(downloadEnabled) {
			if(supervisor != null) {
				supervisor.getRemoteOnlyResolver().loadCacheCapeFromURL(capeURL, callback);
				return;
			}else if(skinCache != null) {
				skinCache.resolveCapeByURL(capeURL, (data) -> {
					if(data != ISkinCacheService.ERROR) {
						callback.accept(new CustomCapeGeneric(data));
					}else {
						callback.accept(MissingCape.MISSING_CAPE);
					}
				});
				return;
			}
		}
		callback.accept(MissingCape.MISSING_CAPE);
	}

	void loadPlayerCapeFromURL(String capeURL, UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
		if(downloadEnabled) {
			if(supervisor != null) {
				supervisor.getRemoteOnlyResolver().loadCacheCapeFromURL(capeURL, callback);
				return;
			}else if(skinCache != null) {
				skinCache.resolveCapeByURL(capeURL, (data) -> {
					if(data != ISkinCacheService.ERROR) {
						callback.accept(new CustomCapePlayer(playerUUID.getMostSignificantBits(),
								playerUUID.getLeastSignificantBits(), data));
					}else {
						callback.accept(MissingCape.MISSING_CAPE);
					}
				});
				return;
			}
		}
		callback.accept(MissingCape.MISSING_CAPE);
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public ISkinImageLoader getSkinLoader(boolean cacheEnabled) {
		return cacheEnabled ? SkinImageLoaderCacheOn.INSTANCE : SkinImageLoaderCacheOff.INSTANCE;
	}

	public boolean isFNAWSkinsEnabledOnServer(String serverName) {
		return fnawSkinsEnabled.test(serverName);
	}

	public ISkinManagerBase<PlayerObject> createVanillaSkinManager(BasePlayerInstance<PlayerObject> playerInstance) {
		if(supervisor != null || skinCache != null) {
			String prop = playerInstance.getPlatformPlayer().getTexturesProperty();
			if(prop != null) {
				TexturesResult props = GameProfileUtil.extractSkinAndCape(prop);
				if(props != null) {
					return new SkinManagerVanillaOnline<PlayerObject>(playerInstance, props.getSkinURL(),
							props.getSkinModel(), props.getCapeURL());
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
			onComplete.accept(new SkinManagerEagler<>(playerInstance, skin2, cape2, true, keyedLookupHelper));
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
				onComplete.accept(handle.skinOriginal, handle.capeOriginal);
			}else {
				if(handle.skinURL == null && handle.capeURL == null) {
					handle.handleComplete(conn, handle.skin, handle.cape, onComplete);
				} else {
					(new RegisterSkinDownloader(this, conn, handle, onComplete)).run();
				}
			}
		});
	}

	public void handleEnabled() {
		if(server.getConfig().getSettings().getSkinService().isEnableSkinsRestorerApplyHook()) {
			if(skinsRestorerHelper == null) {
				skinsRestorerHelper = SkinsRestorerHelper.instance(server);
			}
			if(skinsRestorerHelper != null) {
				skinsRestorerHelper.setListener(this);
				server.logger().info("Listening for SkinsRestorer skin apply event on vanilla players");
			}
		}
		if(skinCache != null) {
			skinCacheTick = server.getPlatform().getScheduler().executeAsyncRepeatingTask(skinCache::tick, 30000l, 30000l);
		}
	}

	public void handleDisabled() {
		if(skinsRestorerHelper != null) {
			skinsRestorerHelper.setListener(null);
		}
		if(skinCacheTick != null) {
			skinCacheTick.cancel();
		}
	}

	@Override
	public void handleSRSkinApply(BasePlayerInstance<PlayerObject> player, String value, String signature) {
		if(!player.isEaglerPlayer()) {
			ISkinManagerBase<PlayerObject> skinMgr = player.getSkinManager();
			if(skinMgr instanceof ISkinManagerImpl impl) {
				impl.handleSRSkinApply(value, signature);
			}
		}
	}

}
