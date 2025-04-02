package net.lax1dude.eaglercraft.backend.rpc.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.rpc.api.event.IEaglercraftVoiceCapableEvent;

public abstract class EaglercraftVoiceCapableEvent extends Event
		implements IEaglercraftVoiceCapableEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftVoiceCapableEvent() {
		super(false);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
