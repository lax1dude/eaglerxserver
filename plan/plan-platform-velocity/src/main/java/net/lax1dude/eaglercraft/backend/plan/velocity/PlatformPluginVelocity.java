/*
 * Copyright (c) 2025 ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.plan.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;

import com.velocitypowered.api.proxy.Player;
import net.lax1dude.eaglercraft.backend.plan.PlanHook;
import net.lax1dude.eaglercraft.backend.plan.PlanVersion;

import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;

import net.lax1dude.eaglercraft.backend.server.api.velocity.EaglerXServerAPI;

import java.util.UUID;
import java.util.function.BiConsumer;

@Plugin(
	id = PlatformPluginVelocity.PLUGIN_ID,
	name = PlatformPluginVelocity.PLUGIN_NAME,
	authors = {
		PlatformPluginVelocity.PLUGIN_AUTHOR
	},
	version = PlatformPluginVelocity.PLUGIN_VERSION,
	description = "Official Plan player analytics integration plugin for EaglercraftXServer",
	dependencies = {
		@Dependency(id = EaglerXServerAPI.PLUGIN_ID, optional = false),
		@Dependency(id = "plan", optional = false)
	}
)
public class PlatformPluginVelocity {

	public static final String PLUGIN_ID = "eaglerxplan";
	public static final String PLUGIN_NAME = PlanVersion.PLUGIN_BRAND;
	public static final String PLUGIN_AUTHOR = PlanVersion.PLUGIN_AUTHOR;
	public static final String PLUGIN_VERSION = PlanVersion.PLUGIN_VERSION;

	private BiConsumer<UUID, String> eaglerInit;

	@Subscribe
	public void onProxyInitialize(ProxyInitializeEvent event) {
		eaglerInit = PlanHook.hookIntoPlan(EaglerXServerAPI.instance());
	}

	@Subscribe
	public void onPlayerJoin(ServerConnectedEvent event) {
		Player player = event.getPlayer();
		eaglerInit.accept(player.getUniqueId(), player.getUsername());
	}

}
