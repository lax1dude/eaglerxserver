package net.lax1dude.eaglercraft.backend.server.velocity.chat;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderClickEvent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentText;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslation;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderHoverEvent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderStyle;

abstract class BuilderTranslationBase<ParentType> implements IBuilderComponentTranslation<ParentType>, IAppendCallback {

	BuilderStyle<IBuilderComponentTranslation<ParentType>> style;
	BuilderClick<IBuilderComponentTranslation<ParentType>> click;
	BuilderHover<IBuilderComponentTranslation<ParentType>> hover;
	String translation;
	String insertion;
	List<Component> buildChildren;

	@Override
	public IBuilderStyle<IBuilderComponentTranslation<ParentType>> beginStyle() {
		return style = new BuilderStyle<>(this);
	}

	@Override
	public IBuilderClickEvent<IBuilderComponentTranslation<ParentType>> beginClickEvent() {
		return click = new BuilderClick<>(this);
	}

	@Override
	public IBuilderHoverEvent<IBuilderComponentTranslation<ParentType>> beginHoverEvent() {
		return hover = new BuilderHover<>(this);
	}

	@Override
	public IBuilderComponentText<IBuilderComponentTranslation<ParentType>> appendTextComponent() {
		return new BuilderTextChild<>(this);
	}

	@Override
	public IBuilderComponentTranslation<IBuilderComponentTranslation<ParentType>> appendTranslationComponent() {
		return new BuilderTranslationChild<>(this);
	}

	@Override
	public IBuilderComponentTranslation<ParentType> translation(String key) {
		this.translation = key;
		return null;
	}

	@Override
	public IBuilderComponentTranslation<ParentType> insertion(String txt) {
		this.insertion = txt;
		return this;
	}

	@Override
	public void append(Component comp) {
		if(buildChildren == null) {
			buildChildren = new ArrayList<>(4);
		}
		buildChildren.add(comp);
	}

	protected Component build() {
		Component ret = Component.translatable(translation);
		if(insertion != null) {
			ret.insertion(insertion);
		}
		if(style != null) {
			style.applyTo(ret);
		}
		if(click != null) {
			click.applyTo(ret);
		}
		if(hover != null) {
			hover.applyTo(ret);
		}
		if(buildChildren != null) {
			ret.children(buildChildren);
		}
		return ret;
	}

}
