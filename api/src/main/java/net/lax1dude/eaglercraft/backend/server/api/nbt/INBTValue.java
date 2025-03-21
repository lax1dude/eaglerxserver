package net.lax1dude.eaglercraft.backend.server.api.nbt;

import java.io.DataOutput;
import java.io.IOException;

public interface INBTValue<T> {

	void mutate(T value) throws IOException;

	void write(DataOutput dataOutput, byte[] tmp) throws IOException;

	T value() throws IOException;

}