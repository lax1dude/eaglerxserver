/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
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
import net.lax1dude.eaglercraft.backend.server.base.PlayerRateLimits;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.InternUtils;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingSkin;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.UnsafeUtil;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.ISupervisorServiceImpl;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketEnableFNAWSkinsEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherTexturesV5EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketUnforceClientV4EAG;

public class SkinManagerEagler<PlayerObject> implements ISkinManagerEagler<PlayerObject>, ISkinManagerImpl {

	final EaglerPlayerInstance<PlayerObject> player;
	private final SkinService<PlayerObject> skinService;
	private IEaglerPlayerSkin skin;
	private IEaglerPlayerCape cape;
	private final IEaglerPlayerSkin oldSkin;
	private final IEaglerPlayerCape oldCape;
	private EnumEnableFNAW fnawSkinsEnabled;
	final KeyedRequestHelper<IEaglerPlayerSkin> keyedSkinLookupHelper;
	private boolean fnawSkinsManaged = true;

	SkinManagerEagler(EaglerPlayerInstance<PlayerObject> player, IEaglerPlayerSkin skin, IEaglerPlayerCape cape,
			boolean fnawSkinsEnabled, boolean keyedLookupHelper) {
		this.player = player;
		this.skinService = player.getEaglerXServer().getSkinService();
		this.skin = oldSkin = skin;
		this.cape = oldCape = cape;
		this.fnawSkinsEnabled = fnawSkinsEnabled ? EnumEnableFNAW.ENABLED : EnumEnableFNAW.DISABLED;
		this.keyedSkinLookupHelper = keyedLookupHelper ? new KeyedRequestHelper<>() : null;
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
	public void resolvePlayerTextures(BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> callback) {
		callback.accept(getEaglerSkin(), getEaglerCape());
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
	public void resolvePlayerTexturesKeyed(UUID requester, BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> callback) {
		callback.accept(getEaglerSkin(), getEaglerCape());
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
		if (newSkin == null) {
			throw new NullPointerException("newSkin");
		}
		if (!newSkin.equals(skin)) {
			skin = newSkin;
			int pv = player.getEaglerProtocol().ver;
			if (pv >= 4) {
				if (pv >= 5 && newSkin.equals(oldSkin)) {
					// This was added in protocol V4, but doesn't always work correctly pre-u51
					// So we're only gonna send it to protocol V5 clients and up
					player.sendEaglerMessage(new SPacketUnforceClientV4EAG(true, false, false));
				} else {
					player.sendEaglerMessage(newSkin.getForceSkinPacketV4());
				}
			}
			if (notifyOthers) {
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
		if (newCape == null) {
			throw new NullPointerException("newCape");
		}
		if (!newCape.equals(cape)) {
			cape = newCape;
			int pv = player.getEaglerProtocol().ver;
			if (pv >= 4) {
				if (pv >= 5 && newCape.equals(oldCape)) {
					// This was added in protocol V4, but doesn't always work correctly pre-u51
					// So we're only gonna send it to protocol V5 clients and up
					player.sendEaglerMessage(new SPacketUnforceClientV4EAG(false, true, false));
				} else {
					player.sendEaglerMessage(newCape.getForceCapePacketV4());
				}
			}
			if (notifyOthers) {
				SkinManagerHelper.notifyOthers(player, false, true);
			}
		}
	}

	@Override
	public void changePlayerCape(EnumPresetCapes newCape, boolean notifyOthers) {
		changePlayerCape(InternUtils.getPresetCape(newCape.getId()), notifyOthers);
	}

	@Override
	public void changePlayerTextures(IEaglerPlayerSkin newSkin, IEaglerPlayerCape newCape, boolean notifyOthers) {
		if (newSkin == null) {
			throw new NullPointerException("newSkin");
		}
		if (newCape == null) {
			throw new NullPointerException("newCape");
		}
		boolean s = !newSkin.equals(skin), c = !newCape.equals(cape);
		if (s || c) {
			if (s) {
				skin = newSkin;
			}
			if (c) {
				cape = newCape;
			}
			int pv = player.getEaglerProtocol().ver;
			if (pv >= 4) {
				boolean ufs = false, ufc = false;
				if (s) {
					if (pv >= 5 && newSkin.equals(oldSkin)) {
						ufs = true;
					} else {
						player.sendEaglerMessage(newSkin.getForceSkinPacketV4());
					}
				}
				if (c) {
					if (pv >= 5 && newCape.equals(oldCape)) {
						ufc = true;
					} else {
						player.sendEaglerMessage(newCape.getForceCapePacketV4());
					}
				}
				if (ufs || ufc) {
					player.sendEaglerMessage(new SPacketUnforceClientV4EAG(ufs, ufc, false));
				}
			}
			if (notifyOthers) {
				SkinManagerHelper.notifyOthers(player, s, c);
			}
		}
	}

	@Override
	public void changePlayerTextures(EnumPresetSkins newSkin, EnumPresetCapes newCape, boolean notifyOthers) {
		changePlayerTextures(InternUtils.getPresetSkin(newSkin.getId()), InternUtils.getPresetCape(newCape.getId()),
				notifyOthers);
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
	public void resetPlayerTextures(boolean notifyOthers) {
		changePlayerTextures(oldSkin, oldCape, notifyOthers);
	}

	@Override
	public EnumEnableFNAW getEnableFNAWSkins() {
		return fnawSkinsEnabled;
	}

	@Override
	public void setEnableFNAWSkins(EnumEnableFNAW enabled) {
		if (enabled == null) {
			throw new NullPointerException("enabled");
		}
		EnumEnableFNAW oldState = fnawSkinsEnabled;
		if (oldState != enabled) {
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

	public void handleServerPostConnect(String serverName) {
		if (fnawSkinsManaged) {
			setEnableFNAWSkins(skinService.isFNAWSkinsEnabledOnServer(serverName) ? EnumEnableFNAW.ENABLED
					: EnumEnableFNAW.DISABLED);
		}
	}

	public void handlePacketGetOtherSkin(long uuidMost, long uuidLeast) {
		PlayerRateLimits rateLimits = player.getRateLimits();
		if (!rateLimits.ratelimitSkin()) {
			return;
		}
		UUID targetUUID = new UUID(uuidMost, uuidLeast);
		BasePlayerInstance<PlayerObject> target = player.getEaglerXServer().getPlayerByUUID(targetUUID);
		if (target != null) {
			ISkinManagerImpl skinMgr = (ISkinManagerImpl) target.getSkinManager();
			IEaglerPlayerSkin skin = skinMgr.getPlayerSkinIfLoaded();
			if (skin != null) {
				player.sendEaglerMessage(skin.getSkinPacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
			} else {
				skinMgr.resolvePlayerSkinKeyed(player.getUniqueId(), (res) -> {
					player.sendEaglerMessage(res.getSkinPacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
				});
			}
		} else {
			ISupervisorServiceImpl<PlayerObject> supervisorService = player.getEaglerXServer().getSupervisorService();
			if (supervisorService.isSupervisorEnabled() && !supervisorService.shouldIgnoreUUID(targetUUID)) {
				if (!rateLimits.checkSvSkinAntagonist()) {
					return;
				}
				supervisorService.getRemoteOnlyResolver().resolvePlayerSkinKeyed(player.getUniqueId(), targetUUID, (res) -> {
					if (res != MissingSkin.UNAVAILABLE_SKIN) {
						if (!res.isSuccess()) {
							player.getRateLimits().ratelimitSvSkinAntagonist();
						}
						player.sendEaglerMessage(
								res.getSkinPacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
					}
				});
			} else {
				player.sendEaglerMessage(MissingSkin.forPlayerUUID(targetUUID).getSkinPacket(uuidMost, uuidLeast,
						player.getEaglerProtocol()));
			}
		}
	}

	public void handlePacketGetOtherCape(long uuidMost, long uuidLeast) {
		PlayerRateLimits rateLimits = player.getRateLimits();
		if (!rateLimits.ratelimitCape()) {
			return;
		}
		UUID targetUUID = new UUID(uuidMost, uuidLeast);
		BasePlayerInstance<PlayerObject> target = player.getEaglerXServer().getPlayerByUUID(targetUUID);
		if (target != null) {
			ISkinManagerImpl skinMgr = (ISkinManagerImpl) target.getSkinManager();
			IEaglerPlayerCape skin = skinMgr.getPlayerCapeIfLoaded();
			if (skin != null) {
				player.sendEaglerMessage(skin.getCapePacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
			} else {
				skinMgr.resolvePlayerCapeKeyed(player.getUniqueId(), (res) -> {
					player.sendEaglerMessage(res.getCapePacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
				});
			}
		} else {
			ISupervisorServiceImpl<PlayerObject> supervisorService = player.getEaglerXServer().getSupervisorService();
			if (supervisorService.isSupervisorEnabled() && !supervisorService.shouldIgnoreUUID(targetUUID)) {
				if (!rateLimits.checkSvSkinAntagonist()) {
					return;
				}
				supervisorService.getRemoteOnlyResolver().resolvePlayerCapeKeyed(player.getUniqueId(), targetUUID, (res) -> {
					if (res != MissingCape.UNAVAILABLE_CAPE) {
						if (!res.isSuccess()) {
							player.getRateLimits().ratelimitSvSkinAntagonist();
						}
						player.sendEaglerMessage(
								res.getCapePacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
					}
				});
			} else {
				player.sendEaglerMessage(
						MissingCape.MISSING_CAPE.getCapePacket(uuidMost, uuidLeast, player.getEaglerProtocol()));
			}
		}
	}

	public void handlePacketGetSkinByURL(long uuidMost, long uuidLeast, String url) {
		PlayerRateLimits rateLimits = player.getRateLimits();
		if (!rateLimits.ratelimitSkin()) {
			return;
		}
		if (!rateLimits.checkSkinAntagonist()) {
			return;
		}
		skinService.loadCacheSkinFromURLKeyed(this, url, EnumSkinModel.STEVE, (res) -> {
			if (res != MissingSkin.UNAVAILABLE_SKIN) {
				if (!res.isSuccess()) {
					player.getRateLimits().ratelimitSkinAntagonist();
				}
				player.sendEaglerMessage(res.getSkinPacket(uuidMost, uuidLeast, 0xFF, player.getEaglerProtocol()));
			}
		});
	}

	public void handlePacketGetOtherSkinV5(int requestId, long uuidMost, long uuidLeast) {
		PlayerRateLimits rateLimits = player.getRateLimits();
		if (!rateLimits.ratelimitSkin()) {
			return;
		}
		UUID targetUUID = new UUID(uuidMost, uuidLeast);
		BasePlayerInstance<PlayerObject> target = player.getEaglerXServer().getPlayerByUUID(targetUUID);
		if (target != null) {
			ISkinManagerImpl skinMgr = (ISkinManagerImpl) target.getSkinManager();
			IEaglerPlayerSkin skin = skinMgr.getPlayerSkinIfLoaded();
			if (skin != null) {
				player.sendEaglerMessage(skin.getSkinPacket(requestId, player.getEaglerProtocol()));
			} else {
				skinMgr.resolvePlayerSkinKeyed(player.getUniqueId(), (res) -> {
					player.sendEaglerMessage(res.getSkinPacket(requestId, player.getEaglerProtocol()));
				});
			}
		} else {
			ISupervisorServiceImpl<PlayerObject> supervisorService = player.getEaglerXServer().getSupervisorService();
			if (supervisorService.isSupervisorEnabled() && !supervisorService.shouldIgnoreUUID(targetUUID)) {
				if (!rateLimits.checkSvSkinAntagonist()) {
					return;
				}
				supervisorService.getRemoteOnlyResolver().resolvePlayerSkinKeyed(player.getUniqueId(), targetUUID, (res) -> {
					if (res != MissingSkin.UNAVAILABLE_SKIN) {
						if (!res.isSuccess()) {
							player.getRateLimits().ratelimitSvSkinAntagonist();
						}
						player.sendEaglerMessage(res.getSkinPacket(requestId, player.getEaglerProtocol()));
					}
				});
			} else {
				player.sendEaglerMessage(
						MissingSkin.forPlayerUUID(targetUUID).getSkinPacket(requestId, player.getEaglerProtocol()));
			}
		}
	}

	public void handlePacketGetOtherCapeV5(int requestId, long uuidMost, long uuidLeast) {
		PlayerRateLimits rateLimits = player.getRateLimits();
		if (!rateLimits.ratelimitCape()) {
			return;
		}
		UUID targetUUID = new UUID(uuidMost, uuidLeast);
		BasePlayerInstance<PlayerObject> target = player.getEaglerXServer().getPlayerByUUID(targetUUID);
		if (target != null) {
			ISkinManagerImpl skinMgr = (ISkinManagerImpl) target.getSkinManager();
			IEaglerPlayerCape skin = skinMgr.getPlayerCapeIfLoaded();
			if (skin != null) {
				player.sendEaglerMessage(skin.getCapePacket(requestId, player.getEaglerProtocol()));
			} else {
				skinMgr.resolvePlayerCapeKeyed(player.getUniqueId(), (res) -> {
					player.sendEaglerMessage(res.getCapePacket(requestId, player.getEaglerProtocol()));
				});
			}
		} else {
			ISupervisorServiceImpl<PlayerObject> supervisorService = player.getEaglerXServer().getSupervisorService();
			if (supervisorService.isSupervisorEnabled() && !supervisorService.shouldIgnoreUUID(targetUUID)) {
				if (!rateLimits.checkSvSkinAntagonist()) {
					return;
				}
				supervisorService.getRemoteOnlyResolver().resolvePlayerCapeKeyed(player.getUniqueId(), targetUUID, (res) -> {
					if (res != MissingCape.UNAVAILABLE_CAPE) {
						if (!res.isSuccess()) {
							player.getRateLimits().ratelimitSvSkinAntagonist();
						}
						player.sendEaglerMessage(res.getCapePacket(requestId, player.getEaglerProtocol()));
					}
				});
			} else {
				player.sendEaglerMessage(MissingCape.MISSING_CAPE.getCapePacket(requestId, player.getEaglerProtocol()));
			}
		}
	}

	public void handlePacketGetSkinByURLV5(int requestId, String url) {
		PlayerRateLimits rateLimits = player.getRateLimits();
		if (!rateLimits.ratelimitSkin()) {
			return;
		}
		if (!rateLimits.checkSkinAntagonist()) {
			return;
		}
		skinService.loadCacheSkinFromURLKeyed(this, url, EnumSkinModel.STEVE, (res) -> {
			if (res != MissingSkin.UNAVAILABLE_SKIN) {
				if (!res.isSuccess()) {
					player.getRateLimits().ratelimitSkinAntagonist();
				}
				player.sendEaglerMessage(res.getSkinPacket(requestId, 0xFF, player.getEaglerProtocol()));
			}
		});
	}

	public void handlePacketGetTexturesV5(int requestId, long uuidMost, long uuidLeast) {
		PlayerRateLimits rateLimits = player.getRateLimits();
		if (!rateLimits.ratelimitSkin()) {
			return;
		}
		UUID targetUUID = new UUID(uuidMost, uuidLeast);
		BasePlayerInstance<PlayerObject> target = player.getEaglerXServer().getPlayerByUUID(targetUUID);
		if (target != null) {
			ISkinManagerImpl skinMgr = (ISkinManagerImpl) target.getSkinManager();
			IEaglerPlayerSkin skin = skinMgr.getPlayerSkinIfLoaded();
			IEaglerPlayerCape cape = skinMgr.getPlayerCapeIfLoaded();
			if (skin != null && cape != null) {
				player.sendEaglerMessage(createV5Textures(requestId, skin, cape));
			} else {
				new MultiSkinResolver<SkinManagerEagler<PlayerObject>, PlayerObject>(this, skinMgr, skin, cape,
						player.getUniqueId()) {
					@Override
					protected void onComplete(SkinManagerEagler<PlayerObject> mgr, IEaglerPlayerSkin skin,
							IEaglerPlayerCape cape) {
						mgr.player.sendEaglerMessage(mgr.createV5Textures(requestId, skin, cape));
					}
				};
			}
		} else {
			ISupervisorServiceImpl<PlayerObject> supervisorService = player.getEaglerXServer().getSupervisorService();
			if (supervisorService.isSupervisorEnabled() && !supervisorService.shouldIgnoreUUID(targetUUID)) {
				if (!rateLimits.checkSvSkinAntagonist()) {
					return;
				}
				new MultiSvSkinResolver<SkinManagerEagler<PlayerObject>, PlayerObject>(this,
						supervisorService.getRemoteOnlyResolver(), targetUUID, player.getUniqueId()) {
					@Override
					protected void onComplete(SkinManagerEagler<PlayerObject> mgr, IEaglerPlayerSkin skin,
							IEaglerPlayerCape cape) {
						if (skin != MissingSkin.UNAVAILABLE_SKIN && cape != MissingCape.UNAVAILABLE_CAPE) {
							if (!skin.isSuccess() && !cape.isSuccess()) {
								mgr.player.getRateLimits().ratelimitSvSkinAntagonist();
							}
							mgr.player.sendEaglerMessage(mgr.createV5Textures(requestId, skin, cape));
						}
					}
				};
			}
		}
	}

	public void handlePacketGetSignedSkinV5(int requestId, SHA1Sum checksum) {
		PlayerRateLimits rateLimits = player.getRateLimits();
		if (!rateLimits.ratelimitSkin()) {
			return;
		}

	}

	public void callbackSignedSkin(int requestId, String skinURL) {
		skinService.loadCacheSkinFromURLKeyed(this, skinURL, EnumSkinModel.STEVE, (res) -> {
			if (res != MissingSkin.UNAVAILABLE_SKIN) {
				player.sendEaglerMessage(res.getSkinPacket(requestId, player.getEaglerProtocol()));
			}
		});
	}

	public void callbackInvalidSignedSkin(int requestId) {
		player.getRateLimits().ratelimitSkinAntagonist();
		player.sendEaglerMessage(MissingSkin.MISSING_SKIN.getSkinPacket(requestId, player.getEaglerProtocol()));
	}

	SPacketOtherTexturesV5EAG createV5Textures(int requestId, IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
		int skinID = 0;
		byte[] customSkin = null;
		int capeID = 0;
		byte[] customCape = null;
		if (skin.isSkinPreset()) {
			skinID = skin.getPresetSkinId() & 0x7FFFFFFF;
		} else {
			skinID = -skin.getCustomSkinRawModelId() - 1;
			customSkin = UnsafeUtil.unsafeGetPixelsDirect(skin);
		}
		if (cape.isCapePreset()) {
			capeID = cape.getPresetCapeId() & 0x7FFFFFFF;
		} else {
			capeID = -1;
			customCape = UnsafeUtil.unsafeGetPixelsDirect(cape);
		}
		return new SPacketOtherTexturesV5EAG(requestId, skinID, customSkin, capeID, customCape);
	}

	@Override
	public void handleSRSkinApply(String value, String signature) {
	}

}
