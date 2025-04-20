package net.lax1dude.eaglercraft.backend.eaglermotd.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.lax1dude.eaglercraft.backend.eaglermotd.base.frame.PipelineLoader;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.util.BitmapUtil;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.util.GsonUtil;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public class EaglerMOTDConfiguration {

	public final Map<String, List<MessagePoolEntry>> messages;
	public final Map<String, MessagePool> messagePools;
	public final Map<String, QueryType> queryTypes;
	public int close_socket_after = 1200;
	public int max_sockets_per_ip = 10;
	public int max_total_sockets = 256;

	public static EaglerMOTDConfiguration load(File pluginDir, IEaglerXServerAPI<?> serverAPI, IEaglerMOTDLogger logger, Set<String> listeners) throws IOException, JsonParseException {
		BitmapUtil bitmapUtil = new BitmapUtil();

		byte[] damn = new byte[4096];
		int i;

		File msgs = new File(pluginDir, "messages.json");

		if(!msgs.isFile()) {
			if(!pluginDir.isDirectory()) {
				if(!pluginDir.mkdirs()) {
					throw new IOException("Could not create directory: " + pluginDir.getAbsolutePath());
				}
			}
			try(OutputStream fileNew = new FileOutputStream(msgs)) {
				try(InputStream fileDefault = EaglerMOTDConfiguration.class.getResourceAsStream("default_messages.json")) {
					while((i = fileDefault.read(damn)) != -1) {
						fileNew.write(damn, 0, i);
					}
				}
			}
			File f2 = new File(pluginDir, "frames.json");
			if(!f2.isFile()) {
				try(OutputStream fileNew = new FileOutputStream(f2)) {
					try(InputStream fileDefault = EaglerMOTDConfiguration.class.getResourceAsStream("default_frames.json")) {
						while((i = fileDefault.read(damn)) != -1) {
							fileNew.write(damn, 0, i);
						}
					}
				}
			}
			f2 = new File(pluginDir, "queries.json");
			if(!f2.isFile()) {
				try(OutputStream fileNew = new FileOutputStream(f2)) {
					try(InputStream fileDefault = EaglerMOTDConfiguration.class.getResourceAsStream("default_queries.json")) {
						while((i = fileDefault.read(damn)) != -1) {
							fileNew.write(damn, 0, i);
						}
					}
				}
			}
			f2 = new File("server-animation.png");
			if(!f2.isFile()) {
				try(OutputStream fileNew = new FileOutputStream(f2)) {
					try(InputStream fileDefault = EaglerMOTDConfiguration.class.getResourceAsStream("server-icons-test.png")) {
						while((i = fileDefault.read(damn)) != -1) {
							fileNew.write(damn, 0, i);
						}
					}
				}
			}
		}

		if(!msgs.isFile()) {
			throw new NullPointerException("messages.json is missing and could not be created");
		}

		JsonObject msgsObj = GsonUtil.loadJSONFile(msgs);

		Map<String, JsonObject> framesCache = new HashMap<>();
		Map<String, List<MessagePoolEntry>> messages = new HashMap<>();
		framesCache.put("messages", msgsObj);
		int close_socket_after = GsonUtil.optInt(msgsObj.get("close_socket_after"), 1200);
		int max_sockets_per_ip = GsonUtil.optInt(msgsObj.get("max_sockets_per_ip"), 10);
		int max_total_sockets = GsonUtil.optInt(msgsObj.get("max_total_sockets"), 256);
		msgsObj = msgsObj.getAsJsonObject("messages");

		for(Entry<String, JsonElement> ss : GsonUtil.asMap(msgsObj).entrySet()) {
			List<MessagePoolEntry> poolEntries = new ArrayList<>();
			JsonArray arr = ss.getValue().getAsJsonArray();
			for(int j = 0, l = arr.size(); j < l; ++j) {
				JsonObject entry = arr.get(j).getAsJsonObject();
				List<JsonObject> framesRaw = new ArrayList<>();
				JsonArray framesJSON = entry.get("frames").getAsJsonArray();
				for(int k = 0, l2 = framesJSON.size(); k < l2; ++k) {
					framesRaw.add(loadFrameCache(framesCache, logger, pluginDir, framesJSON.get(k).getAsString()));
				}
				if(framesRaw.size() > 0) {
					poolEntries.add(new MessagePoolEntry(GsonUtil.optInt(entry.get("interval"), 0),
							GsonUtil.optInt(entry.get("timeout"), 500),
							GsonUtil.optFloat(entry.get("weight"), 1.0f),
							GsonUtil.optString(entry.get("next"), null),
							PipelineLoader.loadPipeline(serverAPI, bitmapUtil, framesRaw),
							GsonUtil.optString(entry.get("name"), null)));
				}else {
					logger.error("Message '" + ss.getKey() + "' has no frames!");
				}
			}
			if(poolEntries.size() > 0) {
				List<MessagePoolEntry> existingList = messages.get(ss.getKey());
				if(existingList == null) {
					existingList = poolEntries;
					messages.put(ss.getKey(), existingList);
				}else {
					existingList.addAll(poolEntries);
				}
			}
		}
		
		String flag = null;
		for(String s : messages.keySet()) {
			if(!s.equals("all")) {
				if(!listeners.contains(s)) {
					flag = s;
					break;
				}
			}
		}
		
		if(flag != null) {
			logger.error("Listener '" + flag + "' does not exist!");
			String hostsString = "";
			for(String l : listeners) {
				if(hostsString.length() > 0) {
					hostsString += " ";
				}
				hostsString += l;
			}
			logger.error("Listeners configured: " + hostsString);
		}
		
		Map<String,MessagePool> messagePools = new HashMap<>();
		for(String l : listeners) {
			MessagePool m = new MessagePool(l);
			List<MessagePoolEntry> e = messages.get("all");
			if(e != null) {
				m.messagePool.addAll(e);
			}
			e = messages.get(l);
			if(e != null) {
				m.messagePool.addAll(e);
			}
			if(m.messagePool.size() > 0) {
				logger.info("Loaded " + m.messagePool.size() + " messages for " + l);
				messagePools.put(l, m);
			}
		}

		Map<String, QueryType> queryTypes = new HashMap<>();
		msgs = new File(pluginDir, "queries.json");
		if(msgs.exists()) {
			JsonObject queriesObject = GsonUtil.loadJSONFile(msgs);
			JsonObject queriesQueriesObject = queriesObject.get("queries").getAsJsonObject();
			for(Entry<String, JsonElement> etr : GsonUtil.asMap(queriesQueriesObject).entrySet()) {
				queryTypes.put(etr.getKey().toLowerCase(),
						new QueryType(etr.getKey(), etr.getValue().getAsJsonObject()));
			}
			if(queryTypes.size() > 0) {
				logger.info("Loaded " + queryTypes.size() + " query types");
			}
		}
		
		return new EaglerMOTDConfiguration(messages, messagePools, queryTypes, close_socket_after, max_sockets_per_ip, max_total_sockets);
	}

	private EaglerMOTDConfiguration(Map<String, List<MessagePoolEntry>> messages, Map<String, MessagePool> messagePools,
			Map<String, QueryType> queryTypes, int close_socket_after, int max_sockets_per_ip, int max_total_sockets) {
		this.messages = messages;
		this.messagePools = messagePools;
		this.queryTypes = queryTypes;
		this.close_socket_after = close_socket_after;
		this.max_sockets_per_ip = max_sockets_per_ip;
		this.max_total_sockets = max_total_sockets;
	}

	private static JsonObject loadFrameCache(Map<String, JsonObject> framesCache, IEaglerMOTDLogger logger, File pluginDir, String name) throws IOException {
		int i = name.indexOf('.');
		if(i == -1) {
			throw new FileNotFoundException(name);
		}
		String f = name.substring(0, i);
		JsonObject fc = framesCache.get(f);
		if(fc == null) {
			File ff = new File(pluginDir, f + ".json");
			if(!ff.isFile()) {
				throw new IOException("File '" + f + ".json' cannot be found!");
			}
			framesCache.put(f, fc = GsonUtil.loadJSONFile(ff));
		}
		f = name.substring(i + 1).trim();
		if(fc.has(f)) {
			return fc.get(f).getAsJsonObject();
		}else {
			throw new IOException("Frame '" + name + "' cannot be found!");
		}
	}

}
