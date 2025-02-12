package net.lax1dude.eaglercraft.eaglerxserver.api.query;

import net.lax1dude.eaglercraft.eaglerxserver.api.IEaglerXServerAPI;

public interface IWebSocketQueryService<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	boolean isQueryType(String queryType);

	void registerQueryType(String queryType, IWebSocketQueryType handler);

	void registerQueryTypeSimple(String queryType, IWebSocketQuerySimple handler);

	void unregisterQueryType(String queryType);

}
