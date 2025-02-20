package net.lax1dude.eaglercraft.backend.server.bungee.chat;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderClickEvent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentText;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslation;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderHoverEvent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderStyle;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

abstract class BuilderTextBase<ParentType> implements IBuilderComponentText<ParentType>, IAppendCallback {

	BuilderStyle<IBuilderComponentText<ParentType>> style;
	BuilderClick<IBuilderComponentText<ParentType>> click;
	BuilderHover<IBuilderComponentText<ParentType>> hover;
	String text;
	String insertion;
	List<BaseComponent> buildChildren;

	@Override
	public IBuilderStyle<IBuilderComponentText<ParentType>> beginStyle() {
		return style = new BuilderStyle<>(this);
	}

	@Override
	public IBuilderClickEvent<IBuilderComponentText<ParentType>> beginClickEvent() {
		return click = new BuilderClick<>(this);
	}

	@Override
	public IBuilderHoverEvent<IBuilderComponentText<ParentType>> beginHoverEvent() {
		return hover = new BuilderHover<>(this);
	}

	@Override
	public IBuilderComponentText<IBuilderComponentText<ParentType>> appendTextComponent() {
		return new BuilderTextChild<>(this);
	}

	@Override
	public IBuilderComponentTranslation<IBuilderComponentText<ParentType>> appendTranslationComponent() {
		return new BuilderTranslationChild<>(this);
	}

	@Override
	public IBuilderComponentText<ParentType> insertion(String txt) {
		this.insertion = txt;
		return this;
	}

	@Override
	public IBuilderComponentText<ParentType> text(String txt) {
		this.text = txt;
		return this;
	}

	@Override
	public void append(BaseComponent comp) {
		if(buildChildren == null) {
			buildChildren = new ArrayList<>(4);
		}
		buildChildren.add(comp);
	}

	protected BaseComponent build() {
		TextComponent ret = text != null ? new TextComponent(text) : new TextComponent();
		if(insertion != null) {
			ret.setInsertion(insertion);
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
			ret.setExtra(buildChildren);
		}
		return ret;
	}

}
