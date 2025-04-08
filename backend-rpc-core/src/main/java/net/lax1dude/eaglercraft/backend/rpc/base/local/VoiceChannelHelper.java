package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;

class VoiceChannelHelper {

	static IVoiceChannel wrap(net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel channel) {
		return new VoiceChannelLocal(channel);
	}

	static net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel unwrap(IVoiceChannel channel) {
		return ((VoiceChannelLocal) channel).channel;
	}

	static EnumVoiceState wrap(net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState state) {
		switch(state) {
		case SERVER_DISABLE:
		default:
			return EnumVoiceState.SERVER_DISABLE;
		case DISABLED:
			return EnumVoiceState.DISABLED;
		case ENABLED:
			return EnumVoiceState.ENABLED;
		}
	}

	static net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState unwrap(EnumVoiceState state) {
		switch(state) {
		case SERVER_DISABLE:
		default:
			return net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState.SERVER_DISABLE;
		case DISABLED:
			return net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState.DISABLED;
		case ENABLED:
			return net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState.ENABLED;
		}
	}

	static ICEServerEntry wrap(net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry etr) {
		return etr.isAuthenticated() ? ICEServerEntry.create(etr.getURI())
				: ICEServerEntry.create(etr.getURI(), etr.getUsername(), etr.getPassword());
	}

	static net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry unwrap(ICEServerEntry etr) {
		return etr.isAuthenticated()
				? net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry.create(etr.getURI())
				: net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry.create(etr.getURI(),
						etr.getUsername(), etr.getPassword());
	}

	static final class VoiceChannelLocal implements IVoiceChannel {

		final net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel channel;

		VoiceChannelLocal(net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel channel) {
			this.channel = channel;
		}

		@Override
		public boolean isManaged() {
			return channel.isManaged();
		}

		@Override
		public boolean isDisabled() {
			return channel.isDisabled();
		}

		@Override
		public int hashCode() {
			return channel.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj || ((obj instanceof VoiceChannelLocal) && channel.equals(((VoiceChannelLocal)obj).channel));
		}

	}

}
