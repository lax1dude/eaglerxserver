package net.lax1dude.eaglercraft.backend.server.api.velocity.event;

import java.util.UUID;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCookieEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent.EnumAuthType;

public class VEaglercraftAuthCookieEvent extends VEaglercraftBaseEvent implements IEaglercraftAuthCookieEvent<Player, Component> {

	private final IEaglerPendingConnection pendingConnection;
	private final byte[] authUsername;
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

	public VEaglercraftAuthCookieEvent(IEaglerXServerAPI<Player> api, IEaglerPendingConnection pendingConnection,
			byte[] authUsername, boolean cookiesEnabled, byte[] cookieData, String profileUsername, UUID profileUUID,
			EnumAuthType authType, String authMessage, String authRequestedServer) {
		super(api);
		this.pendingConnection = pendingConnection;
		this.authUsername = authUsername;
		this.cookiesEnabled = cookiesEnabled;
		this.cookieData = cookieData;
		this.profileUsername = profileUsername;
		this.profileUUID = profileUUID;
		this.authType = authType;
		this.authMessage = authMessage;
		this.authRequestedServer = authRequestedServer;
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
	public boolean getCookiesEnabled() {
		return cookiesEnabled;
	}

	@Override
	public byte[] getCookieData() {
		return cookieData;
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
