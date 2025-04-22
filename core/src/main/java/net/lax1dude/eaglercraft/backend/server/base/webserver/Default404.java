/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

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
		String name = htmlEntities(server.getServerName());
		return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\" /><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" /><title>" + name +
				"</title><script type=\"text/javascript\">window.addEventListener(\"load\",()=>{var src=window.location.href;const gEI=(i)=>document.getElementById(i);"
				+ "if(src.startsWith(\"http:\")){src=\"ws:\"+src.substring(5);}else if(src.startsWith(\"https:\")){src=\"wss:\"+src.substring(6);}else{return;}"
				+ "gEI(\"wsUri\").innerHTML=\"<span id=\\\"wsField\\\" style=\\\"font-family:monospace;font-weight:bold;background-color:#EEEEEE;padding:3px 4px;\\\">"
				+ "</span>\";gEI(\"wsField\").innerText=src;});</script></head><body style=\"font-family:sans-serif;margin:0px;padding:12px;\"><h1 style=\"margin-block-start:0px;\">"
				+ "404 'Websocket Upgrade Failure' (rip)</h1><h3>The URL you have requested is the physical WebSocket address of '" + name + "'</h3><p style=\"font-size:1.2em;"
				+ "line-height:1.3em;\">To correctly join this server, load the latest EaglercraftX 1.8 client, click the 'Direct Connect' button<br />on the 'Multiplayer' screen, "
				+ "and enter <span id=\"wsUri\">this URL</span> as the server address</p></body></html>";
	}

}
