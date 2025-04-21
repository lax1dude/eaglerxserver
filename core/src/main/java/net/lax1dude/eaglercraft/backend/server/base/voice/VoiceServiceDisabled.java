package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.util.Collection;
import java.util.Collections;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class VoiceServiceDisabled<PlayerObject> implements IVoiceServiceImpl<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;

	public VoiceServiceDisabled(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isBackendRelayMode() {
		return false;
	}

	@Override
	public boolean isVoiceEnabled() {
		return false;
	}

	@Override
	public boolean isVoiceEnabledAllServers() {
		return false;
	}

	@Override
	public boolean isVoiceEnabledOnServer(String serverName) {
		if(serverName == null) {
			throw new NullPointerException("serverName");
		}
		return false;
	}

	@Override
	public boolean isSeparateServerChannels() {
		return false;
	}

	@Override
	public Collection<ICEServerEntry> getICEServers() {
		return Collections.emptyList();
	}

	@Override
	public void setICEServers(Collection<ICEServerEntry> servers) {
		if(servers == null) {
			throw new NullPointerException("servers");
		}
	}

	@Override
	public IVoiceChannel createVoiceChannel() {
		throw disabledError();
	}

	@Override
	public IVoiceChannel getGlobalVoiceChannel() {
		throw disabledError();
	}

	@Override
	public IVoiceChannel getServerVoiceChannel(String serverName) {
		if(serverName == null) {
			throw new NullPointerException("serverName");
		}
		throw disabledError();
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		throw disabledError();
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		if(channel == null) {
			throw new NullPointerException("channel");
		}
		throw disabledError();
	}

	@Override
	public IVoiceManagerImpl<PlayerObject> createVoiceManager(EaglerPlayerInstance<PlayerObject> player) {
		return null;
	}

	private static RuntimeException disabledError() {
		return new IllegalStateException("Voice service is disabled!");
	}

}
