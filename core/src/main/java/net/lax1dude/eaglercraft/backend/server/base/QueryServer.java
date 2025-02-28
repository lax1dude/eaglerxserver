package net.lax1dude.eaglercraft.backend.server.base;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.api.query.IQueryHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryServer;

public class QueryServer implements IQueryServer {

	private final EaglerXServer<?> server;
	private final ReadWriteLock registeredQueriesLock;
	private final Map<String, IQueryHandler> registeredQueries;

	public QueryServer(EaglerXServer<?> server) {
		this.server = server;
		this.registeredQueriesLock = new ReentrantReadWriteLock();
		this.registeredQueries = new HashMap<>();
	}

	public IQueryHandler getHandlerFor(String queryType) {
		registeredQueriesLock.readLock().lock();
		try {
			return registeredQueries.get(queryType);
		}finally {
			registeredQueriesLock.readLock().unlock();
		}
	}

	private JsonObject createBaseResponse() {
		JsonObject json = new JsonObject();
		json.addProperty("name", server.getServerName());
		json.addProperty("brand", "lax1dude");
		json.addProperty("vers", server.getServerVersionString());
		json.addProperty("cracked", true);
		json.addProperty("time", System.currentTimeMillis());
		json.addProperty("uuid", server.getServerUUIDString());
		return json;
	}

	public JsonObject createStringResponse(String type, String str) {
		JsonObject ret = createBaseResponse();
		ret.addProperty("type", type);
		ret.addProperty("data", str);
		return ret;
	}

	public JsonObject createJsonObjectResponse(String type, JsonObject json) {
		JsonObject ret = createBaseResponse();
		ret.addProperty("type", type);
		ret.add("data", json);
		return ret;
	}

	@Override
	public boolean isQueryType(String queryType) {
		registeredQueriesLock.readLock().lock();
		try {
			return registeredQueries.containsKey(queryType.toLowerCase(Locale.US));
		}finally {
			registeredQueriesLock.readLock().unlock();
		}
	}

	@Override
	public void registerQueryType(String queryType, IQueryHandler handler) {
		boolean warn;
		registeredQueriesLock.writeLock().lock();
		try {
			warn = registeredQueries.put(queryType.toLowerCase(Locale.US), handler) != null;
		}finally {
			registeredQueriesLock.writeLock().unlock();
		}
		if (warn) {
			server.logger().warn("Query type \"" + queryType + "\" was registered multiple times!",
					new RuntimeException("Stack trace"));
		}
	}

	@Override
	public void unregisterQueryType(String queryType) {
		registeredQueriesLock.writeLock().lock();
		try {
			registeredQueries.remove(queryType.toLowerCase(Locale.US));
		}finally {
			registeredQueriesLock.writeLock().unlock();
		}
	}

}
