package net.lax1dude.eaglercraft.backend.server.api.query;

public interface IQueryServer extends IServerIconLoader {

	boolean isQueryType(String queryType);

	void registerQueryType(String queryType, IQueryHandler handler);

	void unregisterQueryType(String queryType);

}
