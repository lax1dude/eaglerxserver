package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftAuthCheckRequiredEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

class BukkitAuthCheckRequiredEventImpl extends EaglercraftAuthCheckRequiredEvent {

	private final IEaglerXServerAPI<Player> api;
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

	BukkitAuthCheckRequiredEventImpl(IEaglerXServerAPI<Player> api, IEaglerPendingConnection pendingConnection,
			boolean clientSolicitingPassword, byte[] authUsername) {
		this.api = api;
		this.pendingConnection = pendingConnection;
		this.clientSolicitingPassword = clientSolicitingPassword;
		this.authUsername = authUsername;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
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
	public boolean isNicknameSelectionEnabled() {
		return nicknameSelectionEnabled;
	}

	@Override
	public void setNicknameSelectionEnabled(boolean enable) {
		nicknameSelectionEnabled = enable;
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
	public BaseComponent getKickMessage() {
		return kickMessage;
	}

	@Override
	public void setKickMessage(BaseComponent kickMessage) {
		this.kickMessage = kickMessage;
	}

	@Override
	public void setKickMessage(String kickMessage) {
		this.kickMessage = kickMessage != null ? new TextComponent(kickMessage) : null;
	}

}
