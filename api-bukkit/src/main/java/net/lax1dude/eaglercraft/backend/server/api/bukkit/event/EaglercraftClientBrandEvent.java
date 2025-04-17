package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class EaglercraftClientBrandEvent extends Event
		implements IEaglercraftClientBrandEvent<Player, BaseComponent>, Cancellable {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftClientBrandEvent() {
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
