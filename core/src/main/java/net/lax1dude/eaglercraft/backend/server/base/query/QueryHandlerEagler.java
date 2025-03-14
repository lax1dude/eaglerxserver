package net.lax1dude.eaglercraft.backend.server.base.query;

import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryHandler;

public class QueryHandlerEagler implements IQueryHandler {

	@Override
	public void handleQuery(IQueryConnection connection) {
		connection.sendResponse("eagler", "eagler");
	}

}
