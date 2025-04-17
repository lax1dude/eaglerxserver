package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTVisitor;

class NBTContext implements INBTContext, IWrapperFactory {

	private final byte[] buf;
	private final NBTVisitorWriter writer;
	private final ValueString stringValue;
	private final ValueByteArray byteValue;
	private final ValueIntArray intValue;
	private final ValueLongArray longValue;

	NBTContext(int bufferSize) {
		this.buf = new byte[bufferSize];
		this.writer = new NBTVisitorWriter(null, buf);
		this.stringValue = new ValueString(null);
		this.byteValue = new ValueByteArray(null);
		this.intValue = new ValueIntArray(null);
		this.longValue = new ValueLongArray(null);
	}

	@Override
	public void accept(DataInput dataInput, INBTVisitor visitor) throws IOException {
		if(dataInput == null) {
			throw new NullPointerException("dataInput");
		}
		if(visitor == null) {
			throw new NullPointerException("visitor");
		}
		NBTVisitorReader.read(dataInput, visitor, this);
	}

	@Override
	public INBTVisitor createWriter(DataOutput dataOutput) {
		if(dataOutput == null) {
			throw new NullPointerException("dataOutput");
		}
		return writer.bind(dataOutput);
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
		stringValue.reset(dataSource);
		return stringValue;
	}

	@Override
	public ValueByteArray wrapByteData(DataInput dataSource) {
		byteValue.reset(dataSource);
		return byteValue;
	}

	@Override
	public ValueIntArray wrapIntData(DataInput dataSource) {
		intValue.reset(dataSource);
		return intValue;
	}

	@Override
	public ValueLongArray wrapLongData(DataInput dataSource) {
		longValue.reset(dataSource);
		return longValue;
	}

}
