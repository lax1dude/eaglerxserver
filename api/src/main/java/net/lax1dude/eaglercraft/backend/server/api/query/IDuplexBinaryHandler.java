package net.lax1dude.eaglercraft.backend.server.api.query;

import javax.annotation.Nonnull;

public interface IDuplexBinaryHandler extends IDuplexBaseHandler {

	void handleBinary(@Nonnull IQueryConnection connection, @Nonnull byte[] binary);

}
