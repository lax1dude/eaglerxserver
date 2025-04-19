package net.lax1dude.eaglercraft.backend.server.bukkit.async;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.PlayerLoginPostEvent;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectSet;
import net.lax1dude.eaglercraft.backend.server.base.collect.ObjectIdentityHashSet;

class PlayerLoginPostEventImpl extends PlayerLoginPostEvent implements PlayerLoginPostEvent.NettyUnsafe {

	private final Channel channel;
	private final Consumer<PlayerLoginPostEvent> callback;
	private boolean cancelled = false;
	private boolean completed = false;
	private ObjectSet<Object> intents;

	PlayerLoginPostEventImpl(Player player, Channel channel, Consumer<PlayerLoginPostEvent> callback) {
		super(player);
		this.channel = channel;
		this.callback = callback;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public void registerIntent(Object token) {
		synchronized(this) {
			if(completed) {
				throw new IllegalStateException("Event is already completed!");
			}
			if(intents == null) {
				intents = new ObjectIdentityHashSet<>();
			}
			intents.add(token);
		}
	}

	@Override
	public void completeIntent(Object token) {
		eagler: {
			synchronized(this) {
				if(intents != null && intents.removeAll(token) > 0) {
					if(completed && intents.isEmpty()) {
						break eagler;
					}
					return;
				}
			}
			throw new IllegalStateException("Intent not registered!");
		}
		onComplete();
	}

	public void complete() {
		synchronized(this) {
			completed = true;
			if(intents != null && !intents.isEmpty()) {
				return;
			}
		}
		onComplete();
	}

	private void onComplete() {
		callback.accept(this);
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

}
