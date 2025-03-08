package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerPlayerInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayerInitializer;

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
			EaglerPlayerInstance<PlayerObject> instance = new EaglerPlayerInstance<>(initializer.getPlayer(), server);
			initializer.setPlayerAttachment(instance);
			try {
				server.registerEaglerPlayer(instance, () -> {
					server.eventDispatcher().dispatchInitializePlayerEvent(instance, (evt, err) -> {
						if(err == null) {
							initializer.complete();
						}else {
							try {
								server.logger().error("Uncaught exception handling initialize player event", err);
								initializer.getPlayer().disconnect(
										server.componentBuilder().buildTextComponent().text("Internal Server Error").end());
							}finally {
								initializer.cancel();
							}
						}
					});
				});
			}catch(EaglerXServer.RegistrationStateException ex) {
				initializer.cancel();
				return;
			}
		}else {
			BasePlayerInstance<PlayerObject> instance = new BasePlayerInstance<>(initializer.getPlayer(), server);
			initializer.setPlayerAttachment(instance);
			try {
				server.registerPlayer(instance);
			}catch(EaglerXServer.RegistrationStateException ex) {
				initializer.cancel();
				return;
			}
			initializer.complete();
		}
	}

	@Override
	public void destroyPlayer(IPlatformPlayer<PlayerObject> player) {
		BasePlayerInstance<PlayerObject> instance = player.getPlayerAttachment();
		if(instance != null) {
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
		}
	}

}
