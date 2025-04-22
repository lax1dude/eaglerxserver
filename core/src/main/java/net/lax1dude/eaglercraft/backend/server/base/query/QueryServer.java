/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.base.query;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.api.query.IQueryHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryServer;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class QueryServer implements IQueryServer {

	private final EaglerXServer<?> server;
	private final ReadWriteLock registeredQueriesLock;
	private final Map<String, QueryEntry> registeredQueries;
	private final String platform;

	public QueryServer(EaglerXServer<?> server) {
		this.server = server;
		this.registeredQueriesLock = new ReentrantReadWriteLock();
		this.registeredQueries = new HashMap<>();
		this.registeredQueries.put("eagler", new QueryEntry(server, new QueryHandlerEagler()));
		this.registeredQueries.put("version", new QueryEntry(server, new QueryHandlerVersion(server)));
		this.registeredQueries.put("revoke_session_token", new QueryEntry(server, new QueryHandlerRevoke(server)));
		this.platform = server.getPlatform().getType().name().toLowerCase();
	}

	public IQueryHandler getHandlerFor(String queryType) {
		registeredQueriesLock.readLock().lock();
		try {
			QueryEntry etr = registeredQueries.get(queryType);
			return etr != null ? etr.handler : null;
		}finally {
			registeredQueriesLock.readLock().unlock();
		}
	}

	private JsonObject createBaseResponse() {
		JsonObject json = new JsonObject();
		json.addProperty("name", server.getServerName());
		json.addProperty("brand", "lax1dude");
		json.addProperty("vers", server.getServerVersionString());
		json.addProperty("plaf", platform);
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
		if(queryType == null) {
			throw new NullPointerException("queryType");
		}
		registeredQueriesLock.readLock().lock();
		try {
			return registeredQueries.containsKey(queryType.toLowerCase(Locale.US));
		}finally {
			registeredQueriesLock.readLock().unlock();
		}
	}

	@Override
	public void registerQueryType(Object plugin, String queryType, IQueryHandler handler) {
		if(plugin == null) {
			throw new NullPointerException("plugin");
		}
		if(queryType == null) {
			throw new NullPointerException("queryType");
		}
		if(handler == null) {
			throw new NullPointerException("handler");
		}
		queryType = queryType.toLowerCase(Locale.US);
		if("motd".equals(queryType) || queryType.startsWith("motd.")) {
			throw new UnsupportedOperationException("Cannot replace the default MOTD handler");
		}
		registeredQueriesLock.writeLock().lock();
		try {
			QueryEntry etr = registeredQueries.get(queryType);
			if(etr != null) {
				throw new IllegalStateException("Query type \"" + queryType + "\" is already registered by: " + etr.plugin);
			}
			registeredQueries.put(queryType, new QueryEntry(plugin, handler));
		}finally {
			registeredQueriesLock.writeLock().unlock();
		}
	}

	@Override
	public void unregisterQueryType(Object plugin, String queryType) {
		if(plugin == null) {
			throw new NullPointerException("plugin");
		}
		if(queryType == null) {
			throw new NullPointerException("queryType");
		}
		registeredQueriesLock.writeLock().lock();
		try {
			queryType = queryType.toLowerCase(Locale.US);
			QueryEntry etr = registeredQueries.get(queryType);
			if(etr != null) {
				if(etr.plugin != plugin) {
					throw new IllegalStateException("Query type is registered by a different plugin: " + etr.plugin);
				}
				registeredQueries.remove(queryType);
			}
		}finally {
			registeredQueriesLock.writeLock().unlock();
		}
	}

	private static class QueryEntry {

		protected final Object plugin;
		protected final IQueryHandler handler;

		protected QueryEntry(Object plugin, IQueryHandler handler) {
			this.plugin = plugin;
			this.handler = handler;
		}

	}

}
