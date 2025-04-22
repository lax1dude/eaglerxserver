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

package net.lax1dude.eaglercraft.backend.eaglermotd.bungee;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.eaglermotd.base.EaglerMOTD;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.IEaglerMOTDLogger;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.IEaglerMOTDPlatform;
import net.lax1dude.eaglercraft.backend.server.api.bungee.EaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class PlatformPluginBungee extends Plugin implements IEaglerMOTDPlatform<ProxiedPlayer> {

	private JavaLogger logger;
	private EaglerMOTD<ProxiedPlayer> eaglermotd;
	Consumer<IEaglercraftMOTDEvent<ProxiedPlayer>> onMOTDHandler;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		eaglermotd = new EaglerMOTD<ProxiedPlayer>(this);
	}

	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, new BungeeListener(this));
		eaglermotd.onEnable(EaglerXServerAPI.instance());
	}

	@Override
	public void onDisable() {
		getProxy().getPluginManager().unregisterListeners(this);
		eaglermotd.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerMOTDLogger logger() {
		return logger;
	}

	@Override
	public void setOnMOTD(Consumer<IEaglercraftMOTDEvent<ProxiedPlayer>> handler) {
		this.onMOTDHandler = handler;
	}

}
