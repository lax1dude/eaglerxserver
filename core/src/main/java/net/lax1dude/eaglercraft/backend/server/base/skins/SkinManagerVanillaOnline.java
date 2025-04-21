package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
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
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesResult;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.InternUtils;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.PresetCapePlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.PresetSkinPlayer;
import net.lax1dude.eaglercraft.backend.server.util.KeyedConcurrentLazyLoader.KeyedConsumerList;

public class SkinManagerVanillaOnline<PlayerObject> implements ISkinManagerBase<PlayerObject>, ISkinManagerImpl {

	private static final VarHandle SKIN_HANDLE;
	private static final VarHandle CAPE_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			SKIN_HANDLE = l.findVarHandle(SkinManagerVanillaOnline.class, "skin", IEaglerPlayerSkin.class);
			CAPE_HANDLE = l.findVarHandle(SkinManagerVanillaOnline.class, "cape", IEaglerPlayerCape.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final BasePlayerInstance<PlayerObject> player;
	private String skinURL;
	private EnumSkinModel skinModel;
	private String capeURL;

	private IEaglerPlayerSkin skin = null;
	private KeyedConsumerList<UUID, IEaglerPlayerSkin> waitingSkinCallbacks = null;

	private IEaglerPlayerCape cape = null;
	public final Object capeLock = new Object();
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
		return (IEaglerPlayerSkin) SKIN_HANDLE.getAcquire(this);
	}

	@Override
	public IEaglerPlayerCape getPlayerCapeIfLoaded() {
		return (IEaglerPlayerCape) SKIN_HANDLE.getAcquire(this);
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
		IEaglerPlayerSkin val = (IEaglerPlayerSkin) SKIN_HANDLE.getAcquire(this);
		if(val != null) {
			callback.accept(val);
		}else {
			KeyedConsumerList<UUID, IEaglerPlayerSkin> expected = null;
			eag: synchronized(this) {
				val = skin;
				if(val != null) {
					break eag;
				}
				if(waitingSkinCallbacks == null) {
					waitingSkinCallbacks = expected = new KeyedConsumerList<>();
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
			KeyedConsumerList<UUID, IEaglerPlayerSkin> expectedFinal = expected;
			getSkinService().loadCacheSkinFromURL(skinURL, skinModel, (skin) -> {
				KeyedConsumerList<UUID, IEaglerPlayerSkin> toCall;
				synchronized(this) {
					toCall = waitingSkinCallbacks;
					if(toCall != null && toCall != expectedFinal) {
						return; // ignore multiple results
					}
					if(this.skin != null) {
						if(originalSkin == null) {
							this.originalSkin = skin;
						}
						return; // ignore multiple results
					}
					SKIN_HANDLE.setRelease(this, skin);
					this.originalSkin = skin;
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
		IEaglerPlayerCape val = (IEaglerPlayerCape) CAPE_HANDLE.getAcquire(this);
		if(val != null) {
			callback.accept(val);
		}else {
			KeyedConsumerList<UUID, IEaglerPlayerCape> expected = null;
			eag: synchronized(capeLock) {
				val = cape;
				if(val != null) {
					break eag;
				}
				if(waitingCapeCallbacks == null) {
					waitingCapeCallbacks = expected = new KeyedConsumerList<>();
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
			KeyedConsumerList<UUID, IEaglerPlayerCape> expectedFinal = expected;
			getSkinService().loadCacheCapeFromURL(capeURL, (cape) -> {
				KeyedConsumerList<UUID, IEaglerPlayerCape> toCall;
				synchronized(capeLock) {
					toCall = waitingCapeCallbacks;
					if(toCall != null && toCall != expectedFinal) {
						return; // ignore multiple results
					}
					if(this.cape != null) {
						if(originalCape == null) {
							this.originalCape = cape;
						}
						return; // ignore multiple results
					}
					CAPE_HANDLE.setRelease(this, cape);
					this.originalCape = cape;
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
		IEaglerPlayerSkin val1 = (IEaglerPlayerSkin) SKIN_HANDLE.getAcquire(this);
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
				SKIN_HANDLE.setRelease(this, newSkin);
				toCall = waitingSkinCallbacks;
				waitingSkinCallbacks = null;
			}else {
				if(originalSkin != null) {
					IEaglerPlayerSkin oldSkin = skin;
					if(oldSkin != null && originalSkin.equals(oldSkin)) {
						return;
					}
					SKIN_HANDLE.setRelease(this, originalSkin);
					newSkin = originalSkin;
					toCall = waitingSkinCallbacks;
					waitingSkinCallbacks = null;
				}else {
					if(skinURL == null || skin == null) {
						return;
					}
					SKIN_HANDLE.setRelease(this, null);
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
				CAPE_HANDLE.setRelease(this, newCape);
				toCall = waitingCapeCallbacks;
				waitingCapeCallbacks = null;
			}else {
				if(originalCape != null) {
					IEaglerPlayerCape oldCape = cape;
					if(oldCape != null && originalCape.equals(oldCape)) {
						return;
					}
					CAPE_HANDLE.setRelease(this, originalCape);
					newCape = originalCape;
					toCall = waitingCapeCallbacks;
					waitingCapeCallbacks = null;
				}else {
					if(capeURL == null || cape == null) {
						return;
					}
					CAPE_HANDLE.setRelease(this, null);
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
					SKIN_HANDLE.setRelease(this, newSkin);
					toCall1 = waitingSkinCallbacks;
					waitingSkinCallbacks = null;
				}
			}else {
				if(originalSkin != null) {
					IEaglerPlayerSkin oldSkin = skin;
					if(oldSkin == null || !originalSkin.equals(oldSkin)) {
						s = true;
						SKIN_HANDLE.setRelease(this, originalSkin);
						newSkin = originalSkin;
						toCall1 = waitingSkinCallbacks;
						waitingSkinCallbacks = null;
					}
				}else {
					if(skinURL != null && skin != null) {
						SKIN_HANDLE.setRelease(this, null);
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
					CAPE_HANDLE.setRelease(this, newCape);
					toCall2 = waitingCapeCallbacks;
					waitingCapeCallbacks = null;
				}
			}else {
				if(originalCape != null) {
					IEaglerPlayerCape oldCape = cape;
					if(oldCape == null || !originalCape.equals(oldCape)) {
						c = true;
						CAPE_HANDLE.setRelease(this, originalCape);
						newCape = originalCape;
						toCall2 = waitingCapeCallbacks;
						waitingCapeCallbacks = null;
					}
				}else {
					if(capeURL != null && cape != null) {
						CAPE_HANDLE.setRelease(this, null);
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

	@Override
	public void handleSRSkinApply(String value, String signature) {
		TexturesResult textures = GameProfileUtil.extractSkinAndCape(value);
		if(textures != null) {
			KeyedConsumerList<UUID, IEaglerPlayerSkin> toCall1 = null;
			KeyedConsumerList<UUID, IEaglerPlayerCape> toCall2 = null;
			boolean s = false, c = false;
			String skinUrl = textures.getSkinURL();
			if(skinUrl != null) {
				synchronized(this) {
					originalSkin = null;
					if(skinURL == null || !skinUrl.equals(skinURL)) {
						s = true;
					}
					SKIN_HANDLE.setRelease(this, null);
					skinURL = skinUrl;
					skinModel = textures.getSkinModel();
					if(skinModel == null) {
						skinModel = EnumSkinModel.STEVE;
					}
					toCall1 = waitingSkinCallbacks;
					waitingSkinCallbacks = null;
				}
			}else {
				UUID uuid = player.getUniqueId();
				IEaglerPlayerSkin newSkin = new PresetSkinPlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
						(uuid.hashCode() & 1) != 0 ? 1 : 0);
				synchronized(this) {
					originalSkin = newSkin;
					if(skin == null || !newSkin.equals(skin)) {
						s = true;
					}
					SKIN_HANDLE.setRelease(this, newSkin);
					skinURL = null;
					skinModel = null;
					toCall1 = waitingSkinCallbacks;
					waitingSkinCallbacks = null;
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
					toCall1 = null;
				}
			}
			String capeUrl = textures.getCapeURL();
			if(capeUrl != null) {
				synchronized(capeLock) {
					originalCape = null;
					if(capeURL == null || !capeUrl.equals(capeURL)) {
						c = true;
					}
					CAPE_HANDLE.setRelease(this, null);
					capeURL = capeUrl;
					toCall2 = waitingCapeCallbacks;
					waitingCapeCallbacks = null;
				}
			}else {
				UUID uuid = player.getUniqueId();
				IEaglerPlayerCape newCape = new PresetCapePlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), 0);
				synchronized(capeLock) {
					originalCape = newCape;
					if(cape == null || !newCape.equals(cape)) {
						c = true;
					}
					CAPE_HANDLE.setRelease(this, newCape);
					capeURL = null;
					toCall2 = waitingCapeCallbacks;
					waitingCapeCallbacks = null;
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
					toCall2 = null;
				}
			}
			if(s || c) {
				SkinManagerHelper.notifyOthers(player, s, c);
			}
			if(toCall1 != null) {
				toCall1.forEach(this::resolvePlayerSkinKeyed);
			}
			if(toCall2 != null) {
				toCall2.forEach(this::resolvePlayerCapeKeyed);
			}
		}
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
