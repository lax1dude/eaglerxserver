package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;

public abstract class EaglercraftWebViewChannelEvent extends Event
		implements IEaglercraftWebViewChannelEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftWebViewChannelEvent() {
		super(false);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
