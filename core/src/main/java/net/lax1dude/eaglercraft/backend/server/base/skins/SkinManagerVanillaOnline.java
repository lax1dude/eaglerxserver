package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.util.KeyedConcurrentLazyLoader.KeyedConsumerList;

public class SkinManagerVanillaOnline<PlayerObject> implements ISkinManagerBase<PlayerObject>, ISkinManagerImpl {

	private final BasePlayerInstance<PlayerObject> player;
	private final String skinURL;
	private final EnumSkinModel skinModel;
	private final String capeURL;

	private volatile IEaglerPlayerSkin skin = null;
	private KeyedConsumerList<UUID, IEaglerPlayerSkin> waitingSkinCallbacks = null;

	private volatile IEaglerPlayerCape cape = null;
	private final Object capeLock = new Object();
	private KeyedConsumerList<UUID, IEaglerPlayerCape> waitingCapeCallbacks = null;

	SkinManagerVanillaOnline(BasePlayerInstance<PlayerObject> player, String skinURL, EnumSkinModel skinModel, String capeURL) {
		this.player = player;
		this.skinURL = skinURL;
		this.skinModel = skinURL != null ? skinModel : null;
		this.skin = skinURL == null ? getSkinService().loadPresetSkin(player.getUniqueId()) : null;
		this.capeURL = capeURL;
		this.cape = capeURL == null ? getSkinService().loadPresetNoCape() : null;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public ISkinService<PlayerObject> getSkinService() {
		return player.getEaglerXServer().getSkinService();
	}

	@Override
	public boolean isEaglerPlayer() {
		return false;
	}

	@Override
	public ISkinManagerEagler<PlayerObject> asEaglerPlayer() {
		return null;
	}

	@Override
	public IEaglerPlayerSkin getPlayerSkinIfLoaded() {
		return skin;
	}

	@Override
	public IEaglerPlayerCape getPlayerCapeIfLoaded() {
		return cape;
	}

	@Override
	public void resolvePlayerSkin(Consumer<IEaglerPlayerSkin> callback) {
		resolvePlayerSkinKeyed(null, callback);
	}

	@Override
	public void resolvePlayerCape(Consumer<IEaglerPlayerCape> callback) {
		resolvePlayerCapeKeyed(null, callback);
	}

	@Override
	public void resolvePlayerSkinKeyed(UUID requester, Consumer<IEaglerPlayerSkin> callback) {
		IEaglerPlayerSkin val = skin;
		if(val != null) {
			callback.accept(val);
		}else {
			eag: synchronized(this) {
				val = skin;
				if(val != null) {
					break eag;
				}
				if(waitingSkinCallbacks == null) {
					waitingSkinCallbacks = new KeyedConsumerList<>();
					waitingSkinCallbacks.add(requester, callback);
				}else {
					waitingSkinCallbacks.add(requester, callback);
					return;
				}
			}
			if(val != null) {
				callback.accept(val);
				return;
			}
			getSkinService().loadCacheSkinFromURL(skinURL, skinModel, (skin) -> {
				KeyedConsumerList<UUID, IEaglerPlayerSkin> toCall;
				synchronized(this) {
					if(this.skin != null) {
						return; // ignore multiple results
					}
					this.skin = skin;
					toCall = waitingSkinCallbacks;
					waitingSkinCallbacks = null;
				}
				if(toCall != null) {
					List<Consumer<IEaglerPlayerSkin>> toCallList = toCall.getList();
					for(int i = 0, l = toCallList.size(); i < l; ++i) {
						try {
							toCallList.get(i).accept(skin);
						}catch(Exception ex) {
							player.getEaglerXServer().logger().error("Caught error from lazy load callback", ex);
						}
					}
				}
			});
		}
	}

	@Override
	public void resolvePlayerCapeKeyed(UUID requester, Consumer<IEaglerPlayerCape> callback) {
		IEaglerPlayerCape val = cape;
		if(val != null) {
			callback.accept(val);
		}else {
			eag: synchronized(capeLock) {
				val = cape;
				if(val != null) {
					break eag;
				}
				if(waitingCapeCallbacks == null) {
					waitingCapeCallbacks = new KeyedConsumerList<>();
					waitingCapeCallbacks.add(requester, callback);
				}else {
					waitingCapeCallbacks.add(requester, callback);
					return;
				}
			}
			if(val != null) {
				callback.accept(val);
				return;
			}
			getSkinService().loadCacheCapeFromURL(capeURL, (cape) -> {
				KeyedConsumerList<UUID, IEaglerPlayerCape> toCall;
				synchronized(capeLock) {
					if(this.cape != null) {
						return; // ignore multiple results
					}
					this.cape = cape;
					toCall = waitingCapeCallbacks;
					waitingCapeCallbacks = null;
				}
				if(toCall != null) {
					List<Consumer<IEaglerPlayerCape>> toCallList = toCall.getList();
					for(int i = 0, l = toCallList.size(); i < l; ++i) {
						try {
							toCallList.get(i).accept(cape);
						}catch(Exception ex) {
							player.getEaglerXServer().logger().error("Caught error from lazy load callback", ex);
						}
					}
				}
			});
		}
	}

}
