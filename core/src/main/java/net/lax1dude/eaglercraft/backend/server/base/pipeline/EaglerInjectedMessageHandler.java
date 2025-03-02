package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.lax1dude.eaglercraft.backend.server.base.message.InjectedMessage;
import net.lax1dude.eaglercraft.backend.server.base.message.InjectedMessageController;

public class EaglerInjectedMessageHandler extends MessageToMessageCodec<ByteBuf, InjectedMessage> {

	private final InjectedMessageController injectedController;

	public EaglerInjectedMessageHandler(InjectedMessageController injectedController) {
		this.injectedController = injectedController;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, InjectedMessage msg, List<Object> output) throws Exception {
		msg.writePacket(output);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> output) throws Exception {
		if(msg.readableBytes() > 0 && msg.getUnsignedByte(0) == 0xEE) {
			injectedController.readPacket(msg);
		}else {
			output.add(msg);
		}
	}

}
