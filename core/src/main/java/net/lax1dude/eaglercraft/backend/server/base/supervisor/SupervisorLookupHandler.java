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

package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.skins.SkinManagerVanillaOnline;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.UnsafeUtil;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvOtherCapeCustom;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvOtherCapePreset;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvOtherCapeURL;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvOtherSkinCustom;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvOtherSkinPreset;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvOtherSkinURL;

public class SupervisorLookupHandler<PlayerObject> {

	private final SupervisorService<PlayerObject> service;
	private final SupervisorConnection connection;

	SupervisorLookupHandler(SupervisorService<PlayerObject> service, SupervisorConnection connection) {
		this.service = service;
		this.connection = connection;
	}

	void handleSupervisorSkinLookup(UUID uuid) {
		BasePlayerInstance<PlayerObject> player = service.getEaglerXServer().getPlayerByUUID(uuid);
		if(player != null) {
			ISkinManagerBase<PlayerObject> skinMgr = player.getSkinManager();
			if(skinMgr.isEaglerPlayer()) {
				sendSkinResponse(uuid, skinMgr.getPlayerSkinIfLoaded());
			}else {
				if(skinMgr instanceof SkinManagerVanillaOnline<PlayerObject> vanillaOnlineMgr) {
					String url;
					int mdl = 0;
					IEaglerPlayerSkin skin = null;
					synchronized(skinMgr) {
						if((url = vanillaOnlineMgr.getEffectiveSkinURLInternal()) == null) {
							skin = skinMgr.getPlayerSkinIfLoaded();
						}else {
							mdl = vanillaOnlineMgr.getEffectiveSkinModelInternal().getId();
						}
					}
					if(url != null) {
						connection.sendSupervisorPacket(new CPacketSvOtherSkinURL(uuid, mdl, url));
					}else if(skin != null) {
						sendSkinResponse(uuid, skin);
					}else {
						throw new IllegalStateException("SkinManagerVanillaOnline skin is in a bad state for: " + uuid + " ("
								+ player.getUsername() + ")");
					}
				}else {
					IEaglerPlayerSkin skin = skinMgr.getPlayerSkinIfLoaded();
					if(skin != null) {
						sendSkinResponse(uuid, skin);
					}else {
						skinMgr.resolvePlayerSkin((skin2) -> {
							sendSkinResponse(uuid, skin2);
						});
					}
				}
			}
		}else {
			service.logger().warn("Received skin lookup request from supervisor for unknown player: " + uuid);
			connection.sendSupervisorPacket(new CPacketSvOtherSkinPreset(uuid, (uuid.hashCode() & 1) != 0 ? 1 : 0));
		}
	}

	private void sendSkinResponse(UUID uuid, IEaglerPlayerSkin skin) {
		if (skin.isSkinPreset()) {
			connection.sendSupervisorPacket(new CPacketSvOtherSkinPreset(uuid, skin.getPresetSkinId()));
		} else {
			connection.sendSupervisorPacket(new CPacketSvOtherSkinCustom(uuid,
					skin.getCustomSkinModelId().getId(), UnsafeUtil.unsafeGetPixelsDirect(skin)));
		}
	}

	void handleSupervisorCapeLookup(UUID uuid) {
		BasePlayerInstance<PlayerObject> player = service.getEaglerXServer().getPlayerByUUID(uuid);
		if(player != null) {
			ISkinManagerBase<PlayerObject> skinMgr = player.getSkinManager();
			if(skinMgr.isEaglerPlayer()) {
				sendCapeResponse(uuid, skinMgr.getPlayerCapeIfLoaded());
			}else {
				if(skinMgr instanceof SkinManagerVanillaOnline<PlayerObject> vanillaOnlineMgr) {
					String url;
					IEaglerPlayerCape cape = null;
					synchronized(vanillaOnlineMgr.capeLock) {
						if((url = vanillaOnlineMgr.getEffectiveCapeURLInternal()) == null) {
							cape = skinMgr.getPlayerCapeIfLoaded();
						}
					}
					if(url != null) {
						connection.sendSupervisorPacket(new CPacketSvOtherCapeURL(uuid, url));
					}else if(cape != null) {
						sendCapeResponse(uuid, cape);
					}else {
						throw new IllegalStateException("SkinManagerVanillaOnline cape is in a bad state for: " + uuid + " ("
								+ player.getUsername() + ")");
					}
				}else {
					IEaglerPlayerCape cape = skinMgr.getPlayerCapeIfLoaded();
					if(cape != null) {
						sendCapeResponse(uuid, cape);
					}else {
						skinMgr.resolvePlayerCape((cape2) -> {
							sendCapeResponse(uuid, cape2);
						});
					}
				}
			}
		}else {
			service.logger().warn("Received skin lookup request from supervisor for unknown player: " + uuid);
			connection.sendSupervisorPacket(new CPacketSvOtherCapePreset(uuid, (uuid.hashCode() & 1) != 0 ? 1 : 0));
		}
	}

	private void sendCapeResponse(UUID uuid, IEaglerPlayerCape cape) {
		if (cape.isCapePreset()) {
			connection.sendSupervisorPacket(new CPacketSvOtherCapePreset(uuid, cape.getPresetCapeId()));
		} else {
			connection.sendSupervisorPacket(new CPacketSvOtherCapeCustom(uuid, UnsafeUtil.unsafeGetPixelsDirect(cape)));
		}
	}

}
