package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor.INBTValue;

class ValueByteArray implements INBTValue<byte[]> {

	private final DataInput dataSource;
	private byte[] resolved;
	private boolean done;

	ValueByteArray(DataInput dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void mutate(byte[] value) throws IOException {
		if(value == null) {
			throw new NullPointerException("Cannot mutate to a null value");
		}
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			int len = dataSource.readInt();
			if(len < 0) {
				throw new IOException("Invalid length!");
			}
			dataSource.skipBytes(len);
		}
		resolved = value;
	}

	@Override
	public void write(DataOutput dataOutput, byte[] tmp) throws IOException {
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved != null) {
			dataOutput.writeInt(resolved.length);
			dataOutput.write(resolved);
		}else {
			done = true;
			int len = dataSource.readInt();
			if(len < 0) {
				throw new IOException("Invalid length!");
			}
			dataOutput.writeInt(len);
			while(len > 0) {
				int j = Math.min(tmp.length, len);
				dataSource.readFully(tmp, 0, j);
				dataOutput.write(tmp, 0, j);
				len -=j;
			}
		}
	}

	@Override
	public byte[] value() throws IOException {
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			int len = dataSource.readInt();
			if(len < 0) {
				throw new IOException("Invalid length!");
			}
			resolved = new byte[len];
			dataSource.readFully(resolved);
		}
		return resolved;
	}

	void finish() throws IOException {
		if(!done) {
			done = true;
			if(resolved == null) {
				int len = dataSource.readInt();
				if(len < 0) {
					throw new IOException("Invalid length!");
				}
				dataSource.skipBytes(len);
			}
		}
	}

}
