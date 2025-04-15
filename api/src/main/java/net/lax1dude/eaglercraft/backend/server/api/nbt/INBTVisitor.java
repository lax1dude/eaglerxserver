package net.lax1dude.eaglercraft.backend.server.api.nbt;

import java.io.IOException;

import javax.annotation.Nonnull;

public interface INBTVisitor {

	@Nonnull
	public static final INBTVisitor NOP = new NOPVisitor();

	@Nonnull
	INBTVisitor parent();

	@Nonnull
	default INBTVisitor visitRootTag(@Nonnull EnumDataType tagType) throws IOException {
		return parent().visitRootTag(tagType);
	}

	@Nonnull
	default INBTVisitor visitTag(@Nonnull EnumDataType tagType, @Nonnull INBTValue<String> tagName) throws IOException {
		return parent().visitTag(tagType, tagName);
	}

	@Nonnull
	default INBTVisitor visitTagList(@Nonnull EnumDataType itemType, int length) throws IOException {
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

	default void visitTagString(@Nonnull INBTValue<String> str) throws IOException {
		parent().visitTagString(str);
	}

	default void visitTagByteArray(@Nonnull INBTValue<byte[]> value) throws IOException {
		parent().visitTagByteArray(value);
	}

	default void visitTagIntArray(@Nonnull INBTValue<int[]> value) throws IOException {
		parent().visitTagIntArray(value);
	}

	default void visitTagLongArray(@Nonnull INBTValue<long[]> value) throws IOException {
		parent().visitTagLongArray(value);
	}

	default void visitTagEnd() throws IOException {
		parent().visitTagEnd();
	}

}
