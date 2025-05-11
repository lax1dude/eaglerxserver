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

package net.lax1dude.eaglercraft.backend.rpc.bukkit.event;

import java.util.function.Consumer;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import net.lax1dude.eaglercraft.backend.rpc.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.event.IEaglercraftVoiceCapableEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;

public class BukkitEventDispatchAdapter implements IEventDispatchAdapter<Player> {

	private IEaglerXBackendRPC<Player> api;
	private final Plugin platformPlugin;
	private final Server server;
	private final PluginManager eventMgr;

	public BukkitEventDispatchAdapter(Plugin platformPlugin, Server server, PluginManager eventMgr) {
		this.platformPlugin = platformPlugin;
		this.server = server;
		this.eventMgr = eventMgr;
	}

	@Override
	public void setAPI(IEaglerXBackendRPC<Player> api) {
		this.api = api;
	}

	private <T extends Event> void fire(T event, Consumer<? super T> callback) {
		if (server.isPrimaryThread()) {
			eventMgr.callEvent(event);
			if (callback != null) {
				callback.accept(event);
			}
		} else {
			server.getScheduler().runTask(platformPlugin, () -> {
				eventMgr.callEvent(event);
				if (callback != null) {
					callback.accept(event);
				}
			});
		}
	}

	@Override
	public void dispatchPlayerReadyEvent(IEaglerPlayer<Player> player) {
		fire(new BukkitPlayerReadyEventImpl(api, player), null);
	}

	@Override
	public void dispatchVoiceCapableEvent(IEaglerPlayer<Player> player, IVoiceChannel channel,
			Consumer<IEaglercraftVoiceCapableEvent<Player>> callback) {
		fire(new BukkitVoiceCapableEventImpl(api, player, channel), callback);
	}

	@Override
	public void dispatchVoiceChangeEvent(IEaglerPlayer<Player> player, EnumVoiceState stateOld,
			EnumVoiceState stateNew) {
		fire(new BukkitVoiceChangeEventImpl(api, player, stateOld, stateNew), null);
	}

}
