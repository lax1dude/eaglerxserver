package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public final class EaglercraftAuthCheckRequiredEvent
		extends AsyncEvent<IEaglercraftAuthCheckRequiredEvent<ProxiedPlayer, BaseComponent>>
		implements IEaglercraftAuthCheckRequiredEvent<ProxiedPlayer, BaseComponent> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPendingConnection pendingConnection;
	private final boolean clientSolicitingPassword;
	private final byte[] authUsername;
	private boolean nicknameSelectionEnabled;
	private byte[] saltingData;
	private EnumAuthType authType;
	private EnumAuthResponse authRequired;
	private String authMessage = "enter the code:";
	private BaseComponent kickMessage;
	private boolean cookieAuth;

	public EaglercraftAuthCheckRequiredEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerPendingConnection pendingConnection, boolean clientSolicitingPassword, @Nonnull byte[] authUsername,
			@Nonnull Callback<IEaglercraftAuthCheckRequiredEvent<ProxiedPlayer, BaseComponent>> cb) {
		super(cb);
		this.api = api;
		this.pendingConnection = pendingConnection;
		this.clientSolicitingPassword = clientSolicitingPassword;
		this.authUsername = authUsername;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IEaglerPendingConnection getPendingConnection() {
		return pendingConnection;
	}

	@Override
	public boolean isClientSolicitingPassword() {
		return clientSolicitingPassword;
	}

	@Nonnull
	@Override
	public byte[] getAuthUsername() {
		return authUsername;
	}

	@Override
	public boolean isNicknameSelectionEnabled() {
		return nicknameSelectionEnabled;
	}

	@Override
	public void setNicknameSelectionEnabled(boolean enable) {
		nicknameSelectionEnabled = enable;
	}

	@Nullable
	@Override
	public byte[] getSaltingData() {
		return saltingData;
	}

	@Override
	public void setSaltingData(@Nullable byte[] saltingData) {
		this.saltingData = saltingData;
	}

	@Nullable
	@Override
	public EnumAuthType getUseAuthType() {
		return authType;
	}

	@Override
	public void setUseAuthType(@Nullable EnumAuthType authType) {
		this.authType = authType;
	}

	@Nullable
	@Override
	public EnumAuthResponse getAuthRequired() {
		return authRequired;
	}

	@Override
	public void setAuthRequired(@Nullable EnumAuthResponse authRequired) {
		this.authRequired = authRequired;
	}

	@Nullable
	@Override
	public String getAuthMessage() {
		return authMessage;
	}

	@Override
	public void setAuthMessage(@Nullable String authMessage) {
		if(authMessage == null) {
			throw new NullPointerException("authMessage");
		}
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

	@Nullable
	@Override
	public BaseComponent getKickMessage() {
		return kickMessage;
	}

	@Override
	public void setKickMessage(@Nullable BaseComponent kickMessage) {
		this.kickMessage = kickMessage;
	}

	@Override
	public void setKickMessage(@Nullable String kickMessage) {
		this.kickMessage = kickMessage != null ? new TextComponent(kickMessage) : null;
	}

}
