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

package net.lax1dude.eaglercraft.backend.rewind_v1_5.velocity;

import java.nio.file.Path;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.IRewindLogger;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.IRewindPlatform;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.RewindProtocol;
import net.lax1dude.eaglercraft.backend.server.api.velocity.EaglerXServerAPI;

@Plugin(
	id = PlatformPluginVelocity.PLUGIN_ID,
	name = PlatformPluginVelocity.PLUGIN_NAME,
	authors = {
		"lax1dude",
		"ayunami2000"
	},
	version = PlatformPluginVelocity.PLUGIN_VERSION,
	description = PlatformPluginVelocity.PLUGIN_DESC,
	dependencies = {
		@Dependency(id = EaglerXServerAPI.PLUGIN_ID, optional = false)
	}
)
public class PlatformPluginVelocity implements IRewindPlatform<Player> {

	public static final String PLUGIN_ID = "eaglerxrewind-1-5";
	public static final String PLUGIN_NAME = "EaglercraftXRewind-1.5";
	public static final String PLUGIN_AUTHOR = "ayunami2000";
	public static final String PLUGIN_VERSION = "1.0.0";
	public static final String PLUGIN_DESC = "Official Eaglercraft 1.5 compatibility plugin for EaglercraftXServer";

	private final ProxyServer proxy;
	private final Logger logger;
	private final SLF4JLogger rewindLogger;
	private final RewindProtocol<Player> protocol;

	@Inject
	public PlatformPluginVelocity(ProxyServer proxyIn, Logger loggerIn, @DataDirectory Path dataDirIn) {
		proxy = proxyIn;
		logger = loggerIn;
		rewindLogger = new SLF4JLogger(loggerIn);
		protocol = new RewindProtocol<Player>(this);
	}

	@Subscribe
	public void onProxyInit(ProxyInitializeEvent e) {
		EaglerXServerAPI.instance().getEaglerXRewindService().registerLegacyProtocol(protocol);
	}

	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent e) {
		EaglerXServerAPI.instance().getEaglerXRewindService().unregisterLegacyProtocol(protocol);
	}

	@Override
	public IRewindLogger logger() {
		return rewindLogger;
	}

}
