package net.lax1dude.eaglercraft.backend.server.api.query;

import javax.annotation.Nonnull;

public interface IQueryHandler {

	void handleQuery(@Nonnull IQueryConnection connection);

}
