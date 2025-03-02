package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerPlayerInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayerInitializer;
import net.lax1dude.eaglercraft.backend.server.base.message.MessageControllerFactory;

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
			instance.messageController = MessageControllerFactory.initializePlayer(instance);
			initializer.setPlayerAttachment(instance);
			server.registerEaglerPlayer(instance);
		}else {
			BasePlayerInstance<PlayerObject> instance = new BasePlayerInstance<>(initializer.getPlayer(), server);
			initializer.setPlayerAttachment(instance);
			server.registerPlayer(instance);
		}
	}

	@Override
	public void destroyPlayer(IPlatformPlayer<PlayerObject> player) {
		BasePlayerInstance<PlayerObject> instance = player.getPlayerAttachment();
		if(instance != null) {
			if(instance.isEaglerPlayer()) {
				server.unregisterEaglerPlayer((EaglerPlayerInstance<PlayerObject>)instance);
			}else {
				server.unregisterPlayer(instance);
			}
		}
	}

}
