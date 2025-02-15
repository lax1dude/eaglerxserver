package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterSkinEvent;

public abstract class EaglercraftRegisterSkinEvent extends Event
		implements IEaglercraftRegisterSkinEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftRegisterSkinEvent() {
		super(true);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
