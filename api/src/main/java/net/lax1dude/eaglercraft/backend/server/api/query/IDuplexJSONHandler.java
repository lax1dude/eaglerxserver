package net.lax1dude.eaglercraft.backend.server.api.query;

import com.google.gson.JsonObject;

public interface IDuplexJSONHandler extends IDuplexBaseHandler {

	void handleJSONObject(IQueryConnection connection, JsonObject json);

}
