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
		IEaglerXServerPlayerInitializer<BaseConnectionInstance, BasePlayerInstance<PlayerObject>, PlayerObject> {

	private final EaglerXServer<PlayerObject> server;

	EaglerXServerPlayerInitializer(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializePlayer(IPlatformPlayerInitializer<BaseConnectionInstance, BasePlayerInstance<PlayerObject>, PlayerObject> initializer) {
		BaseConnectionInstance conn = initializer.getConnectionAttachment();
		if(conn == null) {
			return;
		}
		if(conn.isEaglerPlayer()) {
			conn.asEaglerPlayer().processProfileData();
		}
		ISupervisorServiceImpl<PlayerObject> supervisor = server.getSupervisorService();
		if(supervisor.isSupervisorEnabled()) {
			supervisor.acceptPlayer(conn.getUniqueId(),
					conn.isEaglerPlayer() ? conn.asEaglerPlayer().getEaglerBrandUUID() : IBrandRegistry.BRAND_VANILLA,
					conn.getMinecraftProtocol(),
					conn.isEaglerPlayer() ? conn.asEaglerPlayer().getHandshakeEaglerProtocol() : 0, conn.getUsername(),
					(res) -> {
				if(res == EnumAcceptPlayer.ACCEPT) {
					server.getPlatform().getScheduler().executeAsync(() -> {
						acceptPlayer(initializer);
					});
				}else {
					switch(res) {
					case REJECT_ALREADY_WAITING:
					case REJECT_DUPLICATE_USERNAME:
					case REJECT_DUPLICATE_UUID:
						try {
							initializer.getPlayer().disconnect(server.componentHelper().getStandardKickAlreadyPlaying());
						}finally {
							initializer.cancel();
						}
						break;
					case REJECT_UNKNOWN:
					default:
						try {
							initializer.getPlayer().disconnect(server.componentBuilder().buildTextComponent()
									.text("Internal Supervisor Error").end());
						}finally {
							initializer.cancel();
						}
						break;
					case SUPERVISOR_UNAVAILABLE:
						try {
							initializer.getPlayer().disconnect(server.componentBuilder().buildTextComponent()
									.text(server.getConfig().getSupervisor().getSupervisorUnavailableMessage()).end());
						}finally {
							initializer.cancel();
						}
						break;
					}
				}
			});
		}else {
			acceptPlayer(initializer);
		}
	}

	private void acceptPlayer(IPlatformPlayerInitializer<BaseConnectionInstance, BasePlayerInstance<PlayerObject>, PlayerObject> initializer) {
		BaseConnectionInstance conn = initializer.getConnectionAttachment();
		if(conn.isEaglerPlayer()) {
			NettyPipelineData.ProfileDataHolder profileData = conn.asEaglerPlayer().transferProfileData();
			EaglerPlayerInstance<PlayerObject> instance = new EaglerPlayerInstance<>(initializer.getPlayer(), server);
			initializer.setPlayerAttachment(instance);
			try {
				server.registerEaglerPlayer(instance, profileData, () -> {
					server.eventDispatcher().dispatchInitializePlayerEvent(instance, profileData.extraData, (evt, err) -> {
						if(err == null) {
							initializer.complete();
						}else {
							try {
								instance.logger().error("Uncaught exception handling initialize player event", err);
								initializer.getPlayer().disconnect(
										server.componentBuilder().buildTextComponent().text("Internal Error").end());
							}finally {
								try {
									server.getSupervisorService().dropOwnPlayer(instance.getUniqueId());
								}finally {
									initializer.cancel();
								}
							}
						}
					});
				});
			}catch(EaglerXServer.RegistrationStateException ex) {
				try {
					server.getSupervisorService().dropOwnPlayer(initializer.getPlayer().getUniqueId());
				}finally {
					initializer.cancel();
				}
				return;
			}
		}else {
			BasePlayerInstance<PlayerObject> instance = new BasePlayerInstance<>(initializer.getPlayer(), server);
			initializer.setPlayerAttachment(instance);
			try {
				server.registerPlayer(instance);
			}catch(EaglerXServer.RegistrationStateException ex) {
				try {
					server.getSupervisorService().dropOwnPlayer(instance.getUniqueId());
				}finally {
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
		if(instance != null) {
			try {
				if(instance.isEaglerPlayer()) {
					EaglerPlayerInstance<PlayerObject> eaglerInstance = (EaglerPlayerInstance<PlayerObject>) instance;
					try {
						server.unregisterEaglerPlayer(eaglerInstance);
					}catch(EaglerXServer.RegistrationStateException ex) {
						return;
					}
					server.eventDispatcher().dispatchDestroyPlayerEvent(eaglerInstance, null);
				}else {
					try {
						server.unregisterPlayer(instance);
					}catch(EaglerXServer.RegistrationStateException ex) {
					}
				}
			}finally {
				server.getSupervisorService().dropOwnPlayer(player.getUniqueId());
			}
		}
	}

}
