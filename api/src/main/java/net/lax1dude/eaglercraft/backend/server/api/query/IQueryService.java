package net.lax1dude.eaglercraft.backend.server.api.query;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IQueryService<PlayerObject> extends IServerIconLoader {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	boolean isQueryType(String queryType);

	void registerQueryType(String queryType, IQueryHandler handler);

	void unregisterQueryType(String queryType);

}
