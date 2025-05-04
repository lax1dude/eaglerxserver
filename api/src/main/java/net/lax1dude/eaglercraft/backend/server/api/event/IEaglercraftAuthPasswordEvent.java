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

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.UnsupportedOn;

public interface IEaglercraftAuthPasswordEvent<PlayerObject, ComponentObject> extends IBaseLoginEvent<PlayerObject> {

	public static enum EnumAuthResponse {
		ALLOW, DENY
	}

	@Nonnull
	byte[] getAuthUsername();

	boolean isNicknameSelectionEnabled();

	@Nullable
	byte[] getAuthSaltingData();

	boolean getCookiesEnabled();

	@Nullable
	byte[] getCookieData();

	@Nullable
	default String getCookieDataString() {
		byte[] ret = getCookieData();
		return ret != null ? new String(ret, StandardCharsets.UTF_8) : null;
	}

	@Nonnull
	byte[] getAuthPasswordDataResponse();

	@Nonnull
	String getRequestedNickname();

	@Nonnull
	String getProfileUsername();

	void setProfileUsername(@Nonnull String username);

	@Nonnull
	UUID getProfileUUID();

	@UnsupportedOn({ EnumPlatformType.BUKKIT })
	void setProfileUUID(@Nonnull UUID uuid);

	byte getAuthTypeRaw();

	@Nullable
	default IEaglercraftAuthCheckRequiredEvent.EnumAuthType getAuthType() {
		return IEaglercraftAuthCheckRequiredEvent.EnumAuthType.getById(getAuthTypeRaw());
	}

	@Nullable
	String getAuthMessage();

	@Nonnull
	String getAuthRequestedServer();

	void setAuthRequestedServer(@Nonnull String server);

	@Nullable
	EnumAuthResponse getAuthResponse();

	void setAuthResponse(@Nullable EnumAuthResponse response);

	@Nullable
	ComponentObject getKickMessage();

	void setKickMessage(@Nullable ComponentObject kickMessage);

	void setKickMessage(@Nullable String kickMessage);

	default void setLoginAllowed() {
		setAuthResponse(EnumAuthResponse.ALLOW);
	}

	default void setLoginDenied(@Nullable ComponentObject kickMessage) {
		setKickMessage(kickMessage);
		setAuthResponse(EnumAuthResponse.DENY);
	}

	default void setLoginDenied(@Nullable String kickMessage) {
		setKickMessage(kickMessage);
		setAuthResponse(EnumAuthResponse.DENY);
	}

	void applyTexturesProperty(@Nullable String value, @Nullable String signature);

	@Nullable
	String getAppliedTexturesPropertyValue();

	@Nullable
	String getAppliedTexturesPropertySignature();

	void setOverrideEaglerToVanillaSkins(boolean flag);

	boolean isOverrideEaglerToVanillaSkins();

}
