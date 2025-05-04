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

package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IEaglercraftAuthCheckRequiredEvent<PlayerObject, ComponentObject>
		extends IBaseHandshakeEvent<PlayerObject> {

	public static enum EnumAuthResponse {
		SKIP, REQUIRE, DENY
	}

	public static enum EnumAuthType {
		PLAINTEXT((byte) 255),
		EAGLER_SHA256((byte) 1),
		AUTHME_SHA256((byte) 2);

		private byte id;

		private EnumAuthType(byte id) {
			this.id = id;
		}

		public byte getId() {
			return id;
		}

		public static EnumAuthType getById(byte id) {
			return switch (id) {
			case (byte) 255 -> PLAINTEXT;
			case (byte) 1 -> EAGLER_SHA256;
			case (byte) 2 -> AUTHME_SHA256;
			default -> null;
			};
		}

	}

	boolean isClientSolicitingPassword();

	@Nonnull
	byte[] getAuthUsername();

	boolean isNicknameSelectionEnabled();

	void setNicknameSelectionEnabled(boolean enable);

	@Nullable
	byte[] getSaltingData();

	void setSaltingData(@Nullable byte[] saltingData);

	@Nullable
	default EnumAuthType getUseAuthType() {
		return EnumAuthType.getById(getUseAuthTypeRaw());
	}

	default void setUseAuthType(@Nullable EnumAuthType authType) {
		setUseAuthTypeRaw(authType != null ? authType.id : (byte) 0);
	}

	byte getUseAuthTypeRaw();

	void setUseAuthTypeRaw(byte authType);

	@Nullable
	EnumAuthResponse getAuthRequired();

	void setAuthRequired(@Nullable EnumAuthResponse required);

	@Nonnull
	String getAuthMessage();

	void setAuthMessage(@Nonnull String authMessage);

	boolean getEnableCookieAuth();

	void setEnableCookieAuth(boolean enable);

	@Nullable
	ComponentObject getKickMessage();

	void setKickMessage(@Nullable ComponentObject kickMessage);

	void setKickMessage(@Nullable String kickMessage);

	default void kickUser(@Nullable ComponentObject kickMessage) {
		setKickMessage(kickMessage);
		setAuthRequired(EnumAuthResponse.DENY);
	}

	default void kickUser(@Nullable String kickMessage) {
		setKickMessage(kickMessage);
		setAuthRequired(EnumAuthResponse.DENY);
	}

}
