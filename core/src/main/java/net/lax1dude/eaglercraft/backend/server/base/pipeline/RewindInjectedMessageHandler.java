package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.List;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IOutboundInjector;

@ChannelHandler.Sharable
public class RewindInjectedMessageHandler extends MessageToMessageEncoder<IOutboundInjector.IMessage> {

	public static final RewindInjectedMessageHandler INSTANCE = new RewindInjectedMessageHandler();

	@Override
	protected void encode(ChannelHandlerContext ctx, IOutboundInjector.IMessage msg, List<Object> out) throws Exception {
		msg.write(out);
	}

}
