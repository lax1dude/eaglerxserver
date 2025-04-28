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

package net.lax1dude.eaglercraft.backend.supervisor.status;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.supervisor.EaglerXSupervisorServer;
import net.lax1dude.eaglercraft.backend.supervisor.config.EaglerXSupervisorConfig;

public class HttpStatusRequestHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger("HttpStatusRequestHandler");

	private final EaglerXSupervisorServer server;

	public HttpStatusRequestHandler(EaglerXSupervisorServer server) {
		this.server = server;
	}

	public void channelRead(ChannelHandlerContext ctx, Object msgRaw) throws Exception {
		try {
			if (msgRaw instanceof HttpRequest) {
				handleRequest(ctx, (HttpRequest) msgRaw);
			} else {
				ctx.close();
			}
		} finally {
			ReferenceCountUtil.release(msgRaw);
		}
	}

	private void handleRequest(ChannelHandlerContext ctx, HttpRequest msg) {
		if (!checkAuthorization(ctx, msg)) {
			return;
		}
		String uri = msg.uri();
		if (uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		int j = uri.indexOf('?');
		if (j != -1) {
			uri = uri.substring(0, j);
		}
		switch (uri) {
		case "":
		case "overview":
			sendResponse(ctx, HttpResponseStatus.OK, "text/html", server.getStatusRendererHTML().renderIndex());
			break;
		case "proxies":
			sendResponse(ctx, HttpResponseStatus.OK, "text/html", server.getStatusRendererHTML().renderProxies());
			break;
		case "players":
			sendResponse(ctx, HttpResponseStatus.OK, "text/html", server.getStatusRendererHTML().renderPlayers());
			break;
		default:
			sendResponse(ctx, HttpResponseStatus.NOT_FOUND, "text/html", server.getStatusRendererHTML().render404());
			break;
		}
	}

	private boolean checkAuthorization(ChannelHandlerContext ctx, HttpRequest msg) {
		EaglerXSupervisorConfig conf = server.getConfig();
		String str = conf.getAuthString();
		if (str != null) {
			String authHeader = msg.headers().get(HttpHeaderNames.AUTHORIZATION);
			if (authHeader != null && str.equals(authHeader)) {
				return true;
			}
			server.getLogger().warn("Invalid password attempt on HTTP status page from {}",
					ctx.channel().remoteAddress());
			DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
					HttpResponseStatus.UNAUTHORIZED);
			HttpHeaders responseHeaders = response.headers();
			responseHeaders.add(HttpHeaderNames.WWW_AUTHENTICATE, "Basic realm=\"you eagler\" charset=\"utf-8\"");
			responseHeaders.add(HttpHeaderNames.DATE, gmt.format(new Date()));
			responseHeaders.add(HttpHeaderNames.SERVER, server.getServerString());
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
			return false;
		}
		return true;
	}

	public static final SimpleDateFormat gmt;

	static {
		gmt = new SimpleDateFormat();
		gmt.setTimeZone(new SimpleTimeZone(0, "GMT"));
		gmt.applyPattern("dd MMM yyyy HH:mm:ss z");
	}

	private void sendResponse(ChannelHandlerContext ctx, HttpResponseStatus code, String contentType, String markup) {
		ByteBuf buffer = ctx.alloc().buffer(markup.length());
		buffer.writeCharSequence(markup, StandardCharsets.UTF_8);
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code, buffer);
		HttpHeaders responseHeaders = response.headers();
		responseHeaders.add(HttpHeaderNames.CONTENT_TYPE, contentType);
		responseHeaders.add(HttpHeaderNames.CONTENT_LENGTH, buffer.readableBytes());
		responseHeaders.add(HttpHeaderNames.DATE, gmt.format(new Date()));
		responseHeaders.add(HttpHeaderNames.SERVER, server.getServerString());
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (ctx.channel().isActive()) {
			logger.error("[" + ctx.channel().remoteAddress() + "] Encountered an exception: ", cause);
			ctx.close();
		}
	}

}