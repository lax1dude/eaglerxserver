package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTHelper;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTVisitor;

public class NBTHelper implements INBTHelper, IWrapperFactory {

	public static final INBTHelper INSTANCE = new NBTHelper();

	private NBTHelper() {
	}

	@Override
	public void accept(DataInput dataInput, INBTVisitor visitor) throws IOException {
		NBTVisitorReader.read(dataInput, visitor, this);
	}

	@Override
	public INBTVisitor createWriter(DataOutput dataOutput) {
		return new NBTVisitorWriter(dataOutput);
	}

	@Override
	public INBTContext createThreadContext(int bufferSize) {
		return new NBTContext(bufferSize);
	}

	@Override
	public INBTValue<String> wrapValue(String value) {
		if(value == null) {
			throw new NullPointerException("value");
		}
		return new WrappedString(value);
	}

	@Override
	public INBTValue<byte[]> wrapValue(byte[] value) {
		if(value == null) {
			throw new NullPointerException("value");
		}
		return new WrappedByteArray(value);
	}

	@Override
	public INBTValue<int[]> wrapValue(int[] value) {
		if(value == null) {
			throw new NullPointerException("value");
		}
		return new WrappedIntArray(value);
	}

	@Override
	public INBTValue<long[]> wrapValue(long[] value) {
		if(value == null) {
			throw new NullPointerException("value");
		}
		return new WrappedLongArray(value);
	}

	@Override
	public ValueString wrapStringData(DataInput dataSource) {
		return new ValueString(dataSource);
	}

	@Override
	public ValueByteArray wrapByteData(DataInput dataSource) {
		return new ValueByteArray(dataSource);
	}

	@Override
	public ValueIntArray wrapIntData(DataInput dataSource) {
		return new ValueIntArray(dataSource);
	}

	@Override
	public ValueLongArray wrapLongData(DataInput dataSource) {
		return new ValueLongArray(dataSource);
	}

}
