/*
 * Copyright (c) 2022-2024 lax1dude, ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.skin_cache;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.net.ssl.SSLEngine;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class HTTPClient implements IHTTPClient {

	public static final int MAX_REDIRECTS = 8;

	private static class RedirectTracker {
		private int redirects = 0;
		private String method;
		private RedirectTracker(String method) {
			this.method = method;
		}
	}

	private class NettyHttpChannelFutureListener implements ChannelFutureListener {

		protected final String method;
		protected final URI requestURI;
		protected final Consumer<Response> responseCallback;

		protected NettyHttpChannelFutureListener(String method, URI requestURI, Consumer<Response> responseCallback) {
			this.method = method;
			this.requestURI = requestURI;
			this.responseCallback = responseCallback;
		}

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				String path = requestURI.getRawPath()
						+ ((requestURI.getRawQuery() == null) ? "" : ("?" + requestURI.getRawQuery()));
				HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
						HttpMethod.valueOf(method), path);
				request.headers().set(HttpHeaderNames.HOST, requestURI.getHost());
				request.headers().set(HttpHeaderNames.USER_AGENT, userAgent);
				future.channel().writeAndFlush(request);
			} else {
				addressCache.invalidate(requestURI.getHost());
				responseCallback.accept(new Response(new IOException("Connection failed")));
			}
		}

	}

	private class NettyHttpChannelInitializer extends ChannelInitializer<Channel> {

		protected final Consumer<Response> responseCallback;
		protected final RedirectTracker redirectTracker;
		protected final boolean ssl;
		protected final String host;
		protected final int port;

		protected NettyHttpChannelInitializer(Consumer<Response> responseCallback, RedirectTracker redirectTracker,
				boolean ssl, String host, int port) {
			this.responseCallback = responseCallback;
			this.redirectTracker = redirectTracker;
			this.ssl = ssl;
			this.host = host;
			this.port = port;
		}

		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addLast("timeout", new ReadTimeoutHandler(5L, TimeUnit.SECONDS));
			if (this.ssl) {
				SSLEngine engine = SslContextBuilder.forClient().build().newEngine(ch.alloc(), host, port);
				ch.pipeline().addLast("ssl", new SslHandler(engine));
			}

			ch.pipeline().addLast("http", new HttpClientCodec());
			ch.pipeline().addLast("handler", new NettyHttpResponseHandler(responseCallback, redirectTracker));
		}

	}
	
	private class NettyHttpResponseHandler extends SimpleChannelInboundHandler<HttpObject> {

		protected final Consumer<Response> responseCallback;
		protected final RedirectTracker redirectTracker;
		protected int responseCode = -1;
		protected ByteBuf buffer = null;

		protected NettyHttpResponseHandler(Consumer<Response> responseCallback, RedirectTracker redirectTracker) {
			this.responseCallback = responseCallback;
			this.redirectTracker = redirectTracker;
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
			if (msg instanceof HttpResponse response) {
				responseCode = response.status().code();
				if (responseCode == 301 || responseCode == 302 || responseCode == 303 || responseCode == 307
						|| responseCode == 308) {
					ctx.channel().pipeline().remove(this);
					ctx.channel().close();
					if(responseCode == 303) {
						redirectTracker.method = "GET";
					}
					redirect(response);
					return;
				}else if (responseCode == 204) {
					this.done(ctx);
					return;
				}
			}
			if (msg instanceof HttpContent content) {
				if(buffer == null) {
					buffer = ctx.alloc().buffer();
				}
				this.buffer.writeBytes(content.content());
				if (msg instanceof LastHttpContent) {
					this.done(ctx);
				}
			}
		}

		private void redirect(HttpResponse response) {
			if(++redirectTracker.redirects >= MAX_REDIRECTS) {
				responseCallback.accept(new Response(new IllegalStateException("Too many redirects!")));
			}else {
				CharSequence target = response.headers().get(HttpHeaderNames.LOCATION);
				if(target != null) {
					URI uri;
					try {
						uri = new URI(target.toString());
					}catch(URISyntaxException ex) {
						responseCallback.accept(new Response(new IllegalStateException("Invalid redirect address in 3xx response!", ex)));
						return;
					}
					asyncRequest(uri, responseCallback, redirectTracker);
				}else {
					responseCallback.accept(new Response(new IllegalStateException("Missing redirect address in 3xx response!")));
				}
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			responseCallback.accept(new Response(cause));
		}

		private void done(ChannelHandlerContext ctx) {
			try {
				responseCallback.accept(new Response(responseCode, redirectTracker.redirects > 0, buffer));
			}finally {
				ctx.channel().pipeline().remove(this);
				ctx.channel().close();
			}
		}

	}

	private final Cache<String, InetAddress> addressCache = CacheBuilder.newBuilder()
			.expireAfterWrite(15L, TimeUnit.MINUTES).build();
	private final Supplier<Bootstrap> bootstrapper;
	private final String userAgent;

	public HTTPClient(Supplier<Bootstrap> bootstrapper, String userAgent) {
		this.bootstrapper = bootstrapper;
		this.userAgent = userAgent;
	}

	public void asyncRequest(String method, URI uri, Consumer<Response> responseCallback) {
		asyncRequest(uri, responseCallback, new RedirectTracker(method));
	}

	private void asyncRequest(URI uri, Consumer<Response> responseCallback, RedirectTracker redirectTracker) {
		int port = uri.getPort();
		boolean ssl = false;
		String scheme = uri.getScheme();
		switch(scheme) {
		case "http":
			if(port == -1) {
				port = 80;
			}
			break;
		case "https":
			if(port == -1) {
				port = 443;
			}
			ssl = true;
			break;
		default:
			responseCallback.accept(new Response(new UnsupportedOperationException("Unsupported scheme: " + scheme)));
			return;
		}
		
		String host = uri.getHost();
		InetAddress inetHost = addressCache.getIfPresent(host);
		if (inetHost == null) {
			try {
				inetHost = InetAddress.getByName(host);
			} catch (UnknownHostException ex) {
				responseCallback.accept(new Response(ex));
				return;
			}
			addressCache.put(host, inetHost);
		}
		InetSocketAddress addr = new InetSocketAddress(inetHost, port);
		bootstrapper.get().handler(new NettyHttpChannelInitializer(responseCallback, redirectTracker, ssl, host, port))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000).option(ChannelOption.TCP_NODELAY, true)
				.remoteAddress(addr).connect()
				.addListener(new NettyHttpChannelFutureListener(redirectTracker.method, uri, responseCallback));
	}

}