package net.lax1dude.eaglercraft.backend.server.velocity.chat;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentText;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslation;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslationArgs;

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
		append((Component) component);
		return this;
	}

	@Override
	public void append(Component comp) {
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
