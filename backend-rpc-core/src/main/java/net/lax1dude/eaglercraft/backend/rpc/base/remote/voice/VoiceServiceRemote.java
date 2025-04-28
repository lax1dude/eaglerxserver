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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.EaglerXBackendRPCRemote;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.PlayerInstanceRemote;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.config.ConfigDataSettings.ConfigDataBackendVoice;

public class VoiceServiceRemote<PlayerObject> implements IVoiceServiceImpl<PlayerObject> {

	private final EaglerXBackendRPCRemote<PlayerObject> server;
	private final boolean allWorld;
	private final boolean separateWorld;
	private final Set<String> configWorldsEnabled;
	private final IVoiceChannel globalChannel;
	private final LoadingCache<String, IVoiceChannel> worldChannels;
	private Collection<ICEServerEntry> iceServers;
	private String[] iceServersStr;
	private boolean iceOverride = false;

	public VoiceServiceRemote(EaglerXBackendRPCRemote<PlayerObject> server, ConfigDataBackendVoice config) {
		this.server = server;
		this.allWorld = config.isEnableVoiceChatAllWorlds();
		this.separateWorld = config.isSeparateVoiceChannelsPerWorld();
		this.configWorldsEnabled = config.getEnableVoiceChatOnWorlds();
		this.globalChannel = new ManagedChannel<>(this);
		worldChannels = separateWorld ? CacheBuilder.newBuilder().weakValues().concurrencyLevel(8).initialCapacity(32)
				.build(new CacheLoader<String, IVoiceChannel>() {
					@Override
					public IVoiceChannel load(String key) throws Exception {
						return new ManagedChannel<>(VoiceServiceRemote.this);
					}
				}) : null;
	}

	@Override
	public VoiceManagerRemote<PlayerObject> createVoiceManager(PlayerInstanceRemote<PlayerObject> player) {
		return new VoiceManagerRemote<>(player, this);
	}

	@Override
	public void handleWorldChanged(PlayerInstanceRemote<PlayerObject> player, String worldName) {
		IVoiceManager<PlayerObject> voiceMgr = player.getVoiceManager();
		if (voiceMgr != null) {
			((VoiceManagerRemote<PlayerObject>) voiceMgr).handleWorldChanged(worldName);
		}
	}

	@Override
	public EaglerXBackendRPCRemote<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isVoiceEnabled() {
		return true;
	}

	@Override
	public boolean isVoiceEnabledAllWorlds() {
		return allWorld;
	}

	@Override
	public boolean isVoiceEnabledOnWorld(String worldName) {
		if (worldName == null) {
			throw new NullPointerException("worldName");
		}
		return allWorld || configWorldsEnabled.contains(worldName);
	}

	@Override
	public boolean isSeparateWorldChannels() {
		return separateWorld;
	}

	@Override
	public Collection<ICEServerEntry> getICEServers() {
		return iceServers;
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

	String[] getICEServersStr() {
		return iceServersStr;
	}

	@Override
	public boolean getOverrideICEServers() {
		return iceOverride;
	}

	@Override
	public void setOverrideICEServers(boolean enable) {
		iceOverride = enable;
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
	public IVoiceChannel getWorldVoiceChannel(String worldName) {
		if (worldName == null) {
			throw new NullPointerException("worldName");
		}
		if (allWorld || configWorldsEnabled.contains(worldName)) {
			if (separateWorld) {
				try {
					return worldChannels.get(worldName);
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

}
