package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.rpc.BackendChannelHelper;

public class VoiceServiceRemote<PlayerObject> implements IVoiceServiceImpl<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final String rpcChannel;
	private Collection<ICEServerEntry> iceServers;
	private String[] iceServersStr;

	public VoiceServiceRemote(EaglerXServer<PlayerObject> server) {
		this.server = server;
		this.rpcChannel = BackendChannelHelper.getRPCVoiceChannel(server);
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	String getRPCChannel() {
		return rpcChannel;
	}

	@Override
	public boolean isVoiceEnabled() {
		return true;
	}

	@Override
	public boolean isVoiceEnabledAllServers() {
		return true;
	}

	@Override
	public boolean isBackendRelayMode() {
		return true;
	}

	@Override
	public boolean isVoiceEnabledOnServer(String serverName) {
		if(serverName == null) {
			throw new NullPointerException("serverName");
		}
		return true;
	}

	@Override
	public boolean isSeparateServerChannels() {
		return true;
	}

	@Override
	public Collection<ICEServerEntry> getICEServers() {
		return iceServers;
	}

	@Override
	public void setICEServers(Collection<ICEServerEntry> newICEServers) {
		if(newICEServers == null) {
			throw new NullPointerException("newICEServers");
		}
		newICEServers = iceServers = ImmutableList.copyOf(newICEServers);
		iceServersStr = VoiceServiceLocal.prepareICEServers(newICEServers);
	}

	String[] iceServersStr() {
		return iceServersStr;
	}

	@Override
	public IVoiceChannel createVoiceChannel() {
		throw backendRelayMode();
	}

	@Override
	public IVoiceChannel getGlobalVoiceChannel() {
		throw backendRelayMode();
	}

	@Override
	public IVoiceChannel getServerVoiceChannel(String serverName) {
		if(serverName == null) {
			throw new NullPointerException("serverName");
		}
		throw backendRelayMode();
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		throw backendRelayMode();
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		if(channel == null) {
			throw new NullPointerException("channel");
		}
		throw backendRelayMode();
	}

	@Override
	public IVoiceManagerImpl<PlayerObject> createVoiceManager(EaglerPlayerInstance<PlayerObject> player) {
		return player.hasCapability(EnumCapabilitySpec.VOICE_V0) ? new VoiceManagerRemote<>(player, this) : null;
	}

	static RuntimeException backendRelayMode() {
		return new IllegalStateException("Voice service is in backend-relayed mode!");
	}

}
