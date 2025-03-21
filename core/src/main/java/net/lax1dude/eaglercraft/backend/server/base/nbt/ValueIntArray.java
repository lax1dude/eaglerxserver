package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;

class ValueIntArray implements INBTValue<int[]> {

	private DataInput dataSource;
	private int[] resolved;
	private boolean done;

	ValueIntArray(DataInput dataSource) {
		this.dataSource = dataSource;
	}

	void reset(DataInput dataSource) {
		this.dataSource = dataSource;
		this.resolved = null;
		this.done = false;
	}

	@Override
	public void mutate(int[] value) throws IOException {
		if(value == null) {
			throw new NullPointerException("Cannot mutate to a null value");
		}
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			int len = dataSource.readInt();
			if(len < 0 || len > (Integer.MAX_VALUE >> 2)) {
				throw new IOException("Invalid length!");
			}
			dataSource.skipBytes(len << 2);
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
			writeInts(dataOutput, resolved, l);
		}else {
			done = true;
			int len = dataSource.readInt();
			if(len < 0 || len > (Integer.MAX_VALUE >> 2)) {
				throw new IOException("Invalid length!");
			}
			dataOutput.writeInt(len);
			len <<= 2;
			while(len > 0) {
				int j = Math.min(tmp.length, len);
				dataSource.readFully(tmp, 0, j);
				dataOutput.write(tmp, 0, j);
				len -=j;
			}
		}
	}

	@Override
	public int[] value() throws IOException {
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			int len = dataSource.readInt();
			if(len < 0 || len > (Integer.MAX_VALUE >> 2)) {
				throw new IOException("Invalid length!");
			}
			resolved = new int[len];
			readInts(dataSource, resolved, len);
		}
		return resolved;
	}

	void finish() throws IOException {
		if(!done) {
			done = true;
			if(resolved == null) {
				int len = dataSource.readInt();
				if(len < 0 || len > (Integer.MAX_VALUE >> 2)) {
					throw new IOException("Invalid length!");
				}
				dataSource.skipBytes(len << 2);
			}
		}
	}

	static void readInts(DataInput input, int[] output, int len) throws IOException {
		for(int i = 0; i < len; ++i) {
			output[i] = input.readInt();
		}
	}

	static void writeInts(DataOutput output, int[] input, int len) throws IOException {
		for(int i = 0; i < len; ++i) {
			output.writeInt(input[i]);
		}
	}

}
