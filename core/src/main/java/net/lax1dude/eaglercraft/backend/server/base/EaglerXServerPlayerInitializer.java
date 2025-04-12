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
			EaglerPlayerInstance<PlayerObject> instance = new EaglerPlayerInstance<>(initializer.getPlayer(), server);
			initializer.setPlayerAttachment(instance);
			try {
				server.registerEaglerPlayer(instance, () -> {
					server.eventDispatcher().dispatchInitializePlayerEvent(instance, (evt, err) -> {
						if(err == null) {
							initializer.complete();
						}else {
							try {
								instance.logger().error("Uncaught exception handling initialize player event", err);
								initializer.getPlayer().disconnect(
										server.componentBuilder().buildTextComponent().text("Internal Server Error").end());
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
