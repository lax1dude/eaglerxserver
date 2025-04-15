package net.lax1dude.eaglercraft.backend.server.api.query;

import javax.annotation.Nonnull;

public interface IQueryServer {

	boolean isQueryType(@Nonnull String queryType);

	void registerQueryType(@Nonnull Object plugin, @Nonnull String queryType, @Nonnull IQueryHandler handler);

	void unregisterQueryType(@Nonnull Object plugin, @Nonnull String queryType);

}
