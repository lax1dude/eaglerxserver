package net.lax1dude.eaglercraft.backend.server.api;

public enum EnumRequestMethod {
	GET(0, 1), HEAD(1, 2), PUT(2, 4), DELETE(3, 8), POST(4, 16), PATCH(5, 32), OPTIONS(6, -1);

	public static final int bits = 63;

	private static final EnumRequestMethod[] VALUES = new EnumRequestMethod[] { GET, HEAD, PUT, DELETE, POST, PATCH, OPTIONS };
	private static final EnumRequestMethod[] BIT_LOOKUP = new EnumRequestMethod[] { GET, HEAD, PUT, DELETE, POST, PATCH };

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
		int r = 0, j;
		for(int i = 0; i < methods.length; ++i) {
			j = methods[i].bit;
			if(j == -1) {
				throw new IllegalArgumentException("Cannot have OPTIONS in bitfield!");
			}
			r |= j;
		}
		return r;
	}

	public static EnumRequestMethod[] fromBits(int bits) {
		bits &= EnumRequestMethod.bits;
		int cnt = Integer.bitCount(bits);
		EnumRequestMethod[] ret = new EnumRequestMethod[cnt];
		for(int i = 0; i < cnt; ++i) {
			int j = Integer.numberOfTrailingZeros(bits);
			ret[i] = BIT_LOOKUP[j];
			bits &= ((EnumRequestMethod.bits - 1) << j);
		}
		return ret;
	}

	public static EnumRequestMethod fromId(int id) {
		return id >= 0 && id < VALUES.length ? VALUES[id] : null;
	}

}
