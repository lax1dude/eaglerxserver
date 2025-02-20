package net.lax1dude.eaglercraft.backend.server.velocity.chat;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder;

class VelocityComponentBuilder implements IPlatformComponentBuilder {

	@Override
	public IBuilderComponentText<Object> buildTextComponent() {
		return new BuilderTextRoot();
	}

	@Override
	public IBuilderComponentTranslation<Object> buildTranslationComponent() {
		return new BuilderTranslationRoot();
	}

}
