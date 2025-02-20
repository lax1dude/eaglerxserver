package net.lax1dude.eaglercraft.backend.server.bungee.chat;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder;

class BungeeComponentBuilder implements IPlatformComponentBuilder {

	@Override
	public IBuilderComponentText<Object> buildTextComponent() {
		return new BuilderTextRoot();
	}

	@Override
	public IBuilderComponentTranslation<Object> buildTranslationComponent() {
		return new BuilderTranslationRoot();
	}

}
