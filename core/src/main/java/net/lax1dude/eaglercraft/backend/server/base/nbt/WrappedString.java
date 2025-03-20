package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor.INBTValue;

class WrappedString implements INBTValue<String> {

	private String value;

	WrappedString(String value) {
		this.value = value;
	}

	@Override
	public void mutate(String value) throws IOException {
		this.value = value;
	}

	@Override
	public void write(DataOutput dataOutput, byte[] tmp) throws IOException {
		dataOutput.writeUTF(value);
	}

	@Override
	public String value() throws IOException {
		return value;
	}

}
