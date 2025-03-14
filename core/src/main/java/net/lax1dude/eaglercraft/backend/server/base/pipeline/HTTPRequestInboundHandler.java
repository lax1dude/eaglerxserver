package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import com.google.common.collect.ImmutableMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.webserver.RequestContext;
import net.lax1dude.eaglercraft.backend.server.base.webserver.RouteMap;
import net.lax1dude.eaglercraft.backend.server.base.webserver.RouteProcessor;

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
	private RouteProcessor processor;
	private RequestContext context;

	public HTTPRequestInboundHandler(EaglerXServer<?> server, NettyPipelineData pipelineData) {
		this.server = server;
		this.pipelineData = pipelineData;
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
	public void channelRead(ChannelHandlerContext ctx, Object msgRaw) throws Exception {
		try {
			if(msgRaw instanceof FullHttpRequest) {
				FullHttpRequest msg = (FullHttpRequest) msgRaw;
				HttpMethod method = msg.method();
				EnumRequestMethod meth = methodLookup.get(method);
				if(meth == null) {
					handleUnexpectedMeth(method);
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
				RequestContext context = context();
				context.setContext(meth, uri, path, query, ctx, msg);
				try {
					RouteMap.Result<IRequestHandler> handlerResult = server.getWebServer()
							.resolveInternal(pipelineData.listenerInfo, meth.id(), path, processor());
					if(handlerResult.result != null) {
						boolean dir = path.endsWith("/");
						if(dir != handlerResult.directory) {
							//TODO
						}
						handlerResult.result.handleRequest(context);
						//TODO
					}else {
						//TODO
					}
				}finally {
					context.clearResult();
				}
			}
		}finally {
			ReferenceCountUtil.release(msgRaw);
		}
	}

	private void handleUnexpectedMeth(HttpMethod method) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (ctx.channel().isActive()) {
			
		}
	}

}
