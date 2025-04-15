package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IEaglercraftAuthCheckRequiredEvent<PlayerObject, ComponentObject> extends IBaseHandshakeEvent<PlayerObject> {

	public static enum EnumAuthResponse {
		SKIP, REQUIRE, DENY
	}

	public static enum EnumAuthType {
		PLAINTEXT, EAGLER_SHA256, AUTHME_SHA256
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
	EnumAuthType getUseAuthType();

	void setUseAuthType(@Nullable EnumAuthType authType);

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
