package net.lax1dude.eaglercraft.backend.server.api;

public enum EnumRequestMethod {
	GET(1), HEAD(2), PUT(4), DELETE(8), POST(16), PATCH(32);

	public static final int bits = 63;

	private static final EnumRequestMethod[] VALUES = values();

	static {
		for(int i = 0; i < VALUES.length; ++i) {
			if((1 << i) != VALUES[i].bit)
				throw new IllegalStateException();
		}
	}

	private final int bit;

	private EnumRequestMethod(int bit) {
		this.bit = bit;
	}

	public int bit() {
		return bit;
	}

	public static int toBits(EnumRequestMethod[] methods) {
		int r = 0;
		for(int i = 0; i < methods.length; ++i) {
			r |= methods[i].bit;
		}
		return r;
	}

	public static EnumRequestMethod[] fromBits(int bits) {
		bits &= EnumRequestMethod.bits;
		int cnt = Integer.bitCount(bits);
		EnumRequestMethod[] ret = new EnumRequestMethod[cnt];
		for(int i = 0; i < cnt; ++i) {
			int j = Integer.numberOfTrailingZeros(bits);
			ret[i] = VALUES[j];
			bits &= ((EnumRequestMethod.bits - 1) << j);
		}
		return ret;
	}

}
