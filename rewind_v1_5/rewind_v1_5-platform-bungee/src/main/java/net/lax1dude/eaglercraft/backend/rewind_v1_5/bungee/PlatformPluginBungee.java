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

package net.lax1dude.eaglercraft.backend.rewind_v1_5.bungee;

import eu.hexagonmc.spigot.annotation.meta.DependencyType;
import eu.hexagonmc.spigot.annotation.plugin.Dependency;
import eu.hexagonmc.spigot.annotation.plugin.Plugin.Bungee;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.adapter.IRewindLogger;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.adapter.IRewindPlatform;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.adapter.JavaLogger;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.RewindFactory;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.RewindVersion;
import net.lax1dude.eaglercraft.backend.server.api.bungee.EaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

@eu.hexagonmc.spigot.annotation.plugin.Plugin(
	name = PlatformPluginBungee.PLUGIN_NAME,
	version = PlatformPluginBungee.PLUGIN_VERSION,
	description = PlatformPluginBungee.PLUGIN_DESC,
	bungee = @Bungee(author = PlatformPluginBungee.PLUGIN_AUTHOR),
	dependencies = {
		@Dependency(name = EaglerXServerAPI.PLUGIN_NAME, type = DependencyType.DEPEND)
	}
)
public class PlatformPluginBungee extends Plugin implements IRewindPlatform<ProxiedPlayer> {

	public static final String PLUGIN_NAME = "EaglercraftXRewind-" + RewindVersion.REWIND_VERSION_DASHED;
	public static final String PLUGIN_AUTHOR = RewindVersion.PLUGIN_AUTHOR;
	public static final String PLUGIN_VERSION = RewindVersion.PLUGIN_VERSION;
	public static final String PLUGIN_DESC = RewindVersion.PLUGIN_DESC;

	private JavaLogger logger;
	private IEaglerXRewindProtocol<ProxiedPlayer, ?> protocol;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		protocol = RewindFactory.createRewind(this);
	}

	@Override
	public void onEnable() {
		EaglerXServerAPI.instance().getEaglerXRewindService().registerLegacyProtocol(protocol);
	}

	@Override
	public void onDisable() {
		EaglerXServerAPI.instance().getEaglerXRewindService().unregisterLegacyProtocol(protocol);
	}

	@Override
	public IRewindLogger logger() {
		return logger;
	}

}
