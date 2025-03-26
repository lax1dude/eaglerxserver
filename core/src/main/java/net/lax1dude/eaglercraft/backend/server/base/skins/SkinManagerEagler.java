package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW;
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
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketEnableFNAWSkinsEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapeCustomEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherTexturesV5EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketUnforceClientV4EAG;

public class SkinManagerEagler<PlayerObject> implements ISkinManagerEagler<PlayerObject>, ISkinManagerImpl {

	private final EaglerPlayerInstance<PlayerObject> player;
	private final SkinService<PlayerObject> skinService;
	private IEaglerPlayerSkin skin;
	private IEaglerPlayerCape cape;
	private final IEaglerPlayerSkin oldSkin;
	private final IEaglerPlayerCape oldCape;
	private EnumEnableFNAW fnawSkinsEnabled;
	private boolean fnawSkinsManaged = true;

	SkinManagerEagler(EaglerPlayerInstance<PlayerObject> player, IEaglerPlayerSkin skin, IEaglerPlayerCape cape,
			boolean fnawSkinsEnabled) {
		this.player = player;
		this.skinService = player.getEaglerXServer().getSkinService();
		this.skin = oldSkin = skin;
		this.cape = oldCape = cape;
		this.fnawSkinsEnabled = fnawSkinsEnabled ? EnumEnableFNAW.ENABLED : EnumEnableFNAW.DISABLED;
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
	public void changePlayerSkin(IEaglerPlayerSkin newSkin, boolean notifyOthers) {
		if(!newSkin.equals(skin)) {
			skin = newSkin;
			if(player.getEaglerProtocol().ver >= 4) {
				if(newSkin.equals(oldSkin)) {
					player.sendEaglerMessage(new SPacketUnforceClientV4EAG(true, false, false));
				}else {
					player.sendEaglerMessage(newSkin.getForceSkinPacketV4());
				}
			}
			if(notifyOthers) {
				SkinManagerHelper.notifyOthers(player, true, false);
			}
		}
	}

	@Override
	public void changePlayerSkin(EnumPresetSkins newSkin, boolean notifyOthers) {
		changePlayerSkin(InternUtils.getPresetSkin(newSkin.getId()), notifyOthers);
	}

	@Override
	public void changePlayerCape(IEaglerPlayerCape newCape, boolean notifyOthers) {
		if(!newCape.equals(cape)) {
			cape = newCape;
			if(player.getEaglerProtocol().ver >= 4) {
				if(newCape.equals(oldCape)) {
					player.sendEaglerMessage(new SPacketUnforceClientV4EAG(false, true, false));
				}else {
					player.sendEaglerMessage(newCape.getForceCapePacketV4());
				}
			}
			if(notifyOthers) {
				SkinManagerHelper.notifyOthers(player, false, true);
			}
		}
	}

	@Override
	public void changePlayerCape(EnumPresetCapes newCape, boolean notifyOthers) {
		changePlayerCape(InternUtils.getPresetCape(newCape.getId()), notifyOthers);
	}

	@Override
	public void resetPlayerSkin(boolean notifyOthers) {
		changePlayerSkin(oldSkin, notifyOthers);
	}

	@Override
	public void resetPlayerCape(boolean notifyOthers) {
		changePlayerCape(oldCape, notifyOthers);
	}

	@Override
	public void resetPlayerSkinAndCape(boolean notifyOthers) {
		boolean s = !oldSkin.equals(skin), c = !oldCape.equals(cape);
		if(s || c) {
			if(s) {
				skin = oldSkin;
			}
			if(c) {
				cape = oldCape;
			}
			if(player.getEaglerProtocol().ver >= 4) {
				player.sendEaglerMessage(new SPacketUnforceClientV4EAG(s, c, false));
			}
			if(notifyOthers) {
				SkinManagerHelper.notifyOthers(player, s, c);
			}
		}
	}

	@Override
	public EnumEnableFNAW getEnableFNAWSkins() {
		return fnawSkinsEnabled;
	}

	@Override
	public void setEnableFNAWSkins(EnumEnableFNAW enabled) {
		EnumEnableFNAW oldState = fnawSkinsEnabled;
		if(oldState != enabled) {
			fnawSkinsEnabled = enabled;
			player.sendEaglerMessage(new SPacketEnableFNAWSkinsEAG(enabled != EnumEnableFNAW.DISABLED,
					enabled == EnumEnableFNAW.FORCED));
		}
	}

	@Override
	public void resetEnableFNAWSkins() {
		setEnableFNAWSkins(EnumEnableFNAW.ENABLED);
	}

	@Override
	public boolean isFNAWSkinsServerManaged() {
		return fnawSkinsManaged;
	}

	@Override
	public void setFNAWSkinsServerManaged(boolean managed) {
		fnawSkinsManaged = managed;
	}

	public void handleServerChanged(String serverName) {
		if(fnawSkinsManaged) {
			setEnableFNAWSkins(skinService.isFNAWSkinsEnabledOnServer(serverName) ? EnumEnableFNAW.ENABLED
					: EnumEnableFNAW.DISABLED);
		}
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
				skinMgr.resolvePlayerSkinKeyed(player.getUniqueId(), (res) -> {
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

	public void handlePacketGetTextures(long uuidMost, long uuidLeast) {
		UUID targetUUID = new UUID(uuidMost, uuidLeast);
		BasePlayerInstance<PlayerObject> target = player.getEaglerXServer().getPlayerByUUID(targetUUID);
		if(target != null) {
			ISkinManagerImpl skinMgr = (ISkinManagerImpl) target.getSkinManager();
			IEaglerPlayerSkin skin = skinMgr.getPlayerSkinIfLoaded();
			IEaglerPlayerCape cape = skinMgr.getPlayerCapeIfLoaded();
			if(skin != null && cape != null) {
				player.sendEaglerMessage(createV5Textures(uuidMost, uuidLeast, skin, cape));
			}else {
				new MultiSkinResolver(skin, cape, skinMgr, player.getUniqueId(), uuidMost, uuidLeast);
			}
		}
	}

	private class MultiSkinResolver {

		private final long uuidMost;
		private final long uuidLeast;
		private final AtomicInteger countDown = new AtomicInteger(2);
		private volatile IEaglerPlayerSkin skin;
		private volatile IEaglerPlayerCape cape;

		protected MultiSkinResolver(IEaglerPlayerSkin skin, IEaglerPlayerCape cape, ISkinManagerImpl skinMgr, UUID uuid,
				long uuidMost, long uuidLeast) {
			this.uuidMost = uuidMost;
			this.uuidLeast = uuidLeast;
			if(skin == null) {
				skinMgr.resolvePlayerSkinKeyed(uuid, (res) -> {
					this.skin = res;
					countDown();
				});
			}else {
				this.skin = skin;
				countDown();
			}
			if(cape == null) {
				skinMgr.resolvePlayerCapeKeyed(uuid, (res) -> {
					this.cape = res;
					countDown();
				});
			}else {
				this.cape = cape;
				countDown();
			}
		}

		private void countDown() {
			if(countDown.decrementAndGet() == 0) {
				player.sendEaglerMessage(createV5Textures(uuidMost, uuidLeast, skin, cape));
			}
		}

	}

	private SPacketOtherTexturesV5EAG createV5Textures(long uuidMost, long uuidLeast, IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
		int skinID = 0;
		byte[] customSkin = null;
		int capeID = 0;
		byte[] customCape = null;
		if(skin.isSkinPreset()) {
			skinID = skin.getPresetSkinId() & 0x7FFFFFFF;
		}else {
			SPacketOtherSkinCustomV4EAG cs = (SPacketOtherSkinCustomV4EAG) skin.getSkinPacket(uuidMost, uuidLeast, GamePluginMessageProtocol.V4);
			skinID = -cs.modelID - 1;
			customSkin = cs.customSkin;
		}
		if(cape.isCapePreset()) {
			capeID = cape.getPresetCapeId() & 0x7FFFFFFF;
		}else {
			SPacketOtherCapeCustomEAG cs = (SPacketOtherCapeCustomEAG) cape.getCapePacket(uuidMost, uuidLeast, GamePluginMessageProtocol.V4);
			capeID = -1;
			customCape = cs.customCape;
		}
		return new SPacketOtherTexturesV5EAG(uuidMost, uuidLeast, skinID, customSkin, capeID, customCape);
	}

}
