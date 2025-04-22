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

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import net.lax1dude.eaglercraft.backend.rpc.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;

public class BukkitEventDispatchAdapter implements IEventDispatchAdapter<Player> {

	private IEaglerXBackendRPC<Player> api;
	private final Plugin platformPlugin;
	private final PluginManager eventMgr;

	public BukkitEventDispatchAdapter(Plugin platformPlugin, PluginManager eventMgr) {
		this.platformPlugin = platformPlugin;
		this.eventMgr = eventMgr;
	}

	@Override
	public void setAPI(IEaglerXBackendRPC<Player> api) {
		this.api = api;
	}

	@Override
	public void dispatchPlayerReadyEvent(IEaglerPlayer<Player> player) {
		eventMgr.callEvent(new BukkitPlayerReadyEventImpl(api, player));
	}

	@Override
	public BukkitVoiceCapableEventImpl dispatchVoiceCapableEvent(IEaglerPlayer<Player> player, IVoiceChannel channel) {
		BukkitVoiceCapableEventImpl evt = new BukkitVoiceCapableEventImpl(api, player, channel);
		eventMgr.callEvent(evt);
		return evt;
	}

	@Override
	public void dispatchVoiceChangeEvent(IEaglerPlayer<Player> player, EnumVoiceState stateOld, EnumVoiceState stateNew) {
		eventMgr.callEvent(new BukkitVoiceChangeEventImpl(api, player, stateOld, stateNew));
	}

}
