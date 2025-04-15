package net.lax1dude.eaglercraft.backend.server.api.query;

import javax.annotation.Nonnull;

public interface IDuplexStringHandler extends IDuplexBaseHandler {

	void handleString(@Nonnull IQueryConnection connection, @Nonnull String string);

}
