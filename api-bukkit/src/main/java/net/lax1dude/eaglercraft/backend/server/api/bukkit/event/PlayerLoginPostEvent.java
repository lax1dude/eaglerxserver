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

package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import io.netty.channel.Channel;
import net.md_5.bungee.api.chat.BaseComponent;

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

	public abstract BaseComponent getMessage();

	public abstract void setMessage(String message);

	public abstract void setMessage(BaseComponent message);

	public void setKickMessage(String message) {
		setMessage(message);
		setCancelled(true);
	}

	public void setKickMessage(BaseComponent message) {
		setMessage(message);
		setCancelled(true);
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
