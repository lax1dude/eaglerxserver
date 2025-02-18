package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

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

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
