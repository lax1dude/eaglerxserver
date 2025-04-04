package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManagerX;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceServiceX;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;

public class VoiceManagerLocal<PlayerObject> implements IVoiceManagerX<PlayerObject> {

	private final VoiceServiceLocal<PlayerObject> service;
	private final EaglerPlayerLocal<PlayerObject> player;
	private final net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManagerX<PlayerObject> delegate;

	VoiceManagerLocal(VoiceServiceLocal<PlayerObject> service, EaglerPlayerLocal<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManagerX<PlayerObject> delegate) {
		this.service = service;
		this.player = player;
		this.delegate = delegate;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IVoiceServiceX<PlayerObject> getVoiceService() {
		return service;
	}

	@Override
	public EnumVoiceState getVoiceState() {
		return delegate.getVoiceState();
	}

	@Override
	public IVoiceChannel getVoiceChannel() {
		return delegate.getVoiceChannel();
	}

	@Override
	public void setVoiceChannel(IVoiceChannel channel) {
		delegate.setVoiceChannel(channel);
	}

}
