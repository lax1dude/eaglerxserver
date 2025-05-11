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

package net.lax1dude.eaglercraft.backend.eaglermotd.bukkit;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import eu.hexagonmc.spigot.annotation.meta.DependencyType;
import eu.hexagonmc.spigot.annotation.plugin.Dependency;
import eu.hexagonmc.spigot.annotation.plugin.Plugin;
import eu.hexagonmc.spigot.annotation.plugin.Plugin.Spigot;
import net.lax1dude.eaglercraft.backend.eaglermotd.adapter.IEaglerMOTDImpl;
import net.lax1dude.eaglercraft.backend.eaglermotd.adapter.IEaglerMOTDLogger;
import net.lax1dude.eaglercraft.backend.eaglermotd.adapter.IEaglerMOTDPlatform;
import net.lax1dude.eaglercraft.backend.eaglermotd.adapter.JavaLogger;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.EaglerMOTDFactory;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.EaglerMOTDVersion;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;

@Plugin(
	name = PlatformPluginBukkit.PLUGIN_NAME,
	version = PlatformPluginBukkit.PLUGIN_VERSION,
	description = "Official EaglerMOTD plugin for EaglercraftXServer",
	spigot = @Spigot(
		authors = {
			PlatformPluginBukkit.PLUGIN_AUTHOR
		},
		website = "https://lax1dude.net/eaglerxserver",
		prefix = "EaglerMOTD"
	),
	dependencies = {
		@Dependency(name = EaglerXServerAPI.PLUGIN_NAME, type = DependencyType.DEPEND)
	}
)
public class PlatformPluginBukkit extends JavaPlugin implements IEaglerMOTDPlatform<Player> {

	public static final String PLUGIN_NAME = EaglerMOTDVersion.PLUGIN_BRAND;
	public static final String PLUGIN_AUTHOR = EaglerMOTDVersion.PLUGIN_AUTHOR;
	public static final String PLUGIN_VERSION = EaglerMOTDVersion.PLUGIN_VERSION;

	private JavaLogger logger;
	private IEaglerMOTDImpl<Player> eaglermotd;
	Consumer<IEaglercraftMOTDEvent<Player>> onMOTDHandler;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		eaglermotd = EaglerMOTDFactory.create(this);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
		eaglermotd.onEnable(EaglerXServerAPI.instance());
	}

	@Override
	public void onDisable() {
		eaglermotd.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerMOTDLogger logger() {
		return logger;
	}

	@Override
	public void setOnMOTD(Consumer<IEaglercraftMOTDEvent<Player>> handler) {
		this.onMOTDHandler = handler;
	}

}
