package net.lax1dude.eaglercraft.backend.server.api.nbt;

public enum EnumDataType {
	BYTE(1), SHORT(2), INT(3), LONG(4), FLOAT(5), DOUBLE(6), BYTE_ARRAY(7),
	STRING(8), LIST(9), COMPOUND(10), INT_ARRAY(11), FLOAT_ARRAY(12);

	private final int id;

	private EnumDataType(int i) {
		this.id = i;
	}

	public int getId() {
		return id;
	}

	private static final EnumDataType[] VALUES;

	public static EnumDataType getById(int id) {
		return id >= 0 && id < VALUES.length ? VALUES[id] : null;
	}

	static {
		VALUES = new EnumDataType[13];
		for(EnumDataType tag : values()) {
			VALUES[tag.id] = tag;
		}
	}

}