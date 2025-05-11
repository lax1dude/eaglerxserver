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

package net.lax1dude.eaglercraft.backend.server.velocity;

import java.util.Collection;
import java.util.function.Consumer;

import com.google.common.collect.Collections2;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;

class VelocityServer implements IPlatformServer<Player> {

	private final PlatformPluginVelocity plugin;
	private final RegisteredServer server;
	private final boolean registered;

	public VelocityServer(PlatformPluginVelocity plugin, RegisteredServer server, boolean registered) {
		this.plugin = plugin;
		this.server = server;
		this.registered = registered;
	}

	@Override
	public boolean isEaglerRegistered() {
		return registered;
	}

	@Override
	public String getServerConfName() {
		return server.getServerInfo().getName();
	}

	@Override
	public Collection<IPlatformPlayer<Player>> getAllPlayers() {
		return Collections2.transform(server.getPlayersConnected(), plugin::getPlayer);
	}

	@Override
	public void forEachPlayer(Consumer<IPlatformPlayer<Player>> callback) {
		server.getPlayersConnected().forEach((player) -> {
			IPlatformPlayer<Player> platformPlayer = plugin.getPlayer(player);
			if (platformPlayer != null) {
				callback.accept(platformPlayer);
			}
		});
	}

}
