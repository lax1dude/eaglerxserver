package net.lax1dude.eaglercraft.backend.server.base.webserver;

import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

class Default404 extends DefaultHandler {

	protected Default404(EaglerXServer<?> server) {
		super(server);
	}

	@Override
	protected int getCode() {
		return 404;
	}

	@Override
	protected String getContents(EaglerXServer<?> server) {
		return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\" /><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" /><title>" + server.getServerName() +
				"</title><script type=\"text/javascript\">window.addEventListener(\"load\",()=>{var src=window.location.href;const gEI=(i)=>document.getElementById(i);"
				+ "if(src.startsWith(\"http:\")){src=\"ws:\"+src.substring(5);}else if(src.startsWith(\"https:\")){src=\"wss:\"+src.substring(6);}else{return;}"
				+ "gEI(\"wsUri\").innerHTML=\"<span id=\\\"wsField\\\" style=\\\"font-family:monospace;font-weight:bold;background-color:#EEEEEE;padding:3px 4px;\\\">"
				+ "</span>\";gEI(\"wsField\").innerText=src;});</script></head><body style=\"font-family:sans-serif;margin:0px;padding:12px;\"><h1 style=\"margin-block-start:0px;\">"
				+ "404 'Websocket Upgrade Failure' (rip)</h1><h3>The URL you have requested is the physical WebSocket address of '" + server.getServerName() + "'</h3><p style=\"font-size:1.2em;"
				+ "line-height:1.3em;\">To correctly join this server, load the latest EaglercraftX 1.8 client, click the 'Direct Connect' button<br />on the 'Multiplayer' screen, "
				+ "and enter <span id=\"wsUri\">this URL</span> as the server address</p></body></html>";
	}

}
