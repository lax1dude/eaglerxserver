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

package net.lax1dude.eaglercraft.backend.eaglerweb.bungee;

import eu.hexagonmc.spigot.annotation.meta.DependencyType;
import eu.hexagonmc.spigot.annotation.plugin.Dependency;
import eu.hexagonmc.spigot.annotation.plugin.Plugin.Bungee;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebImpl;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebLogger;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebPlatform;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.JavaLogger;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWeb;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebVersion;
import net.lax1dude.eaglercraft.backend.server.api.bungee.EaglerXServerAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

@eu.hexagonmc.spigot.annotation.plugin.Plugin(
	name = PlatformPluginBungee.PLUGIN_NAME,
	version = PlatformPluginBungee.PLUGIN_VERSION,
	description = "Official EaglerWeb plugin for EaglercraftXServer",
	bungee = @Bungee(author = PlatformPluginBungee.PLUGIN_AUTHOR),
	dependencies = {
		@Dependency(name = EaglerXServerAPI.PLUGIN_NAME, type = DependencyType.DEPEND)
	}
)
public class PlatformPluginBungee extends Plugin implements IEaglerWebPlatform<ProxiedPlayer> {

	public static final String PLUGIN_NAME = "EaglerWeb";
	public static final String PLUGIN_AUTHOR = EaglerWebVersion.PLUGIN_AUTHOR;
	public static final String PLUGIN_VERSION = EaglerWebVersion.PLUGIN_VERSION;

	private JavaLogger logger;
	private IEaglerWebImpl<ProxiedPlayer> plugin;
	IHandleRefresh handleRefresh;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		plugin = new EaglerWeb<>(this);
	}

	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerCommand(this, new CommandEaglerWeb(this));
		plugin.onEnable(EaglerXServerAPI.instance());
	}

	@Override
	public void onDisable() {
		getProxy().getPluginManager().unregisterCommands(this);
		plugin.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerWebLogger logger() {
		return logger;
	}

	@Override
	public String getVersionString() {
		PluginDescription desc = getDescription();
		return desc.getName() + "/" + desc.getVersion();
	}

	@Override
	public void setHandleRefresh(IHandleRefresh handleRefresh) {
		this.handleRefresh = handleRefresh;
	}

}
