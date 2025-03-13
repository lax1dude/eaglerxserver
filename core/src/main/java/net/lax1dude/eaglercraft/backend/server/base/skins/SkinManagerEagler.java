package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.InternUtils;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketEnableFNAWSkinsEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketInvalidatePlayerCacheV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketUnforceClientV4EAG;

public class SkinManagerEagler<PlayerObject> implements ISkinManagerEagler<PlayerObject>, ISkinManagerImpl {

	private final EaglerPlayerInstance<PlayerObject> player;
	private final SkinService<PlayerObject> skinService;
	private IEaglerPlayerSkin skin;
	private IEaglerPlayerCape cape;
	private final IEaglerPlayerSkin oldSkin;
	private final IEaglerPlayerCape oldCape;
	private boolean fnawSkinsEnabled;
	private boolean fnawSkinsForced;

	SkinManagerEagler(EaglerPlayerInstance<PlayerObject> player, IEaglerPlayerSkin skin, IEaglerPlayerCape cape,
			boolean fnawSkinsEnabled) {
		this.player = player;
		this.skinService = player.getEaglerXServer().getSkinService();
		this.skin = oldSkin = skin;
		this.cape = oldCape = cape;
		this.fnawSkinsEnabled = fnawSkinsEnabled;
		this.fnawSkinsForced = false;
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	public ISkinManagerEagler<PlayerObject> asEaglerPlayer() {
		return this;
	}

	@Override
	public ISkinService<PlayerObject> getSkinService() {
		return skinService;
	}

	@Override
	public IEaglerPlayerSkin getPlayerSkinIfLoaded() {
		return getEaglerSkin();
	}

	@Override
	public IEaglerPlayerCape getPlayerCapeIfLoaded() {
		return getEaglerCape();
	}

	@Override
	public void resolvePlayerSkin(Consumer<IEaglerPlayerSkin> callback) {
		callback.accept(getEaglerSkin());
	}

	@Override
	public void resolvePlayerCape(Consumer<IEaglerPlayerCape> callback) {
		callback.accept(getEaglerCape());
	}

	@Override
	public void resolvePlayerSkinKeyed(UUID requester, Consumer<IEaglerPlayerSkin> callback) {
		callback.accept(getEaglerSkin());
	}

	@Override
	public void resolvePlayerCapeKeyed(UUID requester, Consumer<IEaglerPlayerCape> callback) {
		callback.accept(getEaglerCape());
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IEaglerPlayerSkin getEaglerSkin() {
		return skin;
	}

	@Override
	public IEaglerPlayerCape getEaglerCape() {
		return cape;
	}

	@Override
	public void changeEaglerSkin(IEaglerPlayerSkin newSkin, boolean notifyOthers) {
		if(!newSkin.equals(skin)) {
			skin = newSkin;
			if(player.getEaglerProtocol().ver >= 4) {
				if(newSkin == oldSkin) {
					player.sendEaglerMessage(new SPacketUnforceClientV4EAG(true, false, false));
				}else {
					player.sendEaglerMessage(newSkin.getForceSkinPacketV4());
				}
			}
			if(notifyOthers) {
				UUID uuid = player.getUniqueId();
				SPacketInvalidatePlayerCacheV4EAG invalidatePacket = new SPacketInvalidatePlayerCacheV4EAG(true, false,
						uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
				player.getPlatformPlayer().getServer().forEachPlayer((player) -> {
					EaglerPlayerInstance<PlayerObject> playerObj = player
							.<BasePlayerInstance<PlayerObject>>getPlayerAttachment().asEaglerPlayer();
					if (playerObj != null && playerObj != this.player) {
						playerObj.writePacket(invalidatePacket);
					}
				});
			}
		}
	}

	@Override
	public void changeEaglerSkin(EnumPresetSkins newSkin, boolean notifyOthers) {
		changeEaglerSkin(InternUtils.getPresetSkin(newSkin.getId()), notifyOthers);
	}

	@Override
	public void changeEaglerCape(IEaglerPlayerCape newCape, boolean notifyOthers) {
		if(!newCape.equals(cape)) {
			cape = newCape;
			if(player.getEaglerProtocol().ver >= 4) {
				if(newCape == oldCape) {
					player.sendEaglerMessage(new SPacketUnforceClientV4EAG(false, true, false));
				}else {
					player.sendEaglerMessage(newCape.getForceCapePacketV4());
				}
			}
			if(notifyOthers) {
				UUID uuid = player.getUniqueId();
				SPacketInvalidatePlayerCacheV4EAG invalidatePacket = new SPacketInvalidatePlayerCacheV4EAG(false, true,
						uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
				player.getPlatformPlayer().getServer().forEachPlayer((player) -> {
					EaglerPlayerInstance<PlayerObject> playerObj = player
							.<BasePlayerInstance<PlayerObject>>getPlayerAttachment().asEaglerPlayer();
					if (playerObj != null && playerObj != this.player) {
						playerObj.writePacket(invalidatePacket);
					}
				});
			}
		}
	}

	@Override
	public void changeEaglerCape(EnumPresetCapes newCape, boolean notifyOthers) {
		changeEaglerCape(InternUtils.getPresetCape(newCape.getId()), notifyOthers);
	}

	@Override
	public void resetEaglerSkin(boolean notifyOthers) {
		changeEaglerSkin(oldSkin, notifyOthers);
	}

	@Override
	public void resetEaglerCape(boolean notifyOthers) {
		changeEaglerCape(oldCape, notifyOthers);
	}

	@Override
	public void resetEaglerSkinAndCape(boolean notifyOthers) {
		resetEaglerSkinAndCape0(notifyOthers, false);
	}

	private void resetEaglerSkinAndCape0(boolean notifyOthers, boolean fnaw) {
		boolean s = !skin.equals(oldSkin), c = !cape.equals(oldCape);
		if(s || c) {
			if(s) {
				skin = oldSkin;
			}
			if(c) {
				cape = oldCape;
			}
			if(player.getEaglerProtocol().ver >= 4) {
				if(fnaw && fnawSkinsForced) {
					fnawSkinsForced = false;
					player.sendEaglerMessage(new SPacketUnforceClientV4EAG(s, c, true));
				}else {
					player.sendEaglerMessage(new SPacketUnforceClientV4EAG(s, c, false));
				}
			}else if(fnaw && fnawSkinsForced) {
				fnawSkinsForced = false;
				player.sendEaglerMessage(new SPacketEnableFNAWSkinsEAG(fnawSkinsEnabled, false));
			}
			if(notifyOthers) {
				UUID uuid = player.getUniqueId();
				SPacketInvalidatePlayerCacheV4EAG invalidatePacket = new SPacketInvalidatePlayerCacheV4EAG(s, c,
						uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
				player.getPlatformPlayer().getServer().forEachPlayer((player) -> {
					EaglerPlayerInstance<PlayerObject> playerObj = player
							.<BasePlayerInstance<PlayerObject>>getPlayerAttachment().asEaglerPlayer();
					if (playerObj != null && playerObj != this.player) {
						playerObj.writePacket(invalidatePacket);
					}
				});
			}
		}else if(fnaw && fnawSkinsForced) {
			fnawSkinsForced = false;
			player.sendEaglerMessage(new SPacketEnableFNAWSkinsEAG(fnawSkinsEnabled, false));
		}
	}

	@Override
	public boolean isClientFNAWSkinsEnabled() {
		return fnawSkinsEnabled;
	}

	@Override
	public boolean isClientFNAWSkinsForced() {
		return fnawSkinsForced;
	}

	@Override
	public void setClientFNAWSkinsEnabled(boolean enabled) {
		if(enabled != fnawSkinsEnabled) {
			fnawSkinsEnabled = enabled;
			player.sendEaglerMessage(new SPacketEnableFNAWSkinsEAG(enabled, fnawSkinsForced));
		}
	}

	@Override
	public void setClientFNAWSkinsForced(boolean forced) {
		if(forced != fnawSkinsForced) {
			fnawSkinsForced = forced;
			player.sendEaglerMessage(new SPacketEnableFNAWSkinsEAG(fnawSkinsEnabled, forced));
		}
	}

	@Override
	public void setClientFNAWSkinsEnabledForced(boolean enabled, boolean forced) {
		if(enabled != fnawSkinsEnabled || forced != fnawSkinsForced) {
			fnawSkinsEnabled = enabled;
			fnawSkinsForced = forced;
			player.sendEaglerMessage(new SPacketEnableFNAWSkinsEAG(enabled, forced));
		}
	}

	@Override
	public void resetClientFNAWSkinsForced() {
		setClientFNAWSkinsForced(false);
	}

	@Override
	public void resetEaglerSkinAndCapeAndClientFNAWSkinsForced(boolean notifyOthers) {
		resetEaglerSkinAndCape0(notifyOthers, true);
	}

	public void handlePacketGetOtherSkin(long uuidMost, long uuidLeast) {
		UUID targetUUID = new UUID(uuidMost, uuidLeast);
		BasePlayerInstance<PlayerObject> target = player.getEaglerXServer().getPlayerByUUID(targetUUID);
		if(target != null) {
			ISkinManagerImpl skinMgr = (ISkinManagerImpl) target.getSkinManager();
			IEaglerPlayerSkin skin = skinMgr.getPlayerSkinIfLoaded();
			if(skin != null) {
				player.sendEaglerMessage(skin.getSkinPacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
			}else {
				((ISkinManagerImpl)skinMgr).resolvePlayerSkinKeyed(player.getUniqueId(), (res) -> {
					player.sendEaglerMessage(res.getSkinPacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
				});
			}
		}
	}

	public void handlePacketGetOtherCape(long uuidMost, long uuidLeast) {
		UUID targetUUID = new UUID(uuidMost, uuidLeast);
		BasePlayerInstance<PlayerObject> target = player.getEaglerXServer().getPlayerByUUID(targetUUID);
		if(target != null) {
			ISkinManagerImpl skinMgr = (ISkinManagerImpl) target.getSkinManager();
			IEaglerPlayerCape skin = skinMgr.getPlayerCapeIfLoaded();
			if(skin != null) {
				player.sendEaglerMessage(skin.getCapePacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
			}else {
				skinMgr.resolvePlayerCapeKeyed(player.getUniqueId(), (res) -> {
					player.sendEaglerMessage(res.getCapePacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
				});
			}
		}
	}

	public void handlePacketGetSkinByURL(long uuidMost, long uuidLeast, String url) {
		skinService.loadCacheSkinFromURL(url, EnumSkinModel.STEVE, (res) -> {
			player.sendEaglerMessage(res.getSkinPacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
		});
	}

}
