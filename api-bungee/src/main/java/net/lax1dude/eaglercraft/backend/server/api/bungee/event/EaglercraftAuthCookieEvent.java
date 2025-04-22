/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCookieEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent.EnumAuthType;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public final class EaglercraftAuthCookieEvent
		extends AsyncEvent<IEaglercraftAuthCookieEvent<ProxiedPlayer, BaseComponent>>
		implements IEaglercraftAuthCookieEvent<ProxiedPlayer, BaseComponent> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerLoginConnection loginConnection;
	private final byte[] authUsername;
	private final boolean nicknameSelectionEnabled;
	private final boolean cookiesEnabled;
	private final byte[] cookieData;
	private final String requestedUsername;
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

	public EaglercraftAuthCookieEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerLoginConnection loginConnection, @Nonnull byte[] authUsername,
			boolean nicknameSelectionEnabled, boolean cookiesEnabled, @Nullable byte[] cookieData,
			@Nonnull String requestedUsername, @Nonnull String profileUsername, @Nonnull UUID profileUUID,
			@Nullable EnumAuthType authType, @Nullable String authMessage, @Nonnull String authRequestedServer,
			@Nonnull Callback<IEaglercraftAuthCookieEvent<ProxiedPlayer, BaseComponent>> cb) {
		super(cb);
		this.api = api;
		this.loginConnection = loginConnection;
		this.authUsername = authUsername;
		this.nicknameSelectionEnabled = nicknameSelectionEnabled;
		this.cookiesEnabled = cookiesEnabled;
		this.cookieData = cookieData;
		this.requestedUsername = requestedUsername;
		this.profileUsername = profileUsername;
		this.profileUUID = profileUUID;
		this.authType = authType;
		this.authMessage = authMessage;
		this.authRequestedServer = authRequestedServer;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IEaglerLoginConnection getLoginConnection() {
		return loginConnection;
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
	public boolean getCookiesEnabled() {
		return cookiesEnabled;
	}

	@Nullable
	@Override
	public byte[] getCookieData() {
		return cookieData;
	}

	@Nonnull
	@Override
	public String getRequestedNickname() {
		return requestedUsername;
	}

	@Nonnull
	@Override
	public String getProfileUsername() {
		return profileUsername;
	}

	@Override
	public void setProfileUsername(@Nonnull String username) {
		if(username == null) {
			throw new NullPointerException("username");
		}
		profileUsername = username;
	}

	@Nonnull
	@Override
	public UUID getProfileUUID() {
		return profileUUID;
	}

	@Override
	public void setProfileUUID(@Nonnull UUID uuid) {
		if(uuid == null) {
			throw new NullPointerException("uuid");
		}
		profileUUID = uuid;
	}

	@Nullable
	@Override
	public EnumAuthType getAuthType() {
		return authType;
	}

	@Nullable
	@Override
	public String getAuthMessage() {
		return authMessage;
	}

	@Nonnull
	@Override
	public String getAuthRequestedServer() {
		return authRequestedServer;
	}

	@Override
	public void setAuthRequestedServer(@Nonnull String server) {
		if(server == null) {
			throw new NullPointerException("server");
		}
		authRequestedServer = server;
	}

	@Nullable
	@Override
	public EnumAuthResponse getAuthResponse() {
		return authResponse;
	}

	@Override
	public void setAuthResponse(@Nullable EnumAuthResponse response) {
		authResponse = response;
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

	@Override
	public void applyTexturesProperty(@Nullable String value, @Nullable String signature) {
		texturesPropertyValue = value;
		texturesPropertySignature = signature;
	}

	@Nullable
	@Override
	public String getAppliedTexturesPropertyValue() {
		return texturesPropertyValue;
	}

	@Nullable
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
