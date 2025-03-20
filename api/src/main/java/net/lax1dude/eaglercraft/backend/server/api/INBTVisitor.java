package net.lax1dude.eaglercraft.backend.server.api;

import java.io.DataOutput;
import java.io.IOException;

public interface INBTVisitor {

	public static enum EnumTag {
		BYTE(1), SHORT(2), INT(3), LONG(4), FLOAT(5), DOUBLE(6), BYTE_ARRAY(7),
		STRING(8), LIST(9), COMPOUND(10), INT_ARRAY(11), FLOAT_ARRAY(12);

		private final int id;

		private EnumTag(int i) {
			this.id = i;
		}

		public int getId() {
			return id;
		}

		private static final EnumTag[] VALUES;

		public static EnumTag getById(int id) {
			return id >= 0 && id < VALUES.length ? VALUES[id] : null;
		}

		static {
			VALUES = new EnumTag[13];
			for(EnumTag tag : values()) {
				VALUES[tag.id] = tag;
			}
		}

	}

	public interface INBTValue<T> {

		void mutate(T value) throws IOException;

		void write(DataOutput dataOutput, byte[] tmp) throws IOException;

		T value() throws IOException;

	}

	public static final INBTVisitor NOP = new NOPVisitorImpl();

	INBTVisitor parent();

	default INBTVisitor visitRootTag(EnumTag tagType) throws IOException {
		return parent().visitRootTag(tagType);
	}

	default INBTVisitor visitTag(EnumTag tagType, INBTValue<String> tagName) throws IOException {
		return parent().visitTag(tagType, tagName);
	}

	default INBTVisitor visitTagList(EnumTag itemType, int length) throws IOException {
		return parent().visitTagList(itemType, length);
	}

	default void visitTagByte(byte value) throws IOException {
		parent().visitTagByte(value);
	}

	default void visitTagShort(short value) throws IOException {
		parent().visitTagShort(value);
	}

	default void visitTagInt(int value) throws IOException {
		parent().visitTagInt(value);
	}

	default void visitTagLong(long value) throws IOException {
		parent().visitTagLong(value);
	}

	default void visitTagFloat(float value) throws IOException {
		parent().visitTagFloat(value);
	}

	default void visitTagDouble(double value) throws IOException {
		parent().visitTagDouble(value);
	}

	default void visitTagString(INBTValue<String> str) throws IOException {
		parent().visitTagString(str);
	}

	default void visitTagByteArray(INBTValue<byte[]> value) throws IOException {
		parent().visitTagByteArray(value);
	}

	default void visitTagIntArray(INBTValue<int[]> value) throws IOException {
		parent().visitTagIntArray(value);
	}

	default void visitTagLongArray(INBTValue<long[]> value) throws IOException {
		parent().visitTagLongArray(value);
	}

	default void visitTagEnd() throws IOException {
		parent().visitTagEnd();
	}

	static class NOPVisitorImpl implements INBTVisitor {
		private NOPVisitorImpl() {}
		@Override
		public INBTVisitor parent() {
			throw new IllegalStateException();
		}
		@Override
		public INBTVisitor visitRootTag(EnumTag tagType) throws IOException {
			return this;
		}
		@Override
		public INBTVisitor visitTag(EnumTag tagType, INBTValue<String> tagName) throws IOException {
			return this;
		}
		@Override
		public INBTVisitor visitTagList(EnumTag itemType, int length) throws IOException {
			return this;
		}
		@Override
		public void visitTagByte(byte value) throws IOException { }
		@Override
		public void visitTagShort(short value) throws IOException { }
		@Override
		public void visitTagInt(int value) throws IOException { }
		@Override
		public void visitTagLong(long value) throws IOException { }
		@Override
		public void visitTagFloat(float value) throws IOException { }
		@Override
		public void visitTagDouble(double value) throws IOException { }
		@Override
		public void visitTagString(INBTValue<String> str) throws IOException { }
		@Override
		public void visitTagByteArray(INBTValue<byte[]> value) throws IOException { }
		@Override
		public void visitTagIntArray(INBTValue<int[]> value) throws IOException { }
		@Override
		public void visitTagLongArray(INBTValue<long[]> value) throws IOException { }
		@Override
		public void visitTagEnd() throws IOException { }
	}

}
