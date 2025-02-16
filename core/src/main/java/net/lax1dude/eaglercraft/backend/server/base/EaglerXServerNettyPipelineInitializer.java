package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerNettyPipelineInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformNettyPipelineInitializer;

class EaglerXServerNettyPipelineInitializer<PlayerObject> implements IEaglerXServerNettyPipelineInitializer<NettyPipelineData> {

	private final EaglerXServer<PlayerObject> server;

	EaglerXServerNettyPipelineInitializer(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initialize(IPlatformNettyPipelineInitializer<NettyPipelineData> initializer) {
		initializer.setAttachment(new NettyPipelineData(initializer.getChannel(),
				server.getEaglerAttribManager().createEaglerHolder()));
	}

}
