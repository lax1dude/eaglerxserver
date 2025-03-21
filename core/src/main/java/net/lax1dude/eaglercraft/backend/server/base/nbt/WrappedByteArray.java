package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;

class WrappedByteArray implements INBTValue<byte[]> {

	private byte[] value;

	WrappedByteArray(byte[] value) {
		this.value = value;
	}

	@Override
	public void mutate(byte[] value) {
		this.value = value;
	}

	@Override
	public void write(DataOutput dataOutput, byte[] tmp) throws IOException {
		dataOutput.writeInt(value.length);
		dataOutput.write(value);
	}

	@Override
	public byte[] value() throws IOException {
		return value;
	}

}
