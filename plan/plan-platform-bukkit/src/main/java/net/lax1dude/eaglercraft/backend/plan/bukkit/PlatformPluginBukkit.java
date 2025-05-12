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

package net.lax1dude.eaglercraft.backend.plan.bukkit;

import net.lax1dude.eaglercraft.backend.plan.PlanHook;
import net.lax1dude.eaglercraft.backend.plan.PlanVersion;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import eu.hexagonmc.spigot.annotation.meta.DependencyType;
import eu.hexagonmc.spigot.annotation.plugin.Dependency;
import eu.hexagonmc.spigot.annotation.plugin.Plugin;
import eu.hexagonmc.spigot.annotation.plugin.Plugin.Spigot;

import java.util.UUID;
import java.util.function.BiConsumer;

@Plugin(
	name = PlatformPluginBukkit.PLUGIN_NAME,
	version = PlatformPluginBukkit.PLUGIN_VERSION,
	description = "Official Plan player analytics integration plugin for EaglercraftXServer",
	spigot = @Spigot(
		authors = {
			PlatformPluginBukkit.PLUGIN_AUTHOR
		},
		website = "https://lax1dude.net/eaglerxserver",
		prefix = "EaglerXPlan"
	),
	dependencies = {
		@Dependency(name = EaglerXServerAPI.PLUGIN_NAME, type = DependencyType.DEPEND),
		@Dependency(name = "Plan", type = DependencyType.DEPEND)
	}
)
public class PlatformPluginBukkit extends JavaPlugin implements Listener {

	public static final String PLUGIN_NAME = "EaglercraftXPlan";
	public static final String PLUGIN_AUTHOR = PlanVersion.PLUGIN_AUTHOR;
	public static final String PLUGIN_VERSION = PlanVersion.PLUGIN_VERSION;

	private BiConsumer<UUID, String> eaglerInit;

	@Override
	public void onEnable() {
		eaglerInit = PlanHook.hookIntoPlan(EaglerXServerAPI.instance());
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		eaglerInit.accept(player.getUniqueId(), player.getName());
	}

}
