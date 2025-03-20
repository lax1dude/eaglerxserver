package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor.INBTValue;

class WrappedLongArray implements INBTValue<long[]> {

	private long[] value;

	WrappedLongArray(long[] value) {
		this.value = value;
	}

	@Override
	public void mutate(long[] value) throws IOException {
		this.value = value;
	}

	@Override
	public void write(DataOutput dataOutput, byte[] tmp) throws IOException {
		dataOutput.writeInt(value.length);
		ValueLongArray.writeLongs(dataOutput, value, value.length);
	}

	@Override
	public long[] value() throws IOException {
		return value;
	}

}
