package net.lax1dude.eaglercraft.backend.rpc.api.bukkit.event;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.rpc.api.event.IEaglercraftPlayerReadyEvent;

public abstract class EaglercraftPlayerReadyEvent extends Event
		implements IEaglercraftPlayerReadyEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftPlayerReadyEvent() {
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
