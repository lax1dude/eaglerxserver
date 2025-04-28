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

import java.util.Collection;
import java.util.function.Consumer;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.Collections2;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;

class BukkitWorld implements IPlatformServer<Player> {

	private final PlatformPluginBukkit plugin;
	private final World world;

	BukkitWorld(PlatformPluginBukkit plugin, World world) {
		this.plugin = plugin;
		this.world = world;
	}

	@Override
	public boolean isEaglerRegistered() {
		return false;
	}

	@Override
	public String getServerConfName() {
		return world.getName();
	}

	@Override
	public Collection<IPlatformPlayer<Player>> getAllPlayers() {
		return Collections2.transform(world.getPlayers(), plugin::getPlayer);
	}

	@Override
	public void forEachPlayer(Consumer<IPlatformPlayer<Player>> callback) {
		world.getPlayers().forEach((player) -> {
			IPlatformPlayer<Player> platformPlayer = plugin.getPlayer(player);
			if (platformPlayer != null) {
				callback.accept(platformPlayer);
			}
		});
	}

}
