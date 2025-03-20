package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor.INBTValue;

class ValueString implements INBTValue<String> {

	private final DataInput dataSource;
	private String resolved;
	private boolean done;

	ValueString(DataInput dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void mutate(String value) throws IOException {
		if(value == null) {
			throw new NullPointerException("Cannot mutate to a null value");
		}
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			dataSource.skipBytes(dataSource.readUnsignedShort());
		}
		resolved = value;
	}

	@Override
	public void write(DataOutput dataOutput, byte[] tmp) throws IOException {
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved != null) {
			dataOutput.writeUTF(resolved);
		}else {
			done = true;
			int len = dataSource.readUnsignedShort();
			dataOutput.writeShort(len);
			while(len > 0) {
				int j = Math.min(tmp.length, len);
				dataSource.readFully(tmp, 0, j);
				dataOutput.write(tmp, 0, j);
				len -= j;
			}
		}
	}

	@Override
	public String value() throws IOException {
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			resolved = dataSource.readUTF();
		}
		return resolved;
	}

	void finish() throws IOException {
		if(!done) {
			done = true;
			if(resolved == null) {
				dataSource.skipBytes(dataSource.readUnsignedShort());
			}
		}
	}

}
