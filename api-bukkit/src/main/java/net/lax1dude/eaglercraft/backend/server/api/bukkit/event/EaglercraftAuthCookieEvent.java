package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCookieEvent;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class EaglercraftAuthCookieEvent extends Event
		implements IEaglercraftAuthCookieEvent<Player, BaseComponent> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftAuthCookieEvent() {
		super(true);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
