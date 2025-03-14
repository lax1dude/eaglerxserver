package net.lax1dude.eaglercraft.backend.server.api;

public enum EnumRequestMethod {
	GET(0, 1), HEAD(1, 2), PUT(2, 4), DELETE(3, 8), POST(4, 16), PATCH(5, 32);

	public static final int bits = 63;

	private static final EnumRequestMethod[] VALUES = values();

	static {
		for(int i = 0; i < VALUES.length; ++i) {
			if((1 << i) != VALUES[i].bit)
				throw new IllegalStateException();
		}
	}

	private final int id;
	private final int bit;

	private EnumRequestMethod(int id, int bit) {
		this.id = id;
		this.bit = bit;
	}

	public int id() {
		return id;
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
