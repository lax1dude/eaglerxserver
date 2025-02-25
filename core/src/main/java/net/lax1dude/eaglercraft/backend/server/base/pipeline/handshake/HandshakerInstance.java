package net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketInitialInboundHandler;

public abstract class HandshakerInstance {

	protected final WebSocketInitialInboundHandler inboundHandler;

	protected HandshakerInstance(WebSocketInitialInboundHandler inboundHandler) {
		this.inboundHandler = inboundHandler;
	}

	protected void handlePacketInit(String eaglerBrand, String eaglerVersionString, int minecraftVersion, boolean auth, byte[] authUsername) {
		
	}

	protected abstract void sendPacketFailureCode(int code, Object component);

	protected abstract void sendPacketFailureCode(int code, String message);

	protected abstract void sendPacketAuthRequired(int code, int authMethod, String message);

	protected abstract void sendPacketVersionNoAuth(int selectedEaglerProtocol, int selectedMinecraftProtocol, String serverBrand, String serverVersion);

	protected abstract void sendPacketVersionAuth(int selectedEaglerProtocol, int selectedMinecraftProtocol, String serverBrand, String serverVersion, int authMethod, byte[] authSaltingData);

	protected void handlePacketRequestLogin(String username, String requestedUsername, byte[] authPassword, boolean enableCookie, byte[] authCookie) {
		
	}

	protected abstract void sendPacketAllowLogin(String setUsername, UUID setUUID);

	protected abstract void sendPacketDenyLogin(Object component);

	protected abstract void sendPacketDenyLogin(String message);

	protected void handlePacketProfileData(String key, byte[] value) {
		
	}

	protected void handlePacketFinishLogin() {
		
	}

}
