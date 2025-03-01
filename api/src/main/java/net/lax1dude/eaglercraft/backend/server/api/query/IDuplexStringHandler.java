package net.lax1dude.eaglercraft.backend.server.api.query;

public interface IDuplexStringHandler extends IDuplexBaseHandler {

	void handleString(IQueryConnection connection, String string);

}
