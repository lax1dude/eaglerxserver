package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;

public abstract class EaglercraftInitializePlayerEvent extends Event
		implements IEaglercraftInitializePlayerEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftInitializePlayerEvent() {
		super(false);
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
