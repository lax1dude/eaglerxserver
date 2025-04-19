package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import javax.annotation.Nonnull;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.netty.channel.Channel;

public abstract class PlayerLoginInitEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	protected PlayerLoginInitEvent() {
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

	@Nonnull
	public abstract NettyUnsafe netty();

	public interface NettyUnsafe {

		@Nonnull
		Channel getChannel();

	}

}
