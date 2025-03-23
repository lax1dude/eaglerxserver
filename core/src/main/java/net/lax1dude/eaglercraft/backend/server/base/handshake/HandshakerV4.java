package net.lax1dude.eaglercraft.backend.server.base.handshake;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class HandshakerV4 extends HandshakerV3 {

	public HandshakerV4(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketEaglerInitialHandler inboundHandler) {
		super(server, pipelineData, inboundHandler);
	}

	public void init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString,
			int minecraftVersion, boolean auth, byte[] authUsername) {
		handlePacketInit(ctx, eaglerBrand, eaglerVersionString, minecraftVersion, auth, authUsername);
	}

	@Override
	protected int getVersion() {
		return 4;
	}

	@Override
	protected GamePluginMessageProtocol getFinalVersion() {
		return GamePluginMessageProtocol.V4;
	}

	private static final int CAPABILITIES_MASK = EnumCapabilityType.UPDATE.getBit() | EnumCapabilityType.VOICE.getBit()
			| EnumCapabilityType.REDIRECT.getBit() | EnumCapabilityType.NOTIFICATION.getBit()
			| EnumCapabilityType.PAUSE_MENU.getBit() | EnumCapabilityType.WEBVIEW.getBit()
			| EnumCapabilityType.COOKIE.getBit();

	private static final int[] CAPABILITIES_VER = new int[] { 1, 1, 1, 1, 1, 1, 1 };

	@Override
	protected int fallbackCapabilityMask() {
		return CAPABILITIES_MASK;
	}

	@Override
	protected int[] fallbackCapabilityVers() {
		return CAPABILITIES_VER;
	}

	@Override
	protected void handleInboundRequestLogin(ChannelHandlerContext ctx, ByteBuf buffer) {
		int strlen = buffer.readUnsignedByte();
		String username = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
		strlen = buffer.readUnsignedByte();
		String requestedServer = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
		strlen = buffer.readUnsignedByte();
		byte[] authPassword = Util.newByteArray(strlen);
		buffer.readBytes(authPassword);
		boolean enableCookie = buffer.readBoolean();
		int cookieLen = buffer.readUnsignedByte();
		byte[] cookieData = Util.ZERO_BYTES;
		if(enableCookie) {
			cookieData = Util.newByteArray(cookieLen);
			buffer.readBytes(cookieData);
		}else {
			if(cookieLen > 0) {
				throw new IndexOutOfBoundsException();
			}
		}
		if(buffer.isReadable()) {
			throw new IndexOutOfBoundsException();
		}
		handlePacketRequestLogin(ctx, username, requestedServer, authPassword, enableCookie, cookieData,
				fallbackCapabilityMask(), fallbackCapabilityVers());
	}

	@Override
	protected void handleInboundProfileData(ChannelHandlerContext ctx, ByteBuf buffer) {
		int count = buffer.readUnsignedByte();
		while(--count >= 0 && !inboundHandler.terminated) {
			super.handleInboundProfileData(ctx, buffer);
		}
	}

}
