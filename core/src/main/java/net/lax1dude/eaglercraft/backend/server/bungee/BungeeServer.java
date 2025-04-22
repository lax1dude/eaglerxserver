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

package net.lax1dude.eaglercraft.backend.server.bungee;

import java.util.Collection;
import java.util.function.Consumer;

import com.google.common.collect.Collections2;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeServer implements IPlatformServer<ProxiedPlayer> {

	private final PlatformPluginBungee plugin;
	private final ServerInfo serverInfo;
	private final boolean registered;

	BungeeServer(PlatformPluginBungee plugin, ServerInfo serverInfo, boolean registered) {
		this.plugin = plugin;
		this.serverInfo = serverInfo;
		this.registered = registered;
	}

	@Override
	public boolean isEaglerRegistered() {
		return registered;
	}

	@Override
	public String getServerConfName() {
		return serverInfo.getName();
	}

	@Override
	public Collection<IPlatformPlayer<ProxiedPlayer>> getAllPlayers() {
		return Collections2.transform(serverInfo.getPlayers(), plugin::getPlayer);
	}

	@Override
	public void forEachPlayer(Consumer<IPlatformPlayer<ProxiedPlayer>> callback) {
		serverInfo.getPlayers().forEach((player) -> {
			IPlatformPlayer<ProxiedPlayer> platformPlayer = plugin.getPlayer(player);
			if(platformPlayer != null) {
				callback.accept(platformPlayer);
			}
		});
	}

}
