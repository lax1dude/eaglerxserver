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

package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerPlayerInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayerInitializer;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistry;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.EnumAcceptPlayer;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.ISupervisorServiceImpl;

class EaglerXServerPlayerInitializer<PlayerObject> implements
		IEaglerXServerPlayerInitializer<NettyPipelineData, BasePlayerInstance<PlayerObject>, PlayerObject> {

	private final EaglerXServer<PlayerObject> server;

	EaglerXServerPlayerInitializer(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializePlayer(
			IPlatformPlayerInitializer<NettyPipelineData, BasePlayerInstance<PlayerObject>, PlayerObject> initializer) {
		NettyPipelineData pipelineData = initializer.getPipelineAttachment();
		NettyPipelineData.ProfileDataHolder profileData;
		if (pipelineData == null || !pipelineData.isEaglerPlayer()) {
			pipelineData = null;
			profileData = null;
		} else {
			profileData = pipelineData.profileDataHelper();
		}
		IPlatformPlayer<PlayerObject> platformPlayer = initializer.getPlayer();
		ISupervisorServiceImpl<PlayerObject> supervisor = server.getSupervisorService();
		if (supervisor.isSupervisorEnabled()) {
			supervisor.acceptPlayer(platformPlayer.getUniqueId(),
					pipelineData != null ? profileData.brandUUID : IBrandRegistry.BRAND_VANILLA,
					pipelineData != null ? pipelineData.minecraftProtocol
							: initializer.getPlayer().getMinecraftProtocol(),
					pipelineData != null ? pipelineData.handshakeProtocol : 0, platformPlayer.getUsername(), (res) -> {
				if (res == EnumAcceptPlayer.ACCEPT) {
					server.getPlatform().getScheduler().executeAsync(() -> {
						acceptPlayer(initializer, profileData);
					});
				} else {
					switch (res) {
					case REJECT_ALREADY_WAITING:
					case REJECT_DUPLICATE_USERNAME:
					case REJECT_DUPLICATE_UUID:
						try {
							platformPlayer.disconnect(server.componentHelper().getStandardKickAlreadyPlaying());
						} finally {
							initializer.cancel();
						}
						break;
					case REJECT_UNKNOWN:
					default:
						try {
							platformPlayer.disconnect(server.componentBuilder().buildTextComponent()
									.text("Internal Supervisor Error").end());
						} finally {
							initializer.cancel();
						}
						break;
					case SUPERVISOR_UNAVAILABLE:
						try {
							platformPlayer.disconnect(server.componentBuilder().buildTextComponent()
									.text(server.getConfig().getSupervisor().getSupervisorUnavailableMessage())
									.end());
						} finally {
							initializer.cancel();
						}
						break;
					}
				}
			});
		} else {
			acceptPlayer(initializer, profileData);
		}
	}

	private void acceptPlayer(
			IPlatformPlayerInitializer<NettyPipelineData, BasePlayerInstance<PlayerObject>, PlayerObject> initializer,
			NettyPipelineData.ProfileDataHolder profileData) {
		NettyPipelineData conn = initializer.getPipelineAttachment();
		if (conn != null && conn.isEaglerPlayer()) {
			EaglerPlayerInstance<PlayerObject> instance = new EaglerPlayerInstance<>(initializer.getPlayer(), conn,
					profileData.brandUUID);
			initializer.setPlayerAttachment(instance);
			try {
				server.registerEaglerPlayer(instance, profileData, () -> {
					initializer.complete();
					server.eventDispatcher().dispatchInitializePlayerEvent(instance, profileData.extraData, null);
				});
			} catch (EaglerXServer.RegistrationStateException ex) {
				try {
					server.getSupervisorService().dropOwnPlayer(initializer.getPlayer().getUniqueId());
				} finally {
					initializer.cancel();
				}
				return;
			}
		} else {
			BasePlayerInstance<PlayerObject> instance = new BasePlayerInstance<>(initializer.getPlayer(),
					conn != null ? conn.attributeHolder : server.getEaglerAttribManager().createEaglerHolder(), server);
			initializer.setPlayerAttachment(instance);
			try {
				server.registerPlayer(instance);
			} catch (EaglerXServer.RegistrationStateException ex) {
				try {
					server.getSupervisorService().dropOwnPlayer(instance.getUniqueId());
				} finally {
					initializer.cancel();
				}
				return;
			}
			initializer.complete();
		}
	}

	@Override
	public void destroyPlayer(IPlatformPlayer<PlayerObject> player) {
		BasePlayerInstance<PlayerObject> instance = player.getPlayerAttachment();
		if (instance != null) {
			try {
				if (instance.isEaglerPlayer()) {
					EaglerPlayerInstance<PlayerObject> eaglerInstance = (EaglerPlayerInstance<PlayerObject>) instance;
					try {
						server.unregisterEaglerPlayer(eaglerInstance);
					} catch (EaglerXServer.RegistrationStateException ex) {
						return;
					}
					server.eventDispatcher().dispatchDestroyPlayerEvent(eaglerInstance, null);
				} else {
					try {
						server.unregisterPlayer(instance);
					} catch (EaglerXServer.RegistrationStateException ex) {
					}
				}
			} finally {
				server.getSupervisorService().dropOwnPlayer(player.getUniqueId());
			}
		}
	}

}
