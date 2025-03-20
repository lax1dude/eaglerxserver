package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor.INBTValue;

class ValueLongArray implements INBTValue<long[]> {

	private final DataInput dataSource;
	private long[] resolved;
	private boolean done;

	ValueLongArray(DataInput dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void mutate(long[] value) throws IOException {
		if(value == null) {
			throw new NullPointerException("Cannot mutate to a null value");
		}
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			int len = dataSource.readInt();
			if(len < 0 || len > (Integer.MAX_VALUE >> 3)) {
				throw new IOException("Invalid length!");
			}
			dataSource.skipBytes(len << 3);
		}
		resolved = value;
	}

	@Override
	public void write(DataOutput dataOutput, byte[] tmp) throws IOException {
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved != null) {
			int l = resolved.length;
			dataOutput.writeInt(l);
			writeLongs(dataOutput, resolved, l);
		}else {
			int len = dataSource.readInt();
			if(len < 0 || len > (Integer.MAX_VALUE >> 3)) {
				throw new IOException("Invalid length!");
			}
			dataOutput.writeInt(len);
			len <<= 3;
			while(len > 0) {
				int j = Math.min(tmp.length, len);
				dataSource.readFully(tmp, 0, j);
				dataOutput.write(tmp, 0, j);
				len -=j;
			}
		}
	}

	@Override
	public long[] value() throws IOException {
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			int len = dataSource.readInt();
			if(len < 0 || len > (Integer.MAX_VALUE >> 3)) {
				throw new IOException("Invalid length!");
			}
			resolved = new long[len];
			readLongs(dataSource, resolved, len);
		}
		return resolved;
	}

	void finish() throws IOException {
		if(resolved == null) {
			int len = dataSource.readInt();
			if(len < 0 || len > (Integer.MAX_VALUE >> 3)) {
				throw new IOException("Invalid length!");
			}
			dataSource.skipBytes(len << 3);
		}
		done = true;
	}

	private static void readLongs(DataInput input, long[] output, int len) throws IOException {
		for(int i = 0; i < len; ++i) {
			output[i] = input.readLong();
		}
	}

	private static void writeLongs(DataOutput output, long[] input, int len) throws IOException {
		for(int i = 0; i < len; ++i) {
			output.writeLong(input[i]);
		}
	}

}
