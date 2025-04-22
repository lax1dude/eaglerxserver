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

package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftInitializePlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftWebViewMessageEvent;

class BukkitListenerLocal implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListenerLocal(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInitializePlayer(EaglercraftInitializePlayerEvent evt) {
		if(plugin.initializePlayerHandler != null) {
			plugin.initializePlayerHandler.accept(evt);
		}
	}

	@EventHandler
	public void onWebViewChannel(EaglercraftWebViewChannelEvent evt) {
		if(plugin.localWebViewChannelHandler != null) {
			plugin.localWebViewChannelHandler.accept(evt);
		}
	}

	@EventHandler
	public void onWebViewMessage(EaglercraftWebViewMessageEvent evt) {
		if(plugin.localWebViewMessageHandler != null) {
			plugin.localWebViewMessageHandler.accept(evt);
		}
	}

	@EventHandler
	public void onToggleVoice(EaglercraftVoiceChangeEvent evt) {
		if(plugin.localVoiceChangeHandler != null) {
			plugin.localVoiceChangeHandler.accept(evt);
		}
	}

}
