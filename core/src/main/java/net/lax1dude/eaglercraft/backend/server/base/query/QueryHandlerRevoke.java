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

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRevokeSessionQueryEvent.EnumSessionRevokeStatus;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexBinaryHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryHandler;
import net.lax1dude.eaglercraft.backend.server.base.EaglerListener;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class QueryHandlerRevoke implements IQueryHandler {

	private final IDuplexBinaryHandler packetHandler;

	public QueryHandlerRevoke(EaglerXServer<?> server) {
		this.packetHandler = (conn, bytes) -> {
			if (bytes.length > 255) {
				JsonObject response = new JsonObject();
				response.addProperty("status", "error");
				response.addProperty("code", EnumSessionRevokeStatus.FAILED_NOT_FOUND.code);
				response.addProperty("delete", false);
				conn.sendResponse("revoke_session_token", response);
				conn.disconnect();
				return;
			}
			conn.setMaxAge(30000l);
			conn.setBinaryHandler(null);
			server.eventDispatcher().dispatchRevokeSessionQueryEvent(conn, bytes, (evt, err) -> {
				JsonObject response = new JsonObject();
				EnumSessionRevokeStatus stat = evt.getResultStatus();
				response.addProperty("status", stat.status);
				if (stat.code != -1) {
					response.addProperty("code", stat.code);
				}
				if (stat != EnumSessionRevokeStatus.SUCCESS) {
					response.addProperty("delete", evt.getShouldDeleteCookie());
				}
				conn.sendResponse("revoke_session_token", response);
				conn.disconnect();
			});
		};
	}

	@Override
	public void handleQuery(IQueryConnection connection) {
		if (((EaglerListener) connection.getListenerInfo()).getConfigData().isAllowCookieRevokeQuery()) {
			connection.setMaxAge(5000l);
			connection.setBinaryHandler(packetHandler);
			connection.sendResponse("revoke_session_token", "ready");
		} else {
			JsonObject response = new JsonObject();
			response.addProperty("status", "error");
			response.addProperty("code", EnumSessionRevokeStatus.FAILED_NOT_ALLOWED.code);
			response.addProperty("delete", false);
			connection.sendResponse("revoke_session_token", response);
		}
	}

}
