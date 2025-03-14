package net.lax1dude.eaglercraft.backend.server.base.webserver;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreparedResponse;

public class PreparedResponse implements IPreparedResponse {

	protected final ByteBuf buffer;

	PreparedResponse(ByteBuf buffer) {
		this.buffer = buffer;
	}

	@Override
	public void retain() {
		buffer.retain();
	}

	@Override
	public boolean release() {
		return buffer.release();
	}

}
