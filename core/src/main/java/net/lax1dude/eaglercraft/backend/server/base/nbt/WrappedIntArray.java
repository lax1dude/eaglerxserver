package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor.INBTValue;

class WrappedIntArray implements INBTValue<int[]> {

	private int[] value;

	WrappedIntArray(int[] value) {
		this.value = value;
	}

	@Override
	public void mutate(int[] value) throws IOException {
		this.value = value;
	}

	@Override
	public void write(DataOutput dataOutput, byte[] tmp) throws IOException {
		dataOutput.writeInt(value.length);
		ValueIntArray.writeInts(dataOutput, value, value.length);
	}

	@Override
	public int[] value() throws IOException {
		return value;
	}

}
