package net.lax1dude.eaglercraft.backend.server.api.event;

public interface IEaglercraftAuthCheckRequiredEvent<PlayerObject, ComponentObject> extends IBaseHandshakeEvent<PlayerObject> {

	public static enum EnumAuthResponse {
		SKIP, REQUIRE, DENY
	}

	public static enum EnumAuthType {
		PLAINTEXT, EAGLER_SHA256, AUTHME_SHA256
	}

	boolean isClientSolicitingPassword();

	byte[] getAuthUsername();

	byte[] getSaltingData();

	void setSaltingData(byte[] saltingData);

	EnumAuthType getUseAuthType();

	void setUseAuthType(EnumAuthType authType);

	EnumAuthResponse getAuthRequired();

	void setAuthRequired(EnumAuthResponse required);

	String getAuthMessage();

	void setAuthMessage(String authMessage);

	boolean getEnableCookieAuth();

	void setEnableCookieAuth(boolean enable);

	ComponentObject getKickMessage();

	void setKickMessage(ComponentObject kickMessage);

	void setKickMessage(String kickMessage);

	default void kickUser(ComponentObject kickMessage) {
		setKickMessage(kickMessage);
		setAuthRequired(EnumAuthResponse.DENY);
	}

	default void kickUser(String kickMessage) {
		setKickMessage(kickMessage);
		setAuthRequired(EnumAuthResponse.DENY);
	}

}
