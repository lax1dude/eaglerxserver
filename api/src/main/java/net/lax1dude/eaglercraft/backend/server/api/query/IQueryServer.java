package net.lax1dude.eaglercraft.backend.server.api.query;

public interface IQueryServer {

	boolean isQueryType(String queryType);

	void registerQueryType(String queryType, IQueryHandler handler);

	void unregisterQueryType(String queryType);

}
