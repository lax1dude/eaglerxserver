package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;

public abstract class EaglercraftWebViewMessageEvent extends Event
		implements IEaglercraftWebViewMessageEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftWebViewMessageEvent() {
		super(false);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
