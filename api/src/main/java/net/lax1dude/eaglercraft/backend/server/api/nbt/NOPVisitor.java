package net.lax1dude.eaglercraft.backend.server.api.nbt;

import java.io.IOException;

final class NOPVisitor implements INBTVisitor {
	NOPVisitor() {}
	@Override
	public INBTVisitor parent() {
		throw new IllegalStateException();
	}
	@Override
	public INBTVisitor visitRootTag(EnumDataType tagType) throws IOException {
		return this;
	}
	@Override
	public INBTVisitor visitTag(EnumDataType tagType, INBTValue<String> tagName) throws IOException {
		return this;
	}
	@Override
	public INBTVisitor visitTagList(EnumDataType itemType, int length) throws IOException {
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