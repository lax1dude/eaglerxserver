package net.lax1dude.eaglercraft.backend.server.api.query;

public interface IDuplexBinaryHandler extends IDuplexBaseHandler {

	void handleBinary(IQueryConnection connection, byte[] binary);

}
