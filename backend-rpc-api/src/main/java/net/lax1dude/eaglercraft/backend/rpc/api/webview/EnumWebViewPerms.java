package net.lax1dude.eaglercraft.backend.rpc.api.webview;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;

public enum EnumWebViewPerms {
	JAVASCRIPT(1),
	MESSAGE_API(2),
	STRICT_CSP(4);

	private final int bit;

	private EnumWebViewPerms(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}

	@Nonnull
	public static Set<EnumWebViewPerms> fromBits(int bits) {
		Set<EnumWebViewPerms> ret = EnumSet.noneOf(EnumWebViewPerms.class);
		if((bits & 1) != 0) ret.add(JAVASCRIPT);
		if((bits & 2) != 0) ret.add(MESSAGE_API);
		if((bits & 4) != 0) ret.add(STRICT_CSP);
		return ret;
	}

	public static int toBits(@Nonnull Set<EnumWebViewPerms> set) {
		int ret = 0;
		for(EnumWebViewPerms perm : set) {
			ret |= perm.bit;
		}
		return ret;
	}

}
