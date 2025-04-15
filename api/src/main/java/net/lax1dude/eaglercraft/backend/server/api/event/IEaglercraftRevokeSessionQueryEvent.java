package net.lax1dude.eaglercraft.backend.server.api.event;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;

public interface IEaglercraftRevokeSessionQueryEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	public static enum EnumSessionRevokeStatus {
		SUCCESS("ok", -1), FAILED_NOT_SUPPORTED("error", 1), FAILED_NOT_ALLOWED("error", 2),
		FAILED_NOT_FOUND("error", 3), FAILED_SERVER_ERROR("error", 4);
		
		public final String status;
		public final int code;
		
		private EnumSessionRevokeStatus(String status, int code) {
			this.status = status;
			this.code = code;
		}
	}

	@Nonnull
	IQueryConnection getSocket();

	@Nonnull
	byte[] getCookieData();

	@Nonnull
	default String getCookieDataString() {
		return new String(getCookieData(), StandardCharsets.UTF_8);
	}

	@Nonnull
	EnumSessionRevokeStatus getResultStatus();

	void setResultStatus(@Nonnull EnumSessionRevokeStatus revokeStatus);

	boolean getShouldDeleteCookie();

	void setShouldDeleteCookie(boolean flag);

}
