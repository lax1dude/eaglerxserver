package net.lax1dude.eaglercraft.backend.rpc.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.rpc.api.event.IEaglercraftVoiceChangeEvent;

public abstract class EaglercraftVoiceChangeEvent extends Event
		implements IEaglercraftVoiceChangeEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftVoiceChangeEvent() {
		super(false);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
