package net.lax1dude.eaglercraft.backend.server.api.rewind;

import java.util.List;

import javax.annotation.Nonnull;

public interface IOutboundInjector {

	public interface IMessage {
		void write(@Nonnull List<Object> output);
	}

	void injectOutbound(@Nonnull IMessage msg);

}
