package net.lax1dude.eaglercraft.backend.server.bungee.event;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftAuthPasswordEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthPasswordEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent.EnumAuthType;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeAuthPasswordEventImpl extends EaglercraftAuthPasswordEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPendingConnection pendingConnection;
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
	private BaseComponent kickMessage;
	private String texturesPropertyValue;
	private String texturesPropertySignature;
	private boolean forceVanillaSkin;

	BungeeAuthPasswordEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPendingConnection pendingConnection,
			byte[] authUsername, byte[] authSaltingData, byte[] authPasswordData, boolean cookiesEnabled,
			byte[] cookieData, String profileUsername, UUID profileUUID, EnumAuthType authType, String authMessage,
			String authRequestedServer, Callback<IEaglercraftAuthPasswordEvent<ProxiedPlayer, BaseComponent>> cb) {
		super(cb);
		this.api = api;
		this.pendingConnection = pendingConnection;
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
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPendingConnection getPendingConnection() {
		return pendingConnection;
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
	public BaseComponent getKickMessage() {
		return kickMessage;
	}

	@Override
	public void setKickMessage(BaseComponent kickMessage) {
		this.kickMessage = kickMessage;
	}

	@Override
	public void setKickMessage(String kickMessage) {
		this.kickMessage = new TextComponent(kickMessage);
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
