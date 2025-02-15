package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterCapeEvent;

public abstract class EaglercraftRegisterCapeEvent extends Event
		implements IEaglercraftRegisterCapeEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftRegisterCapeEvent() {
		super(true);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
