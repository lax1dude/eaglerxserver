package net.lax1dude.eaglercraft.backend.server.api.query;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

public interface IDuplexJSONHandler extends IDuplexBaseHandler {

	void handleJSONObject(@Nonnull IQueryConnection connection, @Nonnull JsonObject json);

}
