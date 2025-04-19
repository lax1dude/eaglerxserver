package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;
import net.lax1dude.eaglercraft.backend.server.base.CompoundRateLimiterMap;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.webserver.RequestContext;
import net.lax1dude.eaglercraft.backend.server.base.webserver.RequestContext.ContextPromise;
import net.lax1dude.eaglercraft.backend.server.base.webserver.ResponseOrdering;
import net.lax1dude.eaglercraft.backend.server.base.webserver.RouteMap;
import net.lax1dude.eaglercraft.backend.server.base.webserver.RouteProcessor;
import net.lax1dude.eaglercraft.backend.server.util.EnumRateLimitState;

public class HTTPRequestInboundHandler extends ChannelInboundHandlerAdapter {

	private static final ImmutableMap<HttpMethod, EnumRequestMethod> methodLookup = 
			ImmutableMap.<HttpMethod, EnumRequestMethod>builder()
			.put(HttpMethod.GET, EnumRequestMethod.GET)
			.put(HttpMethod.HEAD, EnumRequestMethod.HEAD)
			.put(HttpMethod.PUT, EnumRequestMethod.PUT)
			.put(HttpMethod.DELETE, EnumRequestMethod.DELETE)
			.put(HttpMethod.POST, EnumRequestMethod.POST)
			.put(HttpMethod.PATCH, EnumRequestMethod.PATCH)
			.build();

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;
	private ResponseOrdering ordering;
	private RouteProcessor processor;
	private RequestContext context;
	private boolean isFirst;

	public HTTPRequestInboundHandler(EaglerXServer<?> server, NettyPipelineData pipelineData) {
		this.server = server;
		this.pipelineData = pipelineData;
		this.isFirst = true;
	}

	private RouteProcessor processor() {
		if(processor == null) {
			return processor = new RouteProcessor();
		}
		return processor;
	}

	private RequestContext context() {
		if(context == null) {
			return context = new RequestContext(server.getWebServer());
		}
		return context;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if(ordering != null) {
			ordering.release();
		}
		ordering = new ResponseOrdering() {
			@Override
			protected void send(FullHttpResponse data) {
				if(ctx.channel().isActive()) {
					ctx.writeAndFlush(data);
				}else {
					data.release();
				}
			}
		};
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		if(ordering != null) {
			ordering.release();
			ordering = null;
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msgRaw) throws Exception {
		try {
			if(!ctx.channel().isActive()) {
				return;
			}
			if(msgRaw instanceof FullHttpRequest msg) {
				if(msg.protocolVersion() != HttpVersion.HTTP_1_1) {
					ctx.close();
					return;
				}
				
				String uri = msg.uri();
				String path;
				String query;
				int i = uri.indexOf('?');
				if(i != -1) {
					path = uri.substring(0, i);
					query = uri.substring(i);
				}else {
					path = uri;
					query = "";
				}
				
				HttpMethod method = msg.method();
				EnumRequestMethod meth = methodLookup.get(method);
				
				ResponseOrdering.Slot responseSlot = ordering.push();
				
				CompoundRateLimiterMap rateLimiter = pipelineData.listenerInfo.getRateLimiter();
				if(rateLimiter != null) {
					if(!isFirst) {
						if(!HTTPInitialInboundHandler.recheckRatelimitAddress(ctx, pipelineData, msg)) {
							ctx.close();
							return;
						}
					}
					if(pipelineData.rateLimits != null) {
						EnumRateLimitState rateLimit = pipelineData.rateLimits.rateLimitHTTP();
						if(!rateLimit.isOk()) {
							if(rateLimit == EnumRateLimitState.BLOCKED || rateLimit == EnumRateLimitState.BLOCKED_LOCKED) {
								if(meth == null) {
									meth = EnumRequestMethod.GET;
								}
								handleRequest(ctx, msg, meth, null, uri, path, query, server.getWebServer().get429Handler(), responseSlot, false);
							}else {
								ctx.close();
							}
							return;
						}
					}
				}
				
				if(meth == null) {
					if(method == HttpMethod.OPTIONS) {
						handleOptions(ctx, uri, path, query, msg, responseSlot);
					}else {
						handleUnexpectedMeth(ctx, method, responseSlot);
					}
					return;
				}
				
				RouteMap.Result<IRequestHandler> handlerResult = server.getWebServer()
						.resolveInternal(pipelineData.listenerInfo, meth.id(), path, processor());
				
				boolean dir = path.endsWith("/");
				if(dir != handlerResult.directory) {
					FullHttpResponse res = createResponse(HttpResponseStatus.PERMANENT_REDIRECT, null, 0);
					res.headers().add(HttpHeaderNames.LOCATION, redirDir(path, query, dir));
					responseSlot.complete(res);
					return;
				}
				
				handleRequest(ctx, msg, meth, null, uri, path, query, handlerResult.result, responseSlot, false);
			}
		}finally {
			ReferenceCountUtil.release(msgRaw);
			isFirst = false;
		}
	}

	private String redirDir(String path, String query, boolean dir) {
		if(dir) {
			int i = path.length();
			while(i > 0 && path.charAt(i - 1) == '/') {
				--i;
			}
			if(i <= 0) {
				return query;
			}
			StringBuilder stringBuilder = new StringBuilder(i + query.length());
			stringBuilder.append(path, 0, i);
			stringBuilder.append(query);
			return stringBuilder.toString();
		}else {
			return path + "/" + query;
		}
	}

	private void handleOptions(ChannelHandlerContext ctx, String uri, String path, String query, FullHttpRequest msg,
			ResponseOrdering.Slot responseSlot) {
		HttpHeaders headers = msg.headers();
		String reqMethod = headers.get(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);

		if(reqMethod != null) {
			EnumRequestMethod corsMethod;
			try {
				corsMethod = EnumRequestMethod.valueOf(reqMethod);
			}catch(IllegalArgumentException ex) {
				corsMethod = null;
			}
			
			if(corsMethod != null && corsMethod != EnumRequestMethod.OPTIONS && headers.contains(HttpHeaderNames.ORIGIN)) {
				RouteMap.Result<IRequestHandler> handlerResult = server.getWebServer()
						.resolveInternal(pipelineData.listenerInfo, corsMethod.id(), path, processor());

				boolean dir = path.endsWith("/");
				if(dir != handlerResult.directory) {
					FullHttpResponse res = createResponse(HttpResponseStatus.PERMANENT_REDIRECT, null, 0);
					res.headers().add(HttpHeaderNames.LOCATION, redirDir(path, query, dir));
					responseSlot.complete(res);
					return;
				}

				handleRequest(ctx, msg, EnumRequestMethod.OPTIONS, corsMethod, uri, path, query, handlerResult.result,
						responseSlot, false);
			}else {
				responseSlot.complete(createResponse(HttpResponseStatus.BAD_REQUEST, null, 0));
			}
			return;
		}
		
		if("*".equals(path) && query.isEmpty()) {
			FullHttpResponse res = createResponse(HttpResponseStatus.OK, null, 0);
			res.headers().add(HttpHeaderNames.ALLOW, RouteMap.allMethods);
			responseSlot.complete(res);
			return;
		}
		
		RouteMap.Result<List<EnumRequestMethod>> optionsResult = server.getWebServer()
				.optionsInternal(pipelineData.listenerInfo, path, processor());
		if(optionsResult.result != null) {
			boolean dir = path.endsWith("/");
			if(dir != optionsResult.directory) {
				FullHttpResponse res = createResponse(HttpResponseStatus.PERMANENT_REDIRECT, null, 0);
				res.headers().add(HttpHeaderNames.LOCATION, redirDir(path, query, dir));
				responseSlot.complete(res);
				return;
			}
			if(!optionsResult.result.isEmpty()) {
				FullHttpResponse res = createResponse(HttpResponseStatus.OK, null, 0);
				res.headers().add(HttpHeaderNames.ALLOW, optionsResult.result);
				responseSlot.complete(res);
				return;
			}
		}
		
		responseSlot.complete(createResponse(HttpResponseStatus.FORBIDDEN, null, 0));
	}

	private void handleRequest(RequestContext oldContext, IRequestHandler requestHandler,
			ResponseOrdering.Slot responseSlot, boolean isFailing) {
		handleRequest(oldContext.ctx, oldContext.request, oldContext.meth, oldContext.pfMeth, oldContext.uri,
				oldContext.path, oldContext.query, requestHandler, responseSlot, isFailing);
	}

	private void handleRequest(ChannelHandlerContext ctx, FullHttpRequest msg, EnumRequestMethod meth,
			EnumRequestMethod pfMeth, String uri, String path, String query, IRequestHandler requestHandler,
			ResponseOrdering.Slot responseSlot, boolean isFailing) {
		if(pfMeth != null && !requestHandler.enablePreflight()) {
			responseSlot.complete(createResponse(HttpResponseStatus.FORBIDDEN, null, 0));
			return;
		}
		RequestContext context = context();
		context.failing = isFailing;
		context.setContext(pipelineData.listenerInfo, meth, pfMeth, uri, path, query, ctx, msg,
				pipelineData.realAddress);
		try {
			context.requestHandlerInternal = requestHandler;
			context.suspendable = true;
			try {
				if(pfMeth != null) {
					requestHandler.handlePreflight(context);
				}else {
					requestHandler.handleRequest(context);
				}
			}finally {
				context.suspendable = false;
			}
		}catch(Throwable ex) {
			if(context.contextPromise != null) {
				this.context = null;
			}
			completeRequest(context, ex, responseSlot);
			return;
		}
		ContextPromise promise = context.contextPromise;
		if(promise != null) {
			this.context = null; // "forks" the context
			context.responseSlotTmp = responseSlot;
			promise.onResumeInternal(this::completeRequest);
		}else {
			completeRequest(context, null, responseSlot);
		}
	}

	private void completeRequest(RequestContext context, Throwable err) {
		completeRequest(context, err, context.responseSlotTmp);
	}

	private void completeRequest(RequestContext context, Throwable err, ResponseOrdering.Slot responseSlot) {
		boolean rc = context.contextPromise != null;
		try {
			if(err != null) {
				pipelineData.connectionLogger.error("Request handler " + context.requestHandlerInternal
						+ " raised an exception while handling " + context.meth.name() + " \"" + context.uri + "\"", err);
				if(!context.failing) {
					handleRequest(context, server.getWebServer().get500Handler(), responseSlot, true);
				}else {
					responseSlot.complete(createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null, 0));
				}
				return;
			}
			if(context.responseCode == -1) {
				pipelineData.connectionLogger.error("Request handler " + context.requestHandlerInternal
						+ " set no response code for " + context.meth.name() + " \"" + context.uri + "\"");
				if(!context.failing) {
					handleRequest(context, server.getWebServer().get500Handler(), responseSlot, true);
				}else {
					responseSlot.complete(createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null, 0));
				}
				return;
			}
			HttpResponseStatus status = HttpResponseStatus.valueOf(context.responseCode);
			switch(context.response) {
			case RequestContext.RESPONSE_NONE:
			default:
				pipelineData.connectionLogger.error("Request handler " + context.requestHandlerInternal
						+ " made no response for " + context.meth.name() + " \"" + context.uri + "\"");
				handleRequest(context, server.getWebServer().get500Handler(), responseSlot, true);
				break;
			case RequestContext.RESPONSE_PREPARED:
				if(context.meth == EnumRequestMethod.HEAD) {
					responseSlot.complete(populateHeadersFrom(
							createResponse(status, null, context.responsePrepared.buffer.readableBytes()), context));
				}else {
					responseSlot.complete(populateHeadersFrom(
							createResponse(status, Unpooled.wrappedBuffer(context.responsePrepared.buffer), 0), context).retain());
				}
				break;
			case RequestContext.RESPONSE_BYTE_ARRAY:
				if(context.meth == EnumRequestMethod.HEAD) {
					responseSlot.complete(populateHeadersFrom(
							createResponse(status, null, context.responseData.length), context));
				}else {
					responseSlot.complete(populateHeadersFrom(
							createResponse(status, Unpooled.wrappedBuffer(context.responseData), 0), context));
				}
				break;
			case RequestContext.RESPONSE_CHARS:
				if(context.meth == EnumRequestMethod.HEAD) {
					responseSlot.complete(populateHeadersFrom(createResponse(status, null,
							stringByteLength(context.responseChars, context.responseCharsCharset)), context));
				}else {
					ByteBuf buf = context.ctx.alloc().buffer();
					try {
						BufferUtils.writeCharSequence(buf, context.responseChars, context.responseCharsCharset);
						responseSlot.complete(populateHeadersFrom(createResponse(status, buf, 0), context).retain());
					}finally {
						buf.release();
					}
				}
				break;
			case RequestContext.RESPONSE_EMPTY:
				responseSlot.complete(populateHeadersFrom(createResponse(status, null, 0), context));
				break;
			case RequestContext.RESPONSE_UNSAFE_BUF:
				responseSlot.complete(populateHeadersFrom(createResponse(status, context.responseUnsafeByteBuf, 0), context).retain());
				break;
			case RequestContext.RESPONSE_UNSAFE_FULL:
				responseSlot.complete(context.responseUnsafeFull.retain());
				break;
			}
		}finally {
			try {
				if(rc) {
					context.request.release();
				}
			}finally {
				context.clearResult();
			}
		}
	}

	private int stringByteLength(CharSequence chars, Charset charset) {
		if(charset == StandardCharsets.UTF_8) {
			return ByteBufUtil.utf8Bytes(chars);
		}else if(charset == StandardCharsets.US_ASCII || charset == StandardCharsets.ISO_8859_1) {
			return chars.length();
		}else if(charset == StandardCharsets.UTF_16 || charset == StandardCharsets.UTF_16LE || charset == StandardCharsets.UTF_16BE) {
			return chars.length() * 2;
		}else {
			return chars.toString().getBytes(charset).length; // RIP
		}
	}

	private FullHttpResponse createResponse(HttpResponseStatus code, ByteBuf body, int len) {
		FullHttpResponse ret;
		if(body != null) {
			ret = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code, body);
		}else {
			ret = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code);
		}
		HttpHeaders headers = ret.headers();
		headers.set(HttpHeaderNames.CONNECTION, "keep-alive");
		headers.set(HttpHeaderNames.SERVER, server.getServerVersionString());
		headers.set(HttpHeaderNames.DATE, new Date());
		if(body != null) {
			headers.set(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes());
		}else {
			headers.set(HttpHeaderNames.CONTENT_LENGTH, len);
		}
		return ret;
	}

	private FullHttpResponse populateHeadersFrom(FullHttpResponse response, RequestContext context) {
		HttpHeaders headers = response.headers();
		List<Object> obj = context.responseHeaders;
		if(obj != null) {
			int sz = obj.size();
			if(sz > 0 && (sz & 1) == 0) {
				for(int i = 0; i < sz; i += 2) {
					headers.add((String) obj.get(i), obj.get(i + 1));
				}
			}
		}
		return response;
	}

	private void handleUnexpectedMeth(ChannelHandlerContext ctx, HttpMethod method, ResponseOrdering.Slot responseSlot) {
		responseSlot.complete(createResponse(HttpResponseStatus.METHOD_NOT_ALLOWED, null, 0));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(!(cause instanceof ReadTimeoutException) && ctx.channel().isActive()) {
			pipelineData.connectionLogger.error("Uncaught exception in pipeline", cause);
		}
	}

}
