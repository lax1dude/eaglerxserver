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

package net.lax1dude.eaglercraft.backend.server.velocity.event;

import java.util.UUID;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftAuthCookieEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent.EnumAuthType;

class VelocityAuthCookieEventImpl extends EaglercraftAuthCookieEvent {

	private final IEaglerXServerAPI<Player> api;
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
	private Component kickMessage;
	private String texturesPropertyValue;
	private String texturesPropertySignature;
	private boolean forceVanillaSkin;

	VelocityAuthCookieEventImpl(IEaglerXServerAPI<Player> api, IEaglerLoginConnection loginConnection,
			byte[] authUsername, boolean nicknameSelectionEnabled, boolean cookiesEnabled, byte[] cookieData, String requestedUsername, String profileUsername, UUID profileUUID,
			EnumAuthType authType, String authMessage, String authRequestedServer) {
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
	public boolean isNicknameSelectionEnabled() {
		return nicknameSelectionEnabled;
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
	public String getRequestedNickname() {
		return requestedUsername;
	}

	@Override
	public String getProfileUsername() {
		return profileUsername;
	}

	@Override
	public void setProfileUsername(String username) {
		if(username == null) {
			throw new NullPointerException("username");
		}
		profileUsername = username;
	}

	@Override
	public UUID getProfileUUID() {
		return profileUUID;
	}

	@Override
	public void setProfileUUID(UUID uuid) {
		if(uuid == null) {
			throw new NullPointerException("uuid");
		}
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
		if(server == null) {
			throw new NullPointerException("server");
		}
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
		this.kickMessage = kickMessage != null ? Component.text(kickMessage) : null;
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
