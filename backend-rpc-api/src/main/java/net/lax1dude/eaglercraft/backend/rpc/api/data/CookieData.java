package net.lax1dude.eaglercraft.backend.rpc.api.data;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CookieData {

	@Nonnull
	public static CookieData disabled() {
		return DISABLED;
	}

	@Nonnull
	public static CookieData create(@Nullable byte[] cookieBytes) {
		return new CookieData(true, cookieBytes);
	}

	private static final CookieData DISABLED = new CookieData(false, null);

	private final boolean cookieEnabled;
	private final byte[] cookieBytes;

	private CookieData(boolean cookieEnabled, @Nullable byte[] cookieBytes) {
		this.cookieEnabled = cookieEnabled;
		this.cookieBytes = cookieBytes;
	}

	public boolean isCookieEnabled() {
		return cookieEnabled;
	}

	@Nullable
	public byte[] getCookieBytes() {
		return cookieBytes;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + Arrays.hashCode(cookieBytes);
		result = 31 * result + (cookieEnabled ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CookieData other))
			return false;
		if (!Arrays.equals(cookieBytes, other.cookieBytes))
			return false;
		if (cookieEnabled != other.cookieEnabled)
			return false;
		return true;
	}

}
