package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;

public abstract class EaglercraftWebViewChannelEvent extends Event
		implements IEaglercraftWebViewChannelEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftWebViewChannelEvent() {
		super(true);
	}

	@Nonnull
	public HandlerList getHandlers() {
		return handlers;
	}

	@Nonnull
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
