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
		if (protocols.isProtocolLegacyAllowed()) {
			handshakeVersions.add(1);
			handshakeVersions.add(2);
		}
		if (protocols.isProtocolV3Allowed()) {
			handshakeVersions.add(3);
		}
		if (protocols.isProtocolV4Allowed()) {
			handshakeVersions.add(4);
		}
		if (protocols.isProtocolV5Allowed()) {
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
