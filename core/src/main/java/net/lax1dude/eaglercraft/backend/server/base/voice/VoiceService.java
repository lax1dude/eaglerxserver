package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.lax1dude.eaglercraft.backend.server.api.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings.ConfigDataVoiceService;

public class VoiceService<PlayerObject> implements IVoiceService<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final boolean enabled;
	private final boolean allServer;
	private final boolean separateServer;
	private final IVoiceChannel globalChannel;
	private final Map<String, IVoiceChannel> serverChannels;
	private String[] iceServers = new String[0];

	public VoiceService(EaglerXServer<PlayerObject> server, ConfigDataVoiceService config) {
		this.server = server;
		if(!config.isEnableVoiceService()) {
			this.enabled = false;
			this.allServer = false;
			this.separateServer = false;
			this.globalChannel = null;
			this.serverChannels = Collections.emptyMap();
		}else {
			this.enabled = true;
			Collection<String> toEnable;
			Set<String> registeredServers = server.getPlatform().getRegisteredServers().keySet();
			if(config.isEnableVoiceChatAllServers()) {
				this.allServer = true;
				toEnable = registeredServers;
			}else {
				this.allServer = false;
				toEnable = new ArrayList<>();
				for(String str : config.getEnableVoiceChatOnServers()) {
					if(registeredServers.contains(str)) {
						toEnable.add(str);
					}else {
						server.logger().warn("Unknown server defined in voice service config: \"" + str + "\"");
					}
				}
			}
			this.globalChannel = new ManagedChannel(this);
			ImmutableMap.Builder<String, IVoiceChannel> builder = ImmutableMap.builder();
			if(config.isSeparateVoiceChannelsPerServer()) {
				this.separateServer = true;
				for(String str : toEnable) {
					builder.put(str, new ManagedChannel(this));
				}
			}else {
				this.separateServer = false;
				for(String str : toEnable) {
					builder.put(str, this.globalChannel);
				}
			}
			this.serverChannels = builder.build();
		}
	}

	public void handleICEServerUpdate(Collection<ICEServerEntry> newICEServers) {
		String[] newArray = new String[newICEServers.size()];
		int i = 0;
		for(ICEServerEntry etr : newICEServers) {
			newArray[i++] = iceServerToStr(etr);
		}
		if(i != newArray.length) {
			throw new IllegalStateException("fuck you");
		}
		iceServers = newArray;
	}

	private String iceServerToStr(ICEServerEntry etr) {
		if(etr.isAuthenticated()) {
			return etr.getURI() + ";" + etr.getUsername() + ";" + etr.getPassword();
		}else {
			return etr.getURI();
		}
	}

	String[] getICEServers() {
		return iceServers;
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isVoiceEnabled() {
		return enabled;
	}

	@Override
	public boolean isBackendRelayMode() {
		// TODO
		return false;
	}

	@Override
	public boolean isVoiceEnabledAllServers() {
		return allServer;
	}

	@Override
	public boolean isVoiceEnabledOnServer(String serverName) {
		return serverChannels.containsKey(serverName);
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
		return serverChannels.getOrDefault(serverName, DisabledChannel.INSTANCE);
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		return DisabledChannel.INSTANCE;
	}

	public VoiceManager<PlayerObject> createVoiceManager(EaglerPlayerInstance<PlayerObject> player) {
		return enabled ? new VoiceManager<>(player, this) : null;
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		if(channel == null) {
			throw new NullPointerException("Voice channel cannot be null!");
		}
		if(channel == DisabledChannel.INSTANCE) {
			throw new UnsupportedOperationException("Cannot list players connected to the disabled channel");
		}
		if(!(channel instanceof VoiceChannel) || ((VoiceChannel)channel).owner != this) {
			throw new IllegalArgumentException("Unknown voice channel");
		}
		return ((VoiceChannel<PlayerObject>)channel).listConnectedPlayers();
	}

}
