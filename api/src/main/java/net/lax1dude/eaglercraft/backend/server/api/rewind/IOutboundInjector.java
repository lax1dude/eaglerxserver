package net.lax1dude.eaglercraft.backend.server.api.rewind;

import java.util.List;

public interface IOutboundInjector {

	public interface IMessage {
		void write(List<Object> output);
	}

	void injectOutbound(IMessage msg);

}
