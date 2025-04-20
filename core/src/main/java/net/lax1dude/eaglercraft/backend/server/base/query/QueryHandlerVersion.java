package net.lax1dude.eaglercraft.backend.server.base.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryHandler;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings.ConfigDataProtocols;

public class QueryHandlerVersion implements IQueryHandler {

	private final EaglerXServer<?> server;

	public QueryHandlerVersion(EaglerXServer<?> server) {
		this.server = server;
	}

	@Override
	public void handleQuery(IQueryConnection connection) {
		JsonObject responseObj = new JsonObject();
		JsonArray handshakeVersions = new JsonArray();
		ConfigDataProtocols protocols = server.getConfig().getSettings().getProtocols();
		if(protocols.isProtocolLegacyAllowed()) {
			handshakeVersions.add(1);
			handshakeVersions.add(2);
		}
		if(protocols.isProtocolV3Allowed()) {
			handshakeVersions.add(3);
		}
		if(protocols.isProtocolV4Allowed()) {
			handshakeVersions.add(4);
		}
		if(protocols.isProtocolV5Allowed()) {
			handshakeVersions.add(5);
		}
		responseObj.add("handshakeVersions", handshakeVersions);
		JsonObject protocolVersions = new JsonObject();
		protocolVersions.addProperty("min", protocols.getMinMinecraftProtocol());
		protocolVersions.addProperty("max", protocols.getMaxMinecraftProtocol());
		responseObj.add("protocolVersions", protocolVersions);
		JsonObject proxyInfo = new JsonObject();
		proxyInfo.addProperty("brand", server.getServerBrand());
		proxyInfo.addProperty("vers", server.getServerVersion());
		responseObj.add("proxyVersions", proxyInfo);
		connection.sendResponse("version", responseObj);
	}

}
