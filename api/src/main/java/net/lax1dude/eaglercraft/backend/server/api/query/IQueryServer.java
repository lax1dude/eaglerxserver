package net.lax1dude.eaglercraft.backend.server.api.query;

public interface IQueryServer {

	boolean isQueryType(String queryType);

	void registerQueryType(Object plugin, String queryType, IQueryHandler handler);

	void unregisterQueryType(Object plugin, String queryType);

}
