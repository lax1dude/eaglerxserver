package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.InternUtils;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.PresetCapePlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.PresetSkinPlayer;
import net.lax1dude.eaglercraft.backend.server.util.KeyedConcurrentLazyLoader.KeyedConsumerList;

public class SkinManagerVanillaOnline<PlayerObject> implements ISkinManagerBase<PlayerObject>, ISkinManagerImpl {

	private final BasePlayerInstance<PlayerObject> player;
	private final String skinURL;
	private final EnumSkinModel skinModel;
	private final String capeURL;

	private IEaglerPlayerSkin skin = null;
	private KeyedConsumerList<UUID, IEaglerPlayerSkin> waitingSkinCallbacks = null;

	private IEaglerPlayerCape cape = null;
	private final Object capeLock = new Object();
	private KeyedConsumerList<UUID, IEaglerPlayerCape> waitingCapeCallbacks = null;

	private IEaglerPlayerSkin originalSkin = null;
	private IEaglerPlayerCape originalCape = null;

	SkinManagerVanillaOnline(BasePlayerInstance<PlayerObject> player, String skinURL, EnumSkinModel skinModel, String capeURL) {
		this.player = player;
		UUID uuid = player.getUniqueId();
		this.skinURL = skinURL;
		this.skinModel = skinURL != null ? skinModel : null;
		this.skin = skinURL == null
				? new PresetSkinPlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
						(uuid.hashCode() & 1) != 0 ? 1 : 0)
				: null;
		this.capeURL = capeURL;
		this.cape = capeURL == null
				? new PresetCapePlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), 0)
				: null;
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
	public void resolvePlayerTextures(BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> callback) {
		resolvePlayerTexturesKeyed(null, callback);
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
						if(originalSkin == null) {
							this.originalSkin = skin;
						}
						return; // ignore multiple results
					}
					this.skin = skin;
					this.originalSkin = skin;
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
						if(originalCape == null) {
							this.originalCape = cape;
						}
						return; // ignore multiple results
					}
					this.cape = cape;
					this.originalCape = cape;
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

	@Override
	public void resolvePlayerTexturesKeyed(UUID requester, BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> callback) {
		IEaglerPlayerSkin val1 = skin;
		IEaglerPlayerCape val2 = cape;
		if(val1 != null && val2 != null) {
			callback.accept(val1, val2);
		}else {
			new MultiSkinResolver<SkinManagerVanillaOnline<PlayerObject>, PlayerObject>(this, this, val1, val2, requester) {
				@Override
				protected void onComplete(SkinManagerVanillaOnline<PlayerObject> mgr, IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
					callback.accept(skin, cape);
				}
			};
		}
	}

	@Override
	public void changePlayerSkin(IEaglerPlayerSkin newSkin, boolean notifyOthers) {
		changePlayerSkin0(newSkin, notifyOthers);
	}

	private void changePlayerSkin0(IEaglerPlayerSkin newSkin, boolean notifyOthers) {
		KeyedConsumerList<UUID, IEaglerPlayerSkin> toCall = null;
		synchronized(this) {
			if(newSkin != null) {
				IEaglerPlayerSkin oldSkin = skin;
				if(oldSkin != null && newSkin.equals(oldSkin)) {
					return;
				}
				skin = newSkin;
				toCall = waitingSkinCallbacks;
				waitingSkinCallbacks = null;
			}else {
				if(originalSkin != null) {
					IEaglerPlayerSkin oldSkin = skin;
					if(oldSkin != null && originalSkin.equals(oldSkin)) {
						return;
					}
					skin = originalSkin;
					newSkin = originalSkin;
					toCall = waitingSkinCallbacks;
					waitingSkinCallbacks = null;
				}else {
					if(skinURL == null || skin == null) {
						return;
					}
					skin = null;
				}
			}
		}
		if(notifyOthers) {
			SkinManagerHelper.notifyOthers(player, true, false);
		}
		if(toCall != null) {
			List<Consumer<IEaglerPlayerSkin>> toCallList = toCall.getList();
			for(int i = 0, l = toCallList.size(); i < l; ++i) {
				try {
					toCallList.get(i).accept(newSkin);
				}catch(Exception ex) {
					player.getEaglerXServer().logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	@Override
	public void changePlayerSkin(EnumPresetSkins newSkin, boolean notifyOthers) {
		changePlayerSkin(InternUtils.getPresetSkin(newSkin.getId()), notifyOthers);
	}

	@Override
	public void changePlayerCape(IEaglerPlayerCape newCape, boolean notifyOthers) {
		changePlayerCape0(newCape, notifyOthers);
	}

	private void changePlayerCape0(IEaglerPlayerCape newCape, boolean notifyOthers) {
		KeyedConsumerList<UUID, IEaglerPlayerCape> toCall = null;
		synchronized(capeLock) {
			if(newCape != null) {
				IEaglerPlayerCape oldCape = cape;
				if(oldCape != null && newCape.equals(oldCape)) {
					return;
				}
				cape = newCape;
				toCall = waitingCapeCallbacks;
				waitingCapeCallbacks = null;
			}else {
				if(originalCape != null) {
					IEaglerPlayerCape oldCape = cape;
					if(oldCape != null && originalCape.equals(oldCape)) {
						return;
					}
					cape = originalCape;
					newCape = originalCape;
					toCall = waitingCapeCallbacks;
					waitingCapeCallbacks = null;
				}else {
					if(capeURL == null || cape == null) {
						return;
					}
					cape = null;
				}
			}
		}
		if(notifyOthers) {
			SkinManagerHelper.notifyOthers(player, false, true);
		}
		if(toCall != null) {
			List<Consumer<IEaglerPlayerCape>> toCallList = toCall.getList();
			for(int i = 0, l = toCallList.size(); i < l; ++i) {
				try {
					toCallList.get(i).accept(newCape);
				}catch(Exception ex) {
					player.getEaglerXServer().logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	@Override
	public void changePlayerCape(EnumPresetCapes newCape, boolean notifyOthers) {
		changePlayerCape(InternUtils.getPresetCape(newCape.getId()), notifyOthers);
	}

	@Override
	public void changePlayerTextures(IEaglerPlayerSkin newSkin, IEaglerPlayerCape newCape, boolean notifyOthers) {
		changePlayerTextures0(newSkin, newCape, notifyOthers);
	}

	private void changePlayerTextures0(IEaglerPlayerSkin newSkin, IEaglerPlayerCape newCape, boolean notifyOthers) {
		boolean c = false, s = false;
		KeyedConsumerList<UUID, IEaglerPlayerSkin> toCall1 = null;
		KeyedConsumerList<UUID, IEaglerPlayerCape> toCall2 = null;
		synchronized(this) {
			if(newSkin != null) {
				IEaglerPlayerSkin oldSkin = skin;
				if(oldSkin == null || !newSkin.equals(oldSkin)) {
					s = true;
					skin = newSkin;
					toCall1 = waitingSkinCallbacks;
					waitingSkinCallbacks = null;
				}
			}else {
				if(originalSkin != null) {
					IEaglerPlayerSkin oldSkin = skin;
					if(oldSkin == null || !originalSkin.equals(oldSkin)) {
						s = true;
						skin = originalSkin;
						newSkin = originalSkin;
						toCall1 = waitingSkinCallbacks;
						waitingSkinCallbacks = null;
					}
				}else {
					if(skinURL != null && skin != null) {
						skin = null;
						s = true;
					}
				}
			}
		}
		synchronized(capeLock) {
			if(newCape != null) {
				IEaglerPlayerCape oldCape = cape;
				if(oldCape == null || !newCape.equals(oldCape)) {
					c = true;
					cape = newCape;
					toCall2 = waitingCapeCallbacks;
					waitingCapeCallbacks = null;
				}
			}else {
				if(originalCape != null) {
					IEaglerPlayerCape oldCape = cape;
					if(oldCape == null || !originalCape.equals(oldCape)) {
						c = true;
						cape = originalCape;
						newCape = originalCape;
						toCall2 = waitingCapeCallbacks;
						waitingCapeCallbacks = null;
					}
				}else {
					if(capeURL != null && cape != null) {
						cape = null;
						c = true;
					}
				}
			}
		}
		if(notifyOthers && (s || c)) {
			SkinManagerHelper.notifyOthers(player, s, c);
		}
		if(toCall1 != null) {
			List<Consumer<IEaglerPlayerSkin>> toCallList = toCall1.getList();
			for(int i = 0, l = toCallList.size(); i < l; ++i) {
				try {
					toCallList.get(i).accept(newSkin);
				}catch(Exception ex) {
					player.getEaglerXServer().logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
		if(toCall2 != null) {
			List<Consumer<IEaglerPlayerCape>> toCallList = toCall2.getList();
			for(int i = 0, l = toCallList.size(); i < l; ++i) {
				try {
					toCallList.get(i).accept(newCape);
				}catch(Exception ex) {
					player.getEaglerXServer().logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	@Override
	public void changePlayerTextures(EnumPresetSkins newSkin, EnumPresetCapes newCape, boolean notifyOthers) {
		changePlayerTextures(InternUtils.getPresetSkin(newSkin.getId()), InternUtils.getPresetCape(newCape.getId()), notifyOthers);
	}

	@Override
	public void resetPlayerSkin(boolean notifyOthers) {
		changePlayerSkin0(null, notifyOthers);
	}

	@Override
	public void resetPlayerCape(boolean notifyOthers) {
		changePlayerCape0(null, notifyOthers);
	}

	@Override
	public void resetPlayerTextures(boolean notifyOthers) {
		changePlayerTextures0(null, null, notifyOthers);
	}

	public String getEffectiveSkinURLInternal() {
		if(skin == null || originalSkin == skin) {
			return skinURL;
		}else {
			return null;
		}
	}

	public EnumSkinModel getEffectiveSkinModelInternal() {
		return skinModel;
	}

	public String getEffectiveCapeURLInternal() {
		if(cape == null || originalCape == cape) {
			return capeURL;
		}else {
			return null;
		}
	}

}
