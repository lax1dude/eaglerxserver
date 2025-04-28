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

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreparedResponse;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

abstract class DefaultHandler implements IRequestHandler {

	private final EaglerXServer<?> server;
	private IPreparedResponse response;

	protected DefaultHandler(EaglerXServer<?> server) {
		this.server = server;
	}

	@Override
	public void handleRequest(IRequestContext requestContext) {
		requestContext.setResponseCode(getCode());
		if (response != null) {
			requestContext.setResponseBody(response.retain());
		} else {
			requestContext.setResponseBody(getContents(server), StandardCharsets.UTF_8);
		}
		requestContext.addResponseHeader("content-type", "text/html; charset=utf-8");
	}

	void allocate(WebServer webServer) {
		release();
		response = webServer.prepareResponse(getContents(server), StandardCharsets.UTF_8);
	}

	void release() {
		if (response != null) {
			response.release();
			response = null;
		}
	}

	protected static String htmlEntities(String input) {
		return input.replace("<", "&lt;").replace(">", "&gt;");
	}

	protected abstract int getCode();

	protected abstract String getContents(EaglerXServer<?> server);

}
