package net.lax1dude.eaglercraft.backend.server.velocity.event;

import java.util.UUID;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent.EnumAuthType;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftAuthPasswordEvent;

class VelocityAuthPasswordEventImpl extends EaglercraftAuthPasswordEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerLoginConnection loginConnection;
	private final byte[] authUsername;
	private final byte[] authSaltingData;
	private final byte[] authPasswordData;
	private final boolean cookiesEnabled;
	private final byte[] cookieData;
	private String profileUsername;
	private UUID profileUUID;
	private final EnumAuthType authType;
	private final String authMessage;
	private String authRequestedServer;
	private EnumAuthResponse authResponse;
	private Component kickMessage;
	private String texturesPropertyValue;
	private String texturesPropertySignature;
	private boolean forceVanillaSkin;

	VelocityAuthPasswordEventImpl(IEaglerXServerAPI<Player> api, IEaglerLoginConnection loginConnection,
			byte[] authUsername, byte[] authSaltingData, byte[] authPasswordData, boolean cookiesEnabled,
			byte[] cookieData, String profileUsername, UUID profileUUID, EnumAuthType authType, String authMessage,
			String authRequestedServer) {
		this.api = api;
		this.loginConnection = loginConnection;
		this.authUsername = authUsername;
		this.authSaltingData = authSaltingData;
		this.authPasswordData = authPasswordData;
		this.cookiesEnabled = cookiesEnabled;
		this.cookieData = cookieData;
		this.profileUsername = profileUsername;
		this.profileUUID = profileUUID;
		this.authType = authType;
		this.authMessage = authMessage;
		this.authRequestedServer = authRequestedServer;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerLoginConnection getLoginConnection() {
		return loginConnection;
	}

	@Override
	public byte[] getAuthUsername() {
		return authUsername;
	}

	@Override
	public byte[] getAuthSaltingData() {
		return authSaltingData;
	}

	@Override
	public boolean getCookiesEnabled() {
		return cookiesEnabled;
	}

	@Override
	public byte[] getCookieData() {
		return cookieData;
	}

	@Override
	public byte[] getAuthPasswordDataResponse() {
		return authPasswordData;
	}

	@Override
	public String getProfileUsername() {
		return profileUsername;
	}

	@Override
	public void setProfileUsername(String username) {
		profileUsername = username;
	}

	@Override
	public UUID getProfileUUID() {
		return profileUUID;
	}

	@Override
	public void setProfileUUID(UUID uuid) {
		profileUUID = uuid;
	}

	@Override
	public EnumAuthType getAuthType() {
		return authType;
	}

	@Override
	public String getAuthMessage() {
		return authMessage;
	}

	@Override
	public String getAuthRequestedServer() {
		return authRequestedServer;
	}

	@Override
	public void setAuthRequestedServer(String server) {
		authRequestedServer = server;
	}

	@Override
	public EnumAuthResponse getAuthResponse() {
		return authResponse;
	}

	@Override
	public void setAuthResponse(EnumAuthResponse response) {
		authResponse = response;
	}

	@Override
	public Component getKickMessage() {
		return kickMessage;
	}

	@Override
	public void setKickMessage(Component kickMessage) {
		this.kickMessage = kickMessage;
	}

	@Override
	public void setKickMessage(String kickMessage) {
		this.kickMessage = Component.text(kickMessage);
	}

	@Override
	public void applyTexturesProperty(String value, String signature) {
		texturesPropertyValue = value;
		texturesPropertySignature = signature;
	}

	@Override
	public String getAppliedTexturesPropertyValue() {
		return texturesPropertyValue;
	}

	@Override
	public String getAppliedTexturesPropertySignature() {
		return texturesPropertySignature;
	}

	@Override
	public void setOverrideEaglerToVanillaSkins(boolean flag) {
		forceVanillaSkin = flag;
	}

	@Override
	public boolean isOverrideEaglerToVanillaSkins() {
		return forceVanillaSkin;
	}

}
