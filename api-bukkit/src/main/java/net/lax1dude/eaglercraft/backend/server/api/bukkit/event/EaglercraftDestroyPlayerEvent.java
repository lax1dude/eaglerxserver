package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftDestroyPlayerEvent;

public abstract class EaglercraftDestroyPlayerEvent extends Event implements IEaglercraftDestroyPlayerEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftDestroyPlayerEvent() {
		super(false);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
