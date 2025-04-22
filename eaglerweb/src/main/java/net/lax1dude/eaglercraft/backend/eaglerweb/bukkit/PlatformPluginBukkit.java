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

import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWeb;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.IEaglerWebLogger;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.IEaglerWebPlatform;
import net.lax1dude.eaglercraft.backend.eaglerweb.bungee.JavaLogger;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;

public class PlatformPluginBukkit extends JavaPlugin implements IEaglerWebPlatform<Player> {

	private JavaLogger logger;
	private EaglerWeb<Player> plugin;
	IHandleRefresh handleRefresh;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		plugin = new EaglerWeb<Player>(this);
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
