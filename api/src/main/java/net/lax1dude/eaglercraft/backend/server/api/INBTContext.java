package net.lax1dude.eaglercraft.backend.server.api;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor.INBTValue;

public interface INBTContext {

	void accept(DataInput dataInput, INBTVisitor visitor) throws IOException;

	INBTVisitor createWriter(DataOutput dataOutput);

	INBTValue<String> wrapValue(String value);

	INBTValue<byte[]> wrapValue(byte[] value);

	INBTValue<int[]> wrapValue(int[] value);

	INBTValue<long[]> wrapValue(long[] value);

}
