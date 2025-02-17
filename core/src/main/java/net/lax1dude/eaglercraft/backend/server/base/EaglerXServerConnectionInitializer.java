package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnectionInitializer;

class EaglerXServerConnectionInitializer<PlayerObject>
		implements IEaglerXServerConnectionInitializer<NettyPipelineData, BaseConnectionInstance> {

	private final EaglerXServer<PlayerObject> server;

	EaglerXServerConnectionInitializer(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializeConnection(IPlatformConnectionInitializer<NettyPipelineData, BaseConnectionInstance> initializer) {
		NettyPipelineData nettyData = initializer.getPipelineAttachment();
		if(nettyData != null) {
			if (nettyData.isEaglerPlayer()) {
				initializer.setConnectionAttachment(new EaglerConnectionInstance(initializer.getConnection(), nettyData));

			} else {
				initializer.setConnectionAttachment(new BaseConnectionInstance(initializer.getConnection(),
						nettyData.attributeHolder));
				
			}
		} else {
			initializer.setConnectionAttachment(new BaseConnectionInstance(initializer.getConnection(),
					server.getEaglerAttribManager().createEaglerHolder()));
		}
	}

}
