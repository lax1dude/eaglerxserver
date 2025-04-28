/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.bukkit.async;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.PlayerLoginPostEvent;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectSet;
import net.lax1dude.eaglercraft.backend.server.base.collect.ObjectIdentityHashSet;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

class PlayerLoginPostEventImpl extends PlayerLoginPostEvent implements PlayerLoginPostEvent.NettyUnsafe {

	private final Channel channel;
	private final Consumer<PlayerLoginPostEvent> callback;
	private BaseComponent message;
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
	public BaseComponent getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message != null ? new TextComponent(message) : null;
	}

	@Override
	public void setMessage(BaseComponent message) {
		this.message = message;
	}

	@Override
	public void registerIntent(Object token) {
		synchronized (this) {
			if (completed) {
				throw new IllegalStateException("Event is already completed!");
			}
			if (intents == null) {
				intents = new ObjectIdentityHashSet<>();
			}
			intents.add(token);
		}
	}

	@Override
	public void completeIntent(Object token) {
		eagler: {
			synchronized (this) {
				if (intents != null && intents.removeAll(token) > 0) {
					if (completed && intents.isEmpty()) {
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
		synchronized (this) {
			completed = true;
			if (intents != null && !intents.isEmpty()) {
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
