package net.lax1dude.eaglercraft.backend.server.base;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRevokeSessionQueryEvent.EnumSessionRevokeStatus;
import net.lax1dude.eaglercraft.backend.server.api.query.IDuplexBinaryHandler;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryHandler;

public class RevokeSessionQueryHandler implements IQueryHandler {

	private final IDuplexBinaryHandler packetHandler;

	public RevokeSessionQueryHandler(EaglerXServer<?> server) {
		this.packetHandler = (conn, bytes) -> {
			if(bytes.length > 255) {
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
				if(stat.code != -1) {
					response.addProperty("code", stat.code);
				}
				if(stat != EnumSessionRevokeStatus.SUCCESS) {
					response.addProperty("delete", evt.getShouldDeleteCookie());
				}
				conn.sendResponse("revoke_session_token", response);
				conn.disconnect();
			});
		};
	}

	@Override
	public void handleQuery(IQueryConnection connection) {
		if(((EaglerListener)connection.getListenerInfo()).getConfigData().isAllowCookieRevokeQuery()) {
			connection.setMaxAge(5000l);
			connection.setBinaryHandler(packetHandler);
			connection.sendResponse("revoke_session_token", "ready");
		}else {
			JsonObject response = new JsonObject();
			response.addProperty("status", "error");
			response.addProperty("code", EnumSessionRevokeStatus.FAILED_NOT_ALLOWED.code);
			response.addProperty("delete", false);
			connection.sendResponse("revoke_session_token", response);
		}
	}

}
