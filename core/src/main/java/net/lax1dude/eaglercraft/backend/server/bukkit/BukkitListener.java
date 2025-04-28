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

package net.lax1dude.eaglercraft.backend.server.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.PlayerLoginInitEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.PlayerLoginPostEvent;
import net.md_5.bungee.api.chat.BaseComponent;

class BukkitListener implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListener(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLoginEvent(PlayerLoginEvent evt) {
		plugin.postLoginInjector.handleLoginEvent(evt);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerLoginInitEvent(PlayerLoginInitEvent evt) {
		plugin.handleConnectionInit(evt.netty().getChannel());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerPostLoginEvent(PlayerLoginPostEvent evt) {
		Player player = evt.getPlayer();
		plugin.forEachChannel((ch) -> {
			BukkitUnsafe.addPlayerChannel(player, ch);
		});
		Channel channel = evt.netty().getChannel();
		Attribute<BukkitConnection> attr = channel.attr(PipelineAttributes.<BukkitConnection>connectionData());
		BukkitConnection conn = attr.get();
		evt.registerIntent(plugin);
		Runnable cont = () -> {
			try {
				plugin.initializePlayer(player, conn, attr::set, (b) -> {
					if (b != Boolean.TRUE) {
						if (b != null) {
							evt.setKickMessage((BaseComponent) b);
						}
						evt.setCancelled(true);
					}
					evt.completeIntent(plugin);
				});
			} catch (Exception ex) {
				try {
					evt.setCancelled(true);
					evt.completeIntent(plugin);
				} catch (IllegalStateException exx) {
					return;
				}
				if (ex instanceof RuntimeException exx)
					throw exx;
				throw new RuntimeException("Uncaught exception", ex);
			}
		};
		if (conn != null) {
			conn.awaitPlayState(cont);
		} else {
			cont.run();
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent evt) {
		plugin.confirmPlayer(evt.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent evt) {
		if (evt.getFrom() != null) {
			plugin.worldChange(evt.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuitEvent(PlayerQuitEvent evt) {
		plugin.dropPlayer(evt.getPlayer());
	}

}
