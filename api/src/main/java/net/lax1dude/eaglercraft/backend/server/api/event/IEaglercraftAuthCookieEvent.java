package net.lax1dude.eaglercraft.backend.server.api.event;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.UnsupportedOn;

public interface IEaglercraftAuthCookieEvent<PlayerObject, ComponentObject> extends IBaseLoginEvent<PlayerObject> {

	public static enum EnumAuthResponse {
		ALLOW, DENY, REQUIRE_AUTH
	}

	@Nonnull
	byte[] getAuthUsername();

	boolean isNicknameSelectionEnabled();

	boolean getCookiesEnabled();

	@Nullable
	byte[] getCookieData();

	@Nullable
	default String getCookieDataString() {
		byte[] ret = getCookieData();
		return ret != null ? new String(ret, StandardCharsets.UTF_8) : null;
	}

	@Nonnull
	String getRequestedNickname();

	@Nonnull
	String getProfileUsername();

	void setProfileUsername(@Nonnull String username);

	@Nonnull
	UUID getProfileUUID();

	@UnsupportedOn({EnumPlatformType.BUKKIT})
	void setProfileUUID(@Nonnull UUID uuid);

	@Nullable
	IEaglercraftAuthCheckRequiredEvent.EnumAuthType getAuthType();

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

	default void setLoginPasswordRequired() {
		setAuthResponse(EnumAuthResponse.REQUIRE_AUTH);
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
