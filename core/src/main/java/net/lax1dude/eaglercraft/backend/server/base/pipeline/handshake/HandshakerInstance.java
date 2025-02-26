package net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketInitialInboundHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public abstract class HandshakerInstance {

	private static final Pattern USERNAME_REGEX = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
	private static final byte[] OFFLINE_PLAYER_BYTES = "OfflinePlayer:".getBytes(StandardCharsets.US_ASCII);

	protected final EaglerXServer<?> server;
	protected final NettyPipelineData pipelineData;
	protected final WebSocketInitialInboundHandler inboundHandler;
	protected int state = HandshakePacketTypes.STATE_OPENED;

	protected HandshakerInstance(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketInitialInboundHandler inboundHandler) {
		this.server = server;
		this.pipelineData = pipelineData;
		this.inboundHandler = inboundHandler;
	}

	protected void handlePacketInit(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString,
			int minecraftVersion, boolean auth, byte[] authUsername) {
		if(state == HandshakePacketTypes.STATE_OPENED) {
			state = HandshakePacketTypes.STATE_STALLING;
			pipelineData.eaglerBrandString = eaglerBrand;
			pipelineData.eaglerVersionString = eaglerVersionString;
			pipelineData.handshakeProtocol = getVersion();
			pipelineData.gameProtocol = getFinalVersion();
			pipelineData.minecraftProtocol = minecraftVersion;
			pipelineData.handshakeAuthEnabled = auth;
			pipelineData.handshakeAuthUsername = authUsername;
			server.eventDispatcher().dispatchClientBrandEvent(pipelineData, (evt, err) -> {
				if(err == null) {
					if(!evt.isCancelled()) {
						continueHandshakeInit(ctx);
					}else {
						Object obj = evt.getMessage();
						if(obj == null) {
							obj = server.componentBuilder().buildTranslationComponent().translation("disconnect.closed").end();
						}
						inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, obj);
						state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
					}
				}else {
					inboundHandler.terminateInternalError(ctx, getVersion());
					state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
					pipelineData.connectionLogger.error("Caught exception dispatching client brand event", err);
				}
			});
		}else {
			inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_WRONG_PACKET,
					"Wrong Initial Packet");
			state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
		}
	}

	private void continueHandshakeInit(ChannelHandlerContext ctx) {
		if(server.isAuthenticationEventsEnabled()) {
			if(getVersion() <= 1) {
				inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE,
						"Outdated Client (Authentication Required)");
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				return;
			}
			server.eventDispatcher().dispatchAuthCheckRequired(pipelineData, pipelineData.handshakeAuthEnabled,
					pipelineData.handshakeAuthUsername, (evt, err) -> {
				if(!ctx.channel().isActive()) {
					return;
				}
				if(err == null) {
					IEaglercraftAuthCheckRequiredEvent.EnumAuthResponse response = evt.getAuthRequired();
					if(response == null) {
						inboundHandler.terminateInternalError(ctx, getVersion());
						state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
						pipelineData.connectionLogger.error("Auth required check event was not handled");
					}else {
						pipelineData.cookieAuthEventEnabled = evt.getEnableCookieAuth();
						switch(response) {
						case SKIP:
							sendPacketVersionNoAuth(ctx, pipelineData.handshakeProtocol, pipelineData.minecraftProtocol,
									server.getServerBrand(), server.getServerVersion());
							state = HandshakePacketTypes.STATE_CLIENT_VERSION;
							break;
						case REQUIRE:
							IEaglercraftAuthCheckRequiredEvent.EnumAuthType type = evt.getUseAuthType();
							if(type == null) {
								inboundHandler.terminateInternalError(ctx, getVersion());
								state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
								pipelineData.connectionLogger.error("Auth required check event handler did not provide auth type");
								break;
							}
							if(!pipelineData.handshakeAuthEnabled) {
								inboundHandler.terminated = true;
								sendPacketAuthRequired(ctx, type, evt.getAuthMessage()).addListener(ChannelFutureListener.CLOSE);
								state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
								break;
							}
							pipelineData.authEventEnabled = true;
							sendPacketVersionAuth(ctx, pipelineData.handshakeProtocol, pipelineData.minecraftProtocol,
									server.getServerBrand(), server.getServerVersion(), type, evt.getSaltingData());
							state = HandshakePacketTypes.STATE_CLIENT_VERSION;
							break;
						case DENY:
							Object obj = evt.getKickMessage();
							if(obj == null) {
								obj = server.componentBuilder().buildTranslationComponent().translation("disconnect.closed").end();
							}
							inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, obj);
							state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
							break;
						}
					}
				}else {
					inboundHandler.terminateInternalError(ctx, getVersion());
					state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
					pipelineData.connectionLogger.error("Caught exception dispatching auth required check event", err);
				}
			});
		}else {
			sendPacketVersionNoAuth(ctx, pipelineData.handshakeProtocol, pipelineData.minecraftProtocol,
					server.getServerBrand(), server.getServerVersion());
			state = HandshakePacketTypes.STATE_CLIENT_VERSION;
		}
	}

	protected abstract int getVersion();

	protected abstract GamePluginMessageProtocol getFinalVersion();

	protected abstract ChannelFuture sendPacketAuthRequired(ChannelHandlerContext ctx,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, String message);

	protected abstract ChannelFuture sendPacketVersionNoAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion);

	protected abstract ChannelFuture sendPacketVersionAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, byte[] authSaltingData);

	protected void handlePacketRequestLogin(ChannelHandlerContext ctx, String username, String requestedServer,
			byte[] authPassword, boolean enableCookie, byte[] authCookie) {
		if(state == HandshakePacketTypes.STATE_CLIENT_VERSION) {
			state = HandshakePacketTypes.STATE_STALLING;
			
			if(!USERNAME_REGEX.matcher(username).matches()) {
				inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "Invalid Username");
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				return;
			}
			
			byte[] b = pipelineData.handshakeAuthUsername;
			if(b == null) {
				int strlen = username.length();
				b = new byte[strlen];
				for(int i = 0; i < strlen; ++i) {
					b[i] = (byte)username.charAt(i);
				}
				pipelineData.handshakeAuthUsername = b;
			}

			pipelineData.requestedServer = requestedServer;
			pipelineData.cookieEnabled = enableCookie;
			pipelineData.cookieData = authCookie;

			byte[] uuidHashGenerator = new byte[OFFLINE_PLAYER_BYTES.length + b.length];
			System.arraycopy(OFFLINE_PLAYER_BYTES, 0, uuidHashGenerator, 0, OFFLINE_PLAYER_BYTES.length);
			System.arraycopy(b, 0, uuidHashGenerator, OFFLINE_PLAYER_BYTES.length, b.length);
			UUID clientUUID = UUID.nameUUIDFromBytes(uuidHashGenerator);
			
			if(pipelineData.authEventEnabled && authPassword.length > 0) {
				//TODO
			}else if(pipelineData.cookieAuthEventEnabled) {
				//TODO
			}else {
				if(username.equals(new String(b, StandardCharsets.US_ASCII))) {
					pipelineData.username = username;
					pipelineData.uuid = clientUUID;
					sendPacketAllowLogin(ctx, username, clientUUID);
					state = HandshakePacketTypes.STATE_CLIENT_LOGIN;
				}else {
					inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE,
							"Nickname selection is disabled");
					state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				}
			}
		}else {
			inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_WRONG_PACKET,
					"Wrong Request Login Packet");
			state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
		}
	}

	protected abstract ChannelFuture sendPacketAllowLogin(ChannelHandlerContext ctx, String setUsername, UUID setUUID);

	protected abstract ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, Object component);

	protected abstract ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, String message);

	protected void handlePacketProfileData(ChannelHandlerContext ctx, String key, byte[] value) {
		
	}

	protected void handlePacketFinishLogin(ChannelHandlerContext ctx) {
		
	}

}
