package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesProperty;

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
				if(server.isEaglerPlayerPropertyEnabled()) {
					initializer.setEaglerPlayerProperty(true);
				}
				TexturesProperty eaglerPlayersSkin = server.getEaglerPlayersVanillaSkin();
				if(eaglerPlayersSkin != null) {
					initializer.setTexturesProperty(eaglerPlayersSkin.getValue(), eaglerPlayersSkin.getSignature());
				}
				if(server.getPlatformType() != EnumPlatformType.BUKKIT) {
					initializer.setUniqueId(nettyData.uuid);
				}
			} else {
				initializer.setConnectionAttachment(new BaseConnectionInstance(initializer.getConnection(),
						nettyData.attributeHolder));
				if(server.isEaglerPlayerPropertyEnabled()) {
					initializer.setEaglerPlayerProperty(false);
				}
			}
		} else {
			initializer.setConnectionAttachment(new BaseConnectionInstance(initializer.getConnection(),
					server.getEaglerAttribManager().createEaglerHolder()));
			if(server.isEaglerPlayerPropertyEnabled()) {
				initializer.setEaglerPlayerProperty(false);
			}
		}
	}

}
