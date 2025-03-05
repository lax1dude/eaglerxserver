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
		if(initializer.getConnectionAttachment().isEaglerPlayer()) {
			EaglerPlayerInstance<PlayerObject> instance = new EaglerPlayerInstance<>(initializer.getPlayer(), server);
			try {
				server.registerEaglerPlayer(instance);
			}catch(EaglerXServer.RegistrationStateException ex) {
				return;
			}finally {
				initializer.setPlayerAttachment(instance);
			}
			server.eventDispatcher().dispatchInitializePlayerEvent(instance, null);
		}else {
			BasePlayerInstance<PlayerObject> instance = new BasePlayerInstance<>(initializer.getPlayer(), server);
			try {
				server.registerPlayer(instance);
			}catch(EaglerXServer.RegistrationStateException ex) {
			}
			initializer.setPlayerAttachment(instance);
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
