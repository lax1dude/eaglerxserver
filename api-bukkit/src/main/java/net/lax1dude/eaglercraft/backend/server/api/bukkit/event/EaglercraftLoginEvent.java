package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftLoginEvent;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class EaglercraftLoginEvent extends Event
		implements IEaglercraftLoginEvent<Player, BaseComponent>, Cancellable {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftLoginEvent() {
		super(true);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
