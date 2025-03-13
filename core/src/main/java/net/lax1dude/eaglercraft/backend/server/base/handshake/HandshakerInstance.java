package net.lax1dude.eaglercraft.backend.server.base.handshake;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCookieEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthPasswordEvent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler.IHandshaker;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public abstract class HandshakerInstance implements IHandshaker {

	private static final Pattern USERNAME_REGEX = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
	private static final byte[] OFFLINE_PLAYER_BYTES = "OfflinePlayer:".getBytes(StandardCharsets.US_ASCII);

	protected final EaglerXServer<?> server;
	protected final NettyPipelineData pipelineData;
	protected final WebSocketEaglerInitialHandler inboundHandler;
	protected int state = HandshakePacketTypes.STATE_OPENED;

	protected HandshakerInstance(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketEaglerInitialHandler inboundHandler) {
		this.server = server;
		this.pipelineData = pipelineData;
		this.inboundHandler = inboundHandler;
	}

	protected void handleInvalidData(ChannelHandlerContext ctx) {
		inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_INVALID_PACKET,
				"Invalid Packet");
	}

	protected void handleUnknownPacket(ChannelHandlerContext ctx, int id) {
		inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_UNKNOWN_PACKET,
				"Unknown Packet #" + id);
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
			server.eventDispatcher().dispatchClientBrandEvent(pipelineData.asPendingConnection(), (evt, err) -> {
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
			if(getVersion() <= 1 || pipelineData.handshakeAuthUsername == null) {
				inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE,
						"Outdated Client (Authentication Required)");
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				return;
			}
			server.eventDispatcher().dispatchAuthCheckRequired(pipelineData.asPendingConnection(), pipelineData.handshakeAuthEnabled,
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
						pipelineData.nicknameSelectionEnabled = evt.isNicknameSelectionEnabled();
						pipelineData.cookieAuthEventEnabled = evt.getEnableCookieAuth();
						pipelineData.authType = evt.getUseAuthType();
						pipelineData.authMessage = evt.getAuthMessage();
						switch(response) {
						case SKIP:
							sendPacketVersionNoAuth(ctx, pipelineData.handshakeProtocol, pipelineData.minecraftProtocol,
									server.getServerBrand(), server.getServerVersion());
							state = HandshakePacketTypes.STATE_CLIENT_VERSION;
							break;
						case REQUIRE:
							if(pipelineData.authType == null) {
								inboundHandler.terminateInternalError(ctx, getVersion());
								state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
								pipelineData.connectionLogger.error("Auth required check event handler did not provide auth type");
								break;
							}
							if(!pipelineData.handshakeAuthEnabled && (getVersion() < 4 || !pipelineData.cookieAuthEventEnabled)) {
								inboundHandler.terminated = true;
								ctx.channel().eventLoop().execute(() -> sendPacketAuthRequired(ctx, pipelineData.authType,
										evt.getAuthMessage()).addListener(ChannelFutureListener.CLOSE));
								state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
								break;
							}
							pipelineData.authEventEnabled = true;
							if(sendPacketVersionAuth(ctx, pipelineData.handshakeProtocol, pipelineData.minecraftProtocol,
									server.getServerBrand(), server.getServerVersion(), pipelineData.authType, evt.getSaltingData()) != null) {
								state = HandshakePacketTypes.STATE_CLIENT_VERSION;
							}else {
								state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
							}
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

	protected void handlePacketRequestLogin(ChannelHandlerContext ctx, String requestedUsername, String requestedServer,
			byte[] authPassword, boolean enableCookie, byte[] authCookie) {
		if(state == HandshakePacketTypes.STATE_CLIENT_VERSION) {
			state = HandshakePacketTypes.STATE_STALLING;

			String username;

			byte[] b = pipelineData.handshakeAuthUsername;
			if(b == null) {
				// This shouldn't be null unless auth events are disabled anyway
				int strlen = requestedUsername.length();
				b = new byte[strlen];
				for(int i = 0; i < strlen; ++i) {
					b[i] = (byte)requestedUsername.charAt(i);
				}
				pipelineData.handshakeAuthUsername = b;
				username = requestedUsername;
			}else {
				if(pipelineData.nicknameSelectionEnabled) {
					username = requestedUsername;
					b = requestedUsername.getBytes(StandardCharsets.US_ASCII);
				}else {
					username = new String(b, StandardCharsets.US_ASCII);
				}
			}

			if(!USERNAME_REGEX.matcher(username).matches()) {
				inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, "Invalid Username");
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				return;
			}

			pipelineData.requestedServer = requestedServer;
			pipelineData.cookieEnabled = enableCookie;
			pipelineData.cookieData = authCookie;

			byte[] uuidHashGenerator = new byte[OFFLINE_PLAYER_BYTES.length + b.length];
			System.arraycopy(OFFLINE_PLAYER_BYTES, 0, uuidHashGenerator, 0, OFFLINE_PLAYER_BYTES.length);
			System.arraycopy(b, 0, uuidHashGenerator, OFFLINE_PLAYER_BYTES.length, b.length);
			UUID clientUUID = UUID.nameUUIDFromBytes(uuidHashGenerator);
			
			pipelineData.username = username;
			pipelineData.uuid = clientUUID;
			
			if(pipelineData.authEventEnabled) {
				if(authPassword.length > 0) {
					continueLoginPasswordAuth(ctx, requestedUsername, authPassword);
				}else if(pipelineData.cookieAuthEventEnabled) {
					continueLoginCookieAuth(ctx, requestedUsername);
				}else {
					inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_WRONG_PACKET,
							"Missing Login Packet Password");
					state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				}
			}else if(pipelineData.cookieAuthEventEnabled) {
				continueLoginCookieAuth(ctx, requestedUsername);
			}else {
				continueLoginNoAuth(ctx);
			}
		}else {
			inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_WRONG_PACKET,
					"Wrong Request Login Packet");
			state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
		}
	}

	private void continueLoginNoAuth(ChannelHandlerContext ctx) {
		updateLoggerName();
		ctx.channel().eventLoop().execute(() -> sendPacketAllowLogin(ctx, pipelineData.username, pipelineData.uuid));
		state = HandshakePacketTypes.STATE_CLIENT_LOGIN;
	}

	private void continueLoginPasswordAuth(ChannelHandlerContext ctx, String requestedUsername, byte[] authPassword) {
		server.eventDispatcher().dispatchAuthPasswordEvent(pipelineData.asLoginConnection(),
				pipelineData.handshakeAuthUsername, pipelineData.nicknameSelectionEnabled, pipelineData.authSalt,
				authPassword, pipelineData.cookieEnabled, pipelineData.cookieData, requestedUsername,
				pipelineData.username, pipelineData.uuid, pipelineData.authType, pipelineData.authMessage,
				pipelineData.requestedServer, (evt, err) -> {
			IEaglercraftAuthPasswordEvent.EnumAuthResponse response = evt.getAuthResponse();
			if(response == null) {
				inboundHandler.terminateInternalError(ctx, getVersion());
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				pipelineData.connectionLogger.error("Auth password event was not handled");
			}else if(response == IEaglercraftAuthPasswordEvent.EnumAuthResponse.ALLOW) {
				ctx.channel().eventLoop().execute(() -> {
					pipelineData.username = evt.getProfileUsername();
					pipelineData.uuid = evt.getProfileUUID();
					pipelineData.requestedServer = evt.getAuthRequestedServer();
					updateLoggerName();
					sendPacketAllowLogin(ctx, pipelineData.username, pipelineData.uuid);
				});
				state = HandshakePacketTypes.STATE_CLIENT_LOGIN;
			}else {
				Object obj = evt.getKickMessage();
				if(obj == null) {
					obj = server.componentBuilder().buildTranslationComponent().translation("disconnect.closed").end();
				}
				final Object obj2 = obj;
				ctx.channel().eventLoop().execute(() -> sendPacketDenyLogin(ctx, obj2));
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
			}
		});
	}

	private void continueLoginCookieAuth(ChannelHandlerContext ctx, String requestedUsername) {
		server.eventDispatcher().dispatchAuthCookieEvent(pipelineData.asLoginConnection(),
				pipelineData.handshakeAuthUsername, pipelineData.nicknameSelectionEnabled, pipelineData.cookieEnabled,
				pipelineData.cookieData, requestedUsername, pipelineData.username, pipelineData.uuid,
				pipelineData.authType, pipelineData.authMessage, pipelineData.requestedServer, (evt, err) -> {
			IEaglercraftAuthCookieEvent.EnumAuthResponse response = evt.getAuthResponse();
			if(response == null) {
				inboundHandler.terminateInternalError(ctx, getVersion());
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				pipelineData.connectionLogger.error("Auth cookie event was not handled");
				return;
			}
			switch(response) {
			case ALLOW:
				ctx.channel().eventLoop().execute(() -> {
					pipelineData.username = evt.getProfileUsername();
					pipelineData.uuid = evt.getProfileUUID();
					pipelineData.requestedServer = evt.getAuthRequestedServer();
					updateLoggerName();
					sendPacketAllowLogin(ctx, pipelineData.username, pipelineData.uuid);
				});
				state = HandshakePacketTypes.STATE_CLIENT_LOGIN;
				break;
			case DENY:
				Object obj = evt.getKickMessage();
				if(obj == null) {
					obj = server.componentBuilder().buildTranslationComponent().translation("disconnect.closed").end();
				}
				final Object obj2 = obj;
				ctx.channel().eventLoop().execute(() -> sendPacketDenyLogin(ctx, obj2));
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				break;
			case REQUIRE_AUTH:
				inboundHandler.terminated = true;
				ctx.channel().eventLoop().execute(() -> sendPacketAuthRequired(ctx, pipelineData.authType, evt.getAuthMessage())
						.addListener(ChannelFutureListener.CLOSE));
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
				break;
			}
		});
	}

	private void updateLoggerName() {
		pipelineData.connectionLogger = pipelineData.connectionLogger.createSubLogger(pipelineData.username);
	}

	protected abstract ChannelFuture sendPacketAllowLogin(ChannelHandlerContext ctx, String setUsername, UUID setUUID);

	protected abstract ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, Object component);

	protected abstract ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, String message);

	protected void handlePacketProfileData(ChannelHandlerContext ctx, String key, byte[] value) {
		if(state == HandshakePacketTypes.STATE_CLIENT_LOGIN) {
			if(pipelineData.profileDatas == null) {
				pipelineData.profileDatas = new HashMap<>(4);
				pipelineData.profileDatas.put(key, value);
			}else if(pipelineData.profileDatas.size() >= 8) {
				inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_EXCESSIVE_PROFILE_DATA,
						"Too Many Profile Datas");
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
			}else if(pipelineData.profileDatas.putIfAbsent(key, value) != null) {
				inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_DUPLICATE_PROFILE_DATA,
						"Duplicate Profile Data");
				state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
			}
		}else {
			inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_WRONG_PACKET,
					"Wrong Profile Data Packet");
			state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
		}
	}

	protected void handlePacketFinishLogin(ChannelHandlerContext ctx) {
		if(state == HandshakePacketTypes.STATE_CLIENT_LOGIN) {
			state = HandshakePacketTypes.STATE_STALLING;
			inboundHandler.finishLogin(ctx);
		}else {
			inboundHandler.terminateErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_WRONG_PACKET,
					"Wrong Finish Login Packet");
			state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
		}
	}

	protected abstract ChannelFuture sendPacketFinishLogin(ChannelHandlerContext ctx);

	public void finish(ChannelHandlerContext ctx) {
		sendPacketFinishLogin(ctx);
		state = HandshakePacketTypes.STATE_CLIENT_COMPLETE;
	}

}
