package net.lax1dude.eaglercraft.backend.server.api.event;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;

public interface IEaglercraftRevokeSessionQueryEvent<PlayerObject> extends IEaglerXServerEvent<PlayerObject> {

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

	IQueryConnection getSocket();

	byte[] getCookieData();

	default String getCookieDataString() {
		byte[] ret = getCookieData();
		return ret != null ? new String(ret, StandardCharsets.UTF_8) : null;
	}

	EnumSessionRevokeStatus getResultStatus();

	void setResultStatus(EnumSessionRevokeStatus revokeStatus);

	boolean getShouldDeleteCookie();

	void setShouldDeleteCookie(boolean flag);

}
