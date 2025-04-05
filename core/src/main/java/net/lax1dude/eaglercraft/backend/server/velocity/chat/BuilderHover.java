package net.lax1dude.eaglercraft.backend.server.velocity.chat;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumHoverAction;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentText;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslation;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderHoverEvent;

class BuilderHover<ParentType> implements IBuilderHoverEvent<ParentType>, IAppendCallback {

	private final ParentType parent;

	EnumHoverAction action;
	List<Component> buildChildren;

	BuilderHover(ParentType parent) {
		this.parent = parent;
	}

	@Override
	public ParentType end() {
		return parent;
	}

	@Override
	public IBuilderHoverEvent<ParentType> hoverAction(EnumHoverAction action) {
		this.action = action;
		return this;
	}

	@Override
	public IBuilderComponentText<IBuilderHoverEvent<ParentType>> appendTextContent() {
		return new BuilderTextChild<>(this);
	}

	@Override
	public IBuilderComponentTranslation<IBuilderHoverEvent<ParentType>> appendTranslationContent() {
		return new BuilderTranslationChild<>(this);
	}

	@Override
	public void append(Component comp) {
		if(buildChildren == null) {
			buildChildren = new ArrayList<>(4);
		}
		buildChildren.add(comp);
	}

	Component applyTo(Component ret) {
		if(action == EnumHoverAction.SHOW_TEXT && buildChildren != null) {
			if(buildChildren.size() > 1) {
				ret = ret.hoverEvent(HoverEvent.showText(Component.empty().children(buildChildren)));
			}else {
				ret = ret.hoverEvent(HoverEvent.showText(buildChildren.get(0)));
			}
		}
		return ret;
	}

}
