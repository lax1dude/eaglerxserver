package net.lax1dude.eaglercraft.backend.server.bungee.chat;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentText;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslation;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslationArgs;
import net.md_5.bungee.api.chat.BaseComponent;

class BuilderTranslationArgs<ParentType> implements IBuilderComponentTranslationArgs<ParentType>, IAppendCallback {

	private final ParentType parent;
	private List<Object> args;

	BuilderTranslationArgs(ParentType parent) {
		this.parent = parent;
	}

	@Override
	public IBuilderComponentText<IBuilderComponentTranslationArgs<ParentType>> textArg() {
		return new BuilderTextChild<>(this);
	}

	@Override
	public IBuilderComponentTranslation<IBuilderComponentTranslationArgs<ParentType>> translateArg() {
		return new BuilderTranslationChild<>(this);
	}

	@Override
	public IBuilderComponentTranslationArgs<ParentType> rawArg(Object component) {
		append((BaseComponent) component);
		return this;
	}

	@Override
	public void append(BaseComponent comp) {
		if(args == null) {
			args = new ArrayList<>(4);
		}
		args.add(comp);
	}

	@Override
	public ParentType end() {
		if(args != null) {
			((BuilderTranslationBase<?>)parent).componentArgs(args);
		}
		return parent;
	}

}
