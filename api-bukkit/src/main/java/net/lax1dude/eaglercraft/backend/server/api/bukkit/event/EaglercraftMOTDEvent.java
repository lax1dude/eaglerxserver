package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;

public abstract class EaglercraftMOTDEvent extends Event implements IEaglercraftMOTDEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftMOTDEvent() {
		super(false);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
