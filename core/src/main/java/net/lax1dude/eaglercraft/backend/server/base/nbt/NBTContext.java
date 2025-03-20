package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor;

class NBTContext implements INBTContext {

	private final byte[] buf;
	private final NBTVisitorWriter writer;

	NBTContext(int bufferSize) {
		this.buf = new byte[bufferSize];
		this.writer = new NBTVisitorWriter(null, buf);
	}

	@Override
	public void accept(DataInput dataInput, INBTVisitor visitor) throws IOException {
		NBTVisitorReader.read(dataInput, visitor);
	}

	@Override
	public INBTVisitor createWriter(DataOutput dataOutput) {
		return writer.bind(dataOutput);
	}

}
