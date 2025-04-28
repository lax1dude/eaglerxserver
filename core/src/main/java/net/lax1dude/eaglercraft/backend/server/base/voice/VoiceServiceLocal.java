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

package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings.ConfigDataVoiceService;

public class VoiceServiceLocal<PlayerObject> implements IVoiceServiceImpl<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final boolean allServer;
	private final boolean separateServer;
	private final Set<String> configServersEnabled;
	private final IVoiceChannel globalChannel;
	private final LoadingCache<String, IVoiceChannel> serverChannels;
	private Collection<ICEServerEntry> iceServers;
	private String[] iceServersStr;

	public VoiceServiceLocal(EaglerXServer<PlayerObject> server, ConfigDataVoiceService config) {
		this.server = server;
		this.allServer = config.isEnableVoiceChatAllServers();
		this.separateServer = config.isSeparateVoiceChannelsPerServer();
		this.configServersEnabled = config.getEnableVoiceChatOnServers();
		this.globalChannel = new ManagedChannel<>(this);
		serverChannels = separateServer ? CacheBuilder.newBuilder().weakValues().concurrencyLevel(8).initialCapacity(32)
				.build(new CacheLoader<String, IVoiceChannel>() {
					@Override
					public IVoiceChannel load(String key) throws Exception {
						return new ManagedChannel<>(VoiceServiceLocal.this);
					}
				}) : null;
	}

	@Override
	public void setICEServers(Collection<ICEServerEntry> newICEServers) {
		if (newICEServers == null) {
			throw new NullPointerException("newICEServers");
		}
		newICEServers = iceServers = ImmutableList.copyOf(newICEServers);
		iceServersStr = prepareICEServers(newICEServers);
	}

	static String[] prepareICEServers(Collection<ICEServerEntry> newICEServers) {
		String[] newArray = new String[newICEServers.size()];
		int i = 0;
		for (ICEServerEntry etr : newICEServers) {
			newArray[i++] = etr.toString();
		}
		if (i != newArray.length) {
			throw new IllegalStateException("fuck you");
		}
		return newArray;
	}

	@Override
	public Collection<ICEServerEntry> getICEServers() {
		return iceServers;
	}

	String[] getICEServersStr() {
		return iceServersStr;
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isVoiceEnabled() {
		return true;
	}

	@Override
	public boolean isBackendRelayMode() {
		return false;
	}

	@Override
	public boolean isVoiceEnabledAllServers() {
		return allServer;
	}

	@Override
	public boolean isVoiceEnabledOnServer(String serverName) {
		if (serverName == null) {
			throw new NullPointerException("serverName");
		}
		return allServer || configServersEnabled.contains(serverName);
	}

	@Override
	public boolean isSeparateServerChannels() {
		return separateServer;
	}

	@Override
	public IVoiceChannel createVoiceChannel() {
		return new VoiceChannel<>(this);
	}

	@Override
	public IVoiceChannel getGlobalVoiceChannel() {
		return globalChannel;
	}

	@Override
	public IVoiceChannel getServerVoiceChannel(String serverName) {
		if (serverName == null) {
			throw new NullPointerException("serverName");
		}
		if (allServer || configServersEnabled.contains(serverName)) {
			if (separateServer) {
				try {
					return serverChannels.get(serverName);
				} catch (ExecutionException e) {
					if (e.getCause() instanceof RuntimeException ee)
						throw ee;
					throw new RuntimeException(e.getCause());
				}
			} else {
				return globalChannel;
			}
		} else {
			return DisabledChannel.INSTANCE;
		}
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		return DisabledChannel.INSTANCE;
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		if (channel == null) {
			throw new NullPointerException("Voice channel cannot be null!");
		}
		if (channel == DisabledChannel.INSTANCE) {
			throw new UnsupportedOperationException("Cannot list players connected to the disabled channel");
		}
		if (!(channel instanceof VoiceChannel ch) || ch.owner != this) {
			throw new IllegalArgumentException("Unknown voice channel");
		}
		return ch.listConnectedPlayers();
	}

	@Override
	public VoiceManagerLocal<PlayerObject> createVoiceManager(EaglerPlayerInstance<PlayerObject> player) {
		return player.hasCapability(EnumCapabilitySpec.VOICE_V0) ? new VoiceManagerLocal<>(player, this) : null;
	}

}
