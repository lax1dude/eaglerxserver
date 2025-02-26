package net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake;

import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketInitialInboundHandler;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketInitialInboundHandler.IHandshaker;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class HandshakerV1 extends HandshakerInstance implements IHandshaker {

	public HandshakerV1(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketInitialInboundHandler inboundHandler) {
		super(server, pipelineData, inboundHandler);
	}

	public HandshakerV1 init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString) {
		handlePacketInit(ctx, eaglerBrand, eaglerVersionString, 47, false, null);
		return this;
	}

	@Override
	public void handleInbound(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
		
	}

	@Override
	public boolean handleOutbound(ChannelHandlerContext ctx, ByteBuf buffer) {
		return false;
	}

	@Override
	protected int getVersion() {
		return 1;
	}

	@Override
	protected GamePluginMessageProtocol getFinalVersion() {
		return GamePluginMessageProtocol.V3;
	}

	@Override
	protected ChannelFuture sendPacketAuthRequired(ChannelHandlerContext ctx,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ChannelFuture sendPacketVersionNoAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ChannelFuture sendPacketVersionAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, byte[] authSaltingData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ChannelFuture sendPacketAllowLogin(ChannelHandlerContext ctx, String setUsername, UUID setUUID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, Object component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, String message) {
		// TODO Auto-generated method stub
		return null;
	}

}
