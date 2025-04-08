package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public class VoiceManagerLocal<PlayerObject> implements IVoiceManager<PlayerObject> {

	private final VoiceServiceLocal<PlayerObject> service;
	private final EaglerPlayerLocal<PlayerObject> player;
	private final net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager<PlayerObject> delegate;

	VoiceManagerLocal(VoiceServiceLocal<PlayerObject> service, EaglerPlayerLocal<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager<PlayerObject> delegate) {
		this.service = service;
		this.player = player;
		this.delegate = delegate;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IVoiceService<PlayerObject> getVoiceService() {
		return service;
	}

	@Override
	public EnumVoiceState getVoiceState() {
		return VoiceChannelHelper.wrap(delegate.getVoiceState());
	}

	@Override
	public IVoiceChannel getVoiceChannel() {
		return service.wrapConst(delegate.getVoiceChannel());
	}

	@Override
	public void setVoiceChannel(IVoiceChannel channel) {
		delegate.setVoiceChannel(VoiceChannelHelper.unwrap(channel));
	}

}
