package net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake;

import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketInitialInboundHandler;

public abstract class HandshakerInstance {

	protected final WebSocketInitialInboundHandler inboundHandler;

	protected HandshakerInstance(WebSocketInitialInboundHandler inboundHandler) {
		this.inboundHandler = inboundHandler;
	}

	protected void handlePacketInit(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString, int minecraftVersion, boolean auth, byte[] authUsername) {
		
	}

	protected abstract void sendPacketFailureCode(ChannelHandlerContext ctx, int code, Object component);

	protected abstract void sendPacketFailureCode(ChannelHandlerContext ctx, int code, String message);

	protected abstract void sendPacketAuthRequired(ChannelHandlerContext ctx, int code, int authMethod, String message);

	protected abstract void sendPacketVersionNoAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol, int selectedMinecraftProtocol, String serverBrand, String serverVersion);

	protected abstract void sendPacketVersionAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol, int selectedMinecraftProtocol, String serverBrand, String serverVersion, int authMethod, byte[] authSaltingData);

	protected void handlePacketRequestLogin(ChannelHandlerContext ctx, String username, String requestedUsername, byte[] authPassword, boolean enableCookie, byte[] authCookie) {
		
	}

	protected abstract void sendPacketAllowLogin(ChannelHandlerContext ctx, String setUsername, UUID setUUID);

	protected abstract void sendPacketDenyLogin(ChannelHandlerContext ctx, Object component);

	protected abstract void sendPacketDenyLogin(ChannelHandlerContext ctx, String message);

	protected void handlePacketProfileData(ChannelHandlerContext ctx, String key, byte[] value) {
		
	}

	protected void handlePacketFinishLogin(ChannelHandlerContext ctx) {
		
	}

}
