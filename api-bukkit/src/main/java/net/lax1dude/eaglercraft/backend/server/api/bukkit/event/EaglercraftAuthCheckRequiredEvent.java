package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class EaglercraftAuthCheckRequiredEvent extends Event
		implements IEaglercraftAuthCheckRequiredEvent<Player, BaseComponent> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftAuthCheckRequiredEvent() {
		super(true);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
