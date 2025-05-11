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

package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;

class BukkitListener implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListener(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLoginEvent(PlayerLoginEvent evt) {
		plugin.initializePlayer(evt.getPlayer());
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent evt) {
		Player p = evt.getPlayer();
		plugin.confirmPlayer(p);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent evt) {
		plugin.dropPlayer(evt.getPlayer());
	}

	@EventHandler
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent evt) {
		if (evt.getFrom() != null && plugin.worldChangeHandler != null) {
			Player p = evt.getPlayer();
			IPlatformPlayer<Player> platfPlayer = plugin.getPlayer(p);
			if (platfPlayer != null) {
				plugin.worldChangeHandler.handleWorldChanged(platfPlayer, p.getWorld().getName());
			}
		}
	}

}
