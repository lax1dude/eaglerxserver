package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.IOException;

import com.google.gson.JsonParseException;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreflightContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;
import net.lax1dude.eaglercraft.backend.server.api.webserver.RouteDesc;

public class EaglerWeb<PlayerObject> {

	private final IEaglerWebPlatform<PlayerObject> platform;
	private IEaglerXServerAPI<PlayerObject> server;
	private EaglerWebConfig config;
	private EaglerWebHandler handler;
	private boolean registered;

	public EaglerWeb(IEaglerWebPlatform<PlayerObject> platform) {
		this.platform = platform;
	}

	public void onEnable(IEaglerXServerAPI<PlayerObject> server) {
		this.server = server;
		platform.logger().info("Loading config files...");
		try {
			config = EaglerWebConfig.loadConfig(platform.logger(), platform.getDataFolder());
		} catch (JsonParseException | IOException e) {
			platform.logger().info("Loading config files...");
			return;
		}
		platform.logger().info("Indexing pages, please wait...");
		int cnt;
		try {
			cnt = handleRefreshIndex();
		}catch(IOException ex) {
			platform.logger().error("Failed to index pages!", ex);
			return;
		}
		platform.logger().info("Indexed " + cnt + " pages total!");
		platform.setHandleRefresh(this::handleRefreshIndex);
		server.getWebServer().registerRoute(this, RouteDesc.DEFAULT_404, new HandlerBase() {
			@Override
			public void handleRequest(IRequestContext requestContext) {
				EaglerWebHandler handler = EaglerWeb.this.handler;
				if(handler != null) {
					handler.handleRequest(requestContext);
				}else {
					requestContext.getServer().getDefault404Handler().handleRequest(requestContext);
				}
			}
		});
		server.getWebServer().registerRoute(this, RouteDesc.DEFAULT_429, new HandlerBase() {
			@Override
			public void handleRequest(IRequestContext requestContext) {
				EaglerWebHandler handler = EaglerWeb.this.handler;
				if(handler != null) {
					handler.handle429(requestContext);
				}else {
					requestContext.getServer().getDefault429Handler().handleRequest(requestContext);
				}
			}
		});
		server.getWebServer().registerRoute(this, RouteDesc.DEFAULT_500, new HandlerBase() {
			@Override
			public void handleRequest(IRequestContext requestContext) {
				EaglerWebHandler handler = EaglerWeb.this.handler;
				if(handler != null) {
					handler.handle500(requestContext);
				}else {
					requestContext.getServer().getDefault500Handler().handleRequest(requestContext);
				}
			}
		});
		registered = true;
	}

	public void onDisable(IEaglerXServerAPI<PlayerObject> server) {
		platform.logger().info("Shutting down, please wait...");
		platform.setHandleRefresh(null);
		if(registered) {
			registered = false;
			server.getWebServer().unregisterRoute(this, RouteDesc.DEFAULT_404);
			server.getWebServer().unregisterRoute(this, RouteDesc.DEFAULT_429);
			server.getWebServer().unregisterRoute(this, RouteDesc.DEFAULT_500);
		}
		setIndex(null);
	}

	public IEaglerWebPlatform<PlayerObject> getPlatform() {
		return platform;
	}

	public IEaglerXServerAPI<PlayerObject> getServer() {
		return server;
	}

	public EaglerWebConfig getConfig() {
		return config;
	}

	public int handleRefreshIndex() throws IOException {
		EaglerWebHandler newHandler = EaglerWebHandler.build(this);
		setIndex(newHandler);
		return newHandler.size();
	}

	private void setIndex(EaglerWebHandler handler) {
		EaglerWebHandler oldHandler;
		synchronized(this) {
			oldHandler = this.handler;
			this.handler = handler;
		}
		if(oldHandler != null) {
			oldHandler.release();
		}
	}

	private abstract class HandlerBase implements IRequestHandler {

		@Override
		public boolean enablePreflight() {
			EaglerWebHandler handler = EaglerWeb.this.handler;
			return handler != null && handler.enablePreflight();
		}

		@Override
		public void handlePreflight(IPreflightContext context) {
			EaglerWebHandler handler = EaglerWeb.this.handler;
			if(handler != null && handler.enablePreflight()) {
				handler.handlePreflight(context);
			}else {
				context.setResponseCode(403);
				context.setResponseBodyEmpty();
			}
		}

	}

	public IEaglerWebLogger logger() {
		return platform.logger();
	}

}
