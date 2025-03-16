package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;
import net.lax1dude.eaglercraft.backend.server.api.webserver.RouteDesc;

public class EaglerWeb<PlayerObject> implements IRequestHandler {

	private final IEaglerWebPlatform<PlayerObject> platform;
	private IEaglerXServerAPI<PlayerObject> server;
	private EaglerWebIndex handler;
	private boolean corsEnabled;

	public EaglerWeb(IEaglerWebPlatform<PlayerObject> platform) {
		this.platform = platform;
	}

	public void onEnable(IEaglerXServerAPI<PlayerObject> server) {
		this.server = server;
		platform.logger().info("Indexing pages, please wait...");
		int cnt = handleRefreshIndex();
		platform.logger().info("Indexed " + cnt + " pages total!");
		platform.setHandleRefresh(this::handleRefreshIndex);
		server.getWebServer().registerRoute(this, RouteDesc.DEFAULT_404, this);
	}

	public void onDisable(IEaglerXServerAPI<PlayerObject> server) {
		platform.setHandleRefresh(null);
		server.getWebServer().unregisterRoute(this, RouteDesc.DEFAULT_404);
		setIndex(null);
	}

	public IEaglerWebPlatform<PlayerObject> getPlatform() {
		return platform;
	}

	public IEaglerXServerAPI<PlayerObject> getServer() {
		return server;
	}

	public int handleRefreshIndex() {
		EaglerWebIndex newHandler = EaglerWebIndex.build(this);
		setIndex(newHandler);
		return newHandler.size();
	}

	private void setIndex(EaglerWebIndex handler) {
		EaglerWebIndex oldHandler;
		synchronized(this) {
			oldHandler = this.handler;
			this.handler = handler;
		}
		if(oldHandler != null) {
			oldHandler.release();
		}
	}

	@Override
	public void handleRequest(IRequestContext requestContext) {
		EaglerWebIndex handler = this.handler;
		if(handler != null) {
			handler.handleRequest(requestContext);
		}else {
			requestContext.getServer().getDefault404Handler().handleRequest(requestContext);
		}
	}

	@Override
	public boolean isEnableCORS() {
		return corsEnabled;
	}

	@Override
	public boolean handleCORSAllowOrigin(String origin, EnumRequestMethod method, String path, String query) {
		return corsEnabled;
	}

}
