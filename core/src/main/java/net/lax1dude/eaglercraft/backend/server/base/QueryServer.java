package net.lax1dude.eaglercraft.backend.server.base;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
