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

package net.lax1dude.eaglercraft.backend.eaglerweb.bukkit;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import eu.hexagonmc.spigot.annotation.meta.DependencyType;
import eu.hexagonmc.spigot.annotation.plugin.Dependency;
import eu.hexagonmc.spigot.annotation.plugin.Plugin;
import eu.hexagonmc.spigot.annotation.plugin.Plugin.Spigot;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebImpl;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebLogger;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebPlatform;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.JavaLogger;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebFactory;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebVersion;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;

@Plugin(
	name = PlatformPluginBukkit.PLUGIN_NAME,
	version = PlatformPluginBukkit.PLUGIN_VERSION,
	description = "Official EaglerWeb plugin for EaglercraftXServer",
	spigot = @Spigot(
		authors = {
			PlatformPluginBukkit.PLUGIN_AUTHOR
		},
		website = "https://lax1dude.net/eaglerxserver",
		prefix = "EaglerWeb"
	),
	dependencies = {
		@Dependency(name = EaglerXServerAPI.PLUGIN_NAME, type = DependencyType.DEPEND)
	}
)
public class PlatformPluginBukkit extends JavaPlugin implements IEaglerWebPlatform<Player> {

	public static final String PLUGIN_NAME = "EaglerWeb";
	public static final String PLUGIN_AUTHOR = EaglerWebVersion.PLUGIN_AUTHOR;
	public static final String PLUGIN_VERSION = EaglerWebVersion.PLUGIN_VERSION;

	private JavaLogger logger;
	private IEaglerWebImpl<Player> plugin;
	IHandleRefresh handleRefresh;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		plugin = EaglerWebFactory.create(this);
	}

	@Override
	public void onEnable() {
		CommandMap map;
		try {
			map = (CommandMap) Class.forName("net.lax1dude.eaglercraft.backend.server.bukkit.BukkitUnsafe")
					.getMethod("getCommandMap", Server.class).invoke(null, getServer());
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Reflection failed!", e);
		}
		map.register("eagler", new CommandEaglerWeb(this));
		plugin.onEnable(EaglerXServerAPI.instance());
	}

	@Override
	public void onDisable() {
		plugin.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerWebLogger logger() {
		return logger;
	}

	@Override
	public String getVersionString() {
		PluginDescriptionFile desc = getDescription();
		return desc.getName() + "/" + desc.getVersion();
	}

	@Override
	public void setHandleRefresh(IHandleRefresh handleRefresh) {
		this.handleRefresh = handleRefresh;
	}

}
