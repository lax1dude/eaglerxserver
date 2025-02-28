package net.lax1dude.eaglercraft.backend.server.bungee.chat;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumClickAction;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderClickEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

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

	void applyTo(BaseComponent ret) {
		if(action != null) {
			switch(action) {
			case OPEN_URL:
				ret.setClickEvent(new ClickEvent(Action.OPEN_URL, value));
				break;
			case OPEN_FILE:
				ret.setClickEvent(new ClickEvent(Action.OPEN_FILE, value));
				break;
			case RUN_COMMAND:
				ret.setClickEvent(new ClickEvent(Action.RUN_COMMAND, value));
				break;
			case SUGGEST_COMMAND:
				ret.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, value));
				break;
			case CHANGE_PAGE:
				ret.setClickEvent(new ClickEvent(Action.CHANGE_PAGE, value));
				break;
			case COPY_TO_CLIPBOARD:
				if(BungeeComponentHelper.CLICK_ACTION_COPY_TO_CLIPBOARD != null) {
					ret.setClickEvent(new ClickEvent(BungeeComponentHelper.CLICK_ACTION_COPY_TO_CLIPBOARD, value));
				}
				break;
			}
		}
	}

}
