package net.lax1dude.eaglercraft.backend.server.api.nbt;

import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnull;

public interface INBTValue<T> {

	void mutate(@Nonnull T value) throws IOException;

	void write(@Nonnull DataOutput dataOutput, @Nonnull byte[] tmp) throws IOException;

	@Nonnull
	T value() throws IOException;

}