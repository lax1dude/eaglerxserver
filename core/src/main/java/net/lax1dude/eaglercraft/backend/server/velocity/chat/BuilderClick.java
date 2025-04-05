package net.lax1dude.eaglercraft.backend.server.velocity.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumClickAction;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderClickEvent;

class BuilderClick<ParentType> implements IBuilderClickEvent<ParentType> {

	private final ParentType parent;

	EnumClickAction action;
	String value;

	BuilderClick(ParentType parent) {
		this.parent = parent;
	}

	@Override
	public ParentType end() {
		return parent;
	}

	@Override
	public IBuilderClickEvent<ParentType> clickAction(EnumClickAction action) {
		this.action = action;
		return this;
	}

	@Override
	public IBuilderClickEvent<ParentType> clickValue(String value) {
		this.value = value;
		return this;
	}

	Component applyTo(Component ret) {
		if(action != null) {
			switch(action) {
			case OPEN_URL:
				ret = ret.clickEvent(ClickEvent.openUrl(value));
				break;
			case OPEN_FILE:
				ret = ret.clickEvent(ClickEvent.openFile(value));
				break;
			case RUN_COMMAND:
				ret = ret.clickEvent(ClickEvent.runCommand(value));
				break;
			case SUGGEST_COMMAND:
				ret = ret.clickEvent(ClickEvent.suggestCommand(value));
				break;
			case CHANGE_PAGE:
				ret = ret.clickEvent(ClickEvent.changePage(value));
				break;
			case COPY_TO_CLIPBOARD:
				ret = ret.clickEvent(ClickEvent.copyToClipboard(value));
				break;
			}
		}
		return ret;
	}

}
