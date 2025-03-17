package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;

class DefaultHandlers {

	private final String brandString;
	private final String serverName;
	private final boolean enableCORS;
	private final byte[] default429;
	private final byte[] default500;

	DefaultHandlers(EaglerWeb<?> eaglerWeb) {
		this.brandString = eaglerWeb.getPlatform().getVersionString()
				.replace("<", "&lt;").replace(">", "&gt;");
		this.serverName = eaglerWeb.getServer().getServerName()
				.replace("<", "&lt;").replace(">", "&gt;");
		this.enableCORS = eaglerWeb.getConfig().getEnableCORS();
		StringBuilder pageBuilder = new StringBuilder(512);
		pageBuilder.append("<!DOCTYPE html><html><head><title>");
		pageBuilder.append(serverName);
		pageBuilder.append("</title></head><body style=\"font-family:sans-serif;text-align:center;\"><h1>429 Too Many Requests</h1><hr>");
		pageBuilder.append("<p style=\"font-size:1.2em;\">Try again later!</p><p>");
		pageBuilder.append(brandString);
		pageBuilder.append("</p></body></html>");
		this.default429 = pageBuilder.toString().getBytes(StandardCharsets.UTF_8);
		pageBuilder = new StringBuilder(512);
		pageBuilder.append("<!DOCTYPE html><html><head><title>");
		pageBuilder.append(serverName);
		pageBuilder.append("</title></head><body style=\"font-family:sans-serif;text-align:center;\"><h1>500 Internal Error</h1><hr>");
		pageBuilder.append("<p style=\"font-size:1.2em;\">Contact the server owner!</p><p>");
		pageBuilder.append(brandString);
		pageBuilder.append("</p></body></html>");
		this.default500 = pageBuilder.toString().getBytes(StandardCharsets.UTF_8);
	}

	protected static void htmlEntities(CharSequence input, StringBuilder result) {
		for(int i = 0, l = input.length(); i < l; ++i) {
			char c = input.charAt(i);
			if(c == '<') {
				result.append("&lt;");
			}else if(c == '>') {
				result.append("&gt;");
			}else {
				result.append(c);
			}
		}
	}

	void handleAutoIndex(IRequestContext ctx, IndexNodeFolder dir) {
		
	}

	void handle404(IRequestContext ctx) {
		StringBuilder page404Builder = new StringBuilder(512);
		page404Builder.append("<!DOCTYPE html><html><head><title>");
		page404Builder.append(serverName);
		page404Builder.append("</title></head><body style=\"font-family:sans-serif;text-align:center;\"><h1>404 Not Found</h1><hr>");
		page404Builder.append("<p style=\"font-size:1.2em;\">The requested resource <span id=\"addr\" style=\"font-family:monospace;font-weight:bold;background-color:#EEEEEE;padding:3px 4px;\">");
		htmlEntities(ctx.getPath(), page404Builder);
		page404Builder.append("</span> could not be found on this server!</p><p>");
		page404Builder.append(brandString);
		page404Builder.append("</p></body></html>");
		ctx.setResponseCode(404);
		ctx.setResponseBody(page404Builder, StandardCharsets.UTF_8);
		ctx.addResponseHeader("content-type", "text/html; charset=utf-8");
		if(enableCORS) {
			ctx.addResponseHeader("access-control-allow-origin", "*");
		}
	}

	void handle429(IRequestContext ctx) {
		ctx.setResponseCode(429);
		ctx.setResponseBody(default429);
		ctx.addResponseHeader("content-type", "text/html; charset=utf-8");
		if(enableCORS) {
			ctx.addResponseHeader("access-control-allow-origin", "*");
		}
	}

	void handle500(IRequestContext ctx) {
		ctx.setResponseCode(500);
		ctx.setResponseBody(default500);
		ctx.addResponseHeader("content-type", "text/html; charset=utf-8");
		if(enableCORS) {
			ctx.addResponseHeader("access-control-allow-origin", "*");
		}
	}

}
