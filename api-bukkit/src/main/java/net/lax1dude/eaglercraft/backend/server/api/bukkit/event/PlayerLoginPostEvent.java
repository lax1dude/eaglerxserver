package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import io.netty.channel.Channel;

public abstract class PlayerLoginPostEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	protected PlayerLoginPostEvent(Player player) {
		super(player);
	}

	@Nonnull
	public HandlerList getHandlers() {
		return handlers;
	}

	@Nonnull
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public abstract void registerIntent(@Nonnull Object token);

	public abstract void completeIntent(@Nonnull Object token);

	@Nonnull
	public abstract NettyUnsafe netty();

	public interface NettyUnsafe {

		@Nonnull
		Channel getChannel();

	}

}
