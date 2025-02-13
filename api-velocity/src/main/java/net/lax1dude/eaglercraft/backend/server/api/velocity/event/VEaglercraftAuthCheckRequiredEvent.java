package net.lax1dude.eaglercraft.backend.server.api.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;

public class VEaglercraftAuthCheckRequiredEvent extends VEaglercraftBaseEvent implements IEaglercraftAuthCheckRequiredEvent<Player, Component> {

	private final IEaglerPendingConnection pendingConnection;
	private final boolean clientSolicitingPassword;
	private final byte[] authUsername;
	private byte[] saltingData;
	private EnumAuthType authType;
	private EnumAuthResponse authRequired;
	private String authMessage = "enter the code:";
	private Component kickMessage;
	private boolean cookieAuth;

	public VEaglercraftAuthCheckRequiredEvent(IEaglerXServerAPI<Player> api, IEaglerPendingConnection pendingConnection,
			boolean clientSolicitingPassword, byte[] authUsername) {
		super(api);
		this.pendingConnection = pendingConnection;
		this.clientSolicitingPassword = clientSolicitingPassword;
		this.authUsername = authUsername;
	}

	@Override
	public IEaglerPendingConnection getPendingConnection() {
		return pendingConnection;
	}

	@Override
	public boolean isClientSolicitingPassword() {
		return clientSolicitingPassword;
	}

	@Override
	public byte[] getAuthUsername() {
		return authUsername;
	}

	@Override
	public byte[] getSaltingData() {
		return saltingData;
	}

	@Override
	public void setSaltingData(byte[] saltingData) {
		this.saltingData = saltingData;
	}

	@Override
	public EnumAuthType getUseAuthType() {
		return authType;
	}

	@Override
	public void setUseAuthType(EnumAuthType authType) {
		this.authType = authType;
	}

	@Override
	public EnumAuthResponse getAuthRequired() {
		return authRequired;
	}

	@Override
	public void setAuthRequired(EnumAuthResponse authRequired) {
		this.authRequired = authRequired;
	}

	@Override
	public String getAuthMessage() {
		return authMessage;
	}

	@Override
	public void setAuthMessage(String authMessage) {
		this.authMessage = authMessage;
	}

	@Override
	public boolean getEnableCookieAuth() {
		return cookieAuth;
	}

	@Override
	public void setEnableCookieAuth(boolean enable) {
		this.cookieAuth = enable;
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

}
