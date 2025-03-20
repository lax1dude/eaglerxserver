package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor;

public class NBTVisitorWriter implements INBTVisitor {

	private DataOutput dataOutput;
	private byte[] tmp;

	public NBTVisitorWriter(DataOutput dataOutput) {
		this.dataOutput = dataOutput;
	}

	public NBTVisitorWriter(DataOutput dataOutput, byte[] tmp) {
		this.dataOutput = dataOutput;
		this.tmp = tmp;
	}

	NBTVisitorWriter bind(DataOutput dataOutput) {
		this.dataOutput = dataOutput;
		return this;
	}

	@Override
	public INBTVisitor parent() {
		throw new IllegalStateException();
	}

	@Override
	public INBTVisitor visitRootTag(EnumTag tagType) throws IOException {
		dataOutput.writeByte(tagType.getId());
		dataOutput.writeShort(0);
		return this;
	}

	@Override
	public INBTVisitor visitTag(EnumTag tagType, INBTValue<String> tagName) throws IOException {
		dataOutput.writeByte(tagType.getId());
		handleWriteValue(tagName);
		return this;
	}

	@Override
	public INBTVisitor visitTagList(EnumTag itemType, int length) throws IOException {
		dataOutput.writeByte(itemType.getId());
		dataOutput.writeInt(length);
		return this;
	}

	@Override
	public void visitTagByte(byte value) throws IOException {
		dataOutput.writeByte(value);
	}

	@Override
	public void visitTagShort(short value) throws IOException {
		dataOutput.writeShort(value);
	}

	@Override
	public void visitTagInt(int value) throws IOException {
		dataOutput.writeInt(value);
	}

	@Override
	public void visitTagLong(long value) throws IOException {
		dataOutput.writeLong(value);
	}

	@Override
	public void visitTagFloat(float value) throws IOException {
		dataOutput.writeFloat(value);
	}

	@Override
	public void visitTagDouble(double value) throws IOException {
		dataOutput.writeDouble(value);
	}

	@Override
	public void visitTagString(INBTValue<String> str) throws IOException {
		handleWriteValue(str);
	}

	@Override
	public void visitTagByteArray(INBTValue<byte[]> value) throws IOException {
		handleWriteValue(value);
	}

	@Override
	public void visitTagIntArray(INBTValue<int[]> value) throws IOException {
		handleWriteValue(value);
	}

	@Override
	public void visitTagLongArray(INBTValue<long[]> value) throws IOException {
		handleWriteValue(value);
	}

	private void handleWriteValue(INBTValue<?> str) throws IOException {
		if(tmp == null) {
			tmp = new byte[256];
		}
		str.write(dataOutput, tmp);
	}

	@Override
	public void visitTagEnd() throws IOException {
		dataOutput.writeByte(0);
	}

}
