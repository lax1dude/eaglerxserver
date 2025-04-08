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
		throw disabledError();
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		throw disabledError();
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
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
