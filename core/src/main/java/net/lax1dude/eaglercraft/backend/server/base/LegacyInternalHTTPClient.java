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

package net.lax1dude.eaglercraft.backend.server.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.skin_cache.IHTTPClient;

public class LegacyInternalHTTPClient implements IHTTPClient {

	private final IPlatformScheduler scheduler;
	private final String userAgent;

	public LegacyInternalHTTPClient(IPlatformScheduler scheduler, String userAgent) {
		this.scheduler = scheduler;
		this.userAgent = userAgent;
	}

	@Override
	public void asyncRequest(String method, URI uri, Consumer<Response> responseCallback) {
		String scheme = uri.getScheme();
		if(!scheme.equals("http") && !scheme.equals("https")) {
			responseCallback.accept(new Response(new UnsupportedOperationException("Unsupported scheme: " + scheme)));
			return;
		}
		URL url;
		try {
			url = uri.toURL();
		}catch(MalformedURLException ex) {
			responseCallback.accept(new Response(ex));
			return;
		}
		scheduler.executeAsync(() -> {
			Response res;
			try {
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.addRequestProperty("user-agent", userAgent);
				conn.connect();
				ByteBuf buf = Unpooled.buffer(1024);
				try {
					try(InputStream is = conn.getInputStream()) {
						is.transferTo(new ByteBufOutputStream(buf));
					}
					int responseCode = conn.getResponseCode();
					res = new Response(responseCode, false, buf.retain());
				}finally {
					buf.release();
					conn.disconnect();
				}
			} catch (IOException ex) {
				responseCallback.accept(new Response(ex));
				return;
			}
			responseCallback.accept(res);
		});
	}

}
