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

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftAuthCheckRequiredEvent;

class VelocityAuthCheckRequiredEventImpl extends EaglercraftAuthCheckRequiredEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPendingConnection pendingConnection;
	private final boolean clientSolicitingPassword;
	private final byte[] authUsername;
	private boolean nicknameSelectionEnabled;
	private byte[] saltingData;
	private EnumAuthType authType;
	private EnumAuthResponse authRequired;
	private String authMessage = "enter the code:";
	private Component kickMessage;
	private boolean cookieAuth;

	VelocityAuthCheckRequiredEventImpl(IEaglerXServerAPI<Player> api, IEaglerPendingConnection pendingConnection,
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
		if (authMessage == null) {
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

}
