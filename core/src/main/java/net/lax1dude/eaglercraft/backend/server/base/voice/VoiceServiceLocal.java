package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
	private final IVoiceChannel globalChannel;
	private final Map<String, IVoiceChannel> serverChannels;
	private Collection<ICEServerEntry> iceServers;
	private String[] iceServersStr;

	public VoiceServiceLocal(EaglerXServer<PlayerObject> server, ConfigDataVoiceService config) {
		this.server = server;
		Collection<String> toEnable;
		Set<String> registeredServers = server.getPlatform().getRegisteredServers().keySet();
		if(config.isEnableVoiceChatAllServers()) {
			this.allServer = true;
			toEnable = registeredServers;
		}else {
			this.allServer = false;
			toEnable = new HashSet<>();
			for(String str : config.getEnableVoiceChatOnServers()) {
				if(registeredServers.contains(str)) {
					toEnable.add(str);
				}else {
					server.logger().warn("Unknown server defined in voice service config: \"" + str + "\"");
				}
			}
		}
		this.globalChannel = new ManagedChannel<>(this);
		ImmutableMap.Builder<String, IVoiceChannel> builder = ImmutableMap.builder();
		if(config.isSeparateVoiceChannelsPerServer()) {
			this.separateServer = true;
			for(String str : toEnable) {
				builder.put(str, new ManagedChannel<>(this));
			}
		}else {
			this.separateServer = false;
			for(String str : toEnable) {
				builder.put(str, this.globalChannel);
			}
		}
		this.serverChannels = builder.build();
	}

	@Override
	public void setICEServers(Collection<ICEServerEntry> newICEServers) {
		newICEServers = iceServers = ImmutableList.copyOf(newICEServers);
		iceServersStr = prepareICEServers(newICEServers);
	}

	static String[] prepareICEServers(Collection<ICEServerEntry> newICEServers) {
		String[] newArray = new String[newICEServers.size()];
		int i = 0;
		for(ICEServerEntry etr : newICEServers) {
			newArray[i++] = etr.toString();
		}
		if(i != newArray.length) {
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

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		if(channel == null) {
			throw new NullPointerException("Voice channel cannot be null!");
		}
		if(channel == DisabledChannel.INSTANCE) {
			throw new UnsupportedOperationException("Cannot list players connected to the disabled channel");
		}
		if(!(channel instanceof VoiceChannel) || ((VoiceChannel<?>)channel).owner != this) {
			throw new IllegalArgumentException("Unknown voice channel");
		}
		return ((VoiceChannel<PlayerObject>)channel).listConnectedPlayers();
	}

	@Override
	public VoiceManagerLocal<PlayerObject> createVoiceManager(EaglerPlayerInstance<PlayerObject> player) {
		return player.hasCapability(EnumCapabilitySpec.VOICE_V0) ? new VoiceManagerLocal<>(player, this) : null;
	}

}
