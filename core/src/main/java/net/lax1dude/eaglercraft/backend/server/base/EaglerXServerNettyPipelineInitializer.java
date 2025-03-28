package net.lax1dude.eaglercraft.backend.server.base;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerNettyPipelineInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformNettyPipelineInitializer;

class EaglerXServerNettyPipelineInitializer<PlayerObject> implements IEaglerXServerNettyPipelineInitializer<NettyPipelineData> {

	private final EaglerXServer<PlayerObject> server;

	EaglerXServerNettyPipelineInitializer(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initialize(IPlatformNettyPipelineInitializer<NettyPipelineData> initializer) {
		EaglerListener eagListener = (EaglerListener) initializer.getListener();
		System.out.println("New channel: " + initializer.getChannel());
		Consumer<SocketAddress> realAddressHandle = null;
		CompoundRateLimiterMap.ICompoundRatelimits rateLimits = null;
		if(eagListener.isForwardIP()) {
			if(eagListener.getConfigData().isSpoofPlayerAddressForwarded()) {
				realAddressHandle = initializer.realAddressHandle();
			}
		}else {
			CompoundRateLimiterMap map = eagListener.getRateLimiter();
			if(map != null) {
				SocketAddress addr = initializer.getChannel().remoteAddress();
				if(addr instanceof InetSocketAddress) {
					rateLimits = map.rateLimit(((InetSocketAddress)addr).getAddress());
					if(rateLimits == null) {
						initializer.getChannel().close();
						return;
					}
				}else {
					server.logger().warn("Unable to ratelimit unknown address type: " + addr.getClass().getName()
							+ " - \"" + addr + "\"");
				}
			}
		}
		NettyPipelineData attachment = new NettyPipelineData(initializer.getChannel(), server, eagListener,
				server.getEaglerAttribManager().createEaglerHolder(), realAddressHandle, rateLimits);
		initializer.setAttachment(attachment);
		if (eagListener.isDualStack()) {
			server.getPipelineTransformer().injectDualStack(initializer.getPipeline(), initializer.getChannel(),
					attachment);
		} else {
			server.getPipelineTransformer().injectSingleStack(initializer.getPipeline(), initializer.getChannel(),
					attachment);
		}
	}

}
