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
		return switch(state) {
		case SERVER_DISABLE -> EnumVoiceState.SERVER_DISABLE;
		case DISABLED -> EnumVoiceState.DISABLED;
		case ENABLED -> EnumVoiceState.ENABLED;
		default -> EnumVoiceState.SERVER_DISABLE;
		};
	}

	static net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState unwrap(EnumVoiceState state) {
		return switch (state) {
		case SERVER_DISABLE -> net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState.SERVER_DISABLE;
		case DISABLED -> net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState.DISABLED;
		case ENABLED -> net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState.ENABLED;
		default -> net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState.SERVER_DISABLE;
		};
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
			return this == obj || ((obj instanceof VoiceChannelLocal other) && channel.equals(other.channel));
		}

	}

}
