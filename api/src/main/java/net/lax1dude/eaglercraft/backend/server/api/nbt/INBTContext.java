package net.lax1dude.eaglercraft.backend.server.api.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnull;

public interface INBTContext {

	void accept(@Nonnull DataInput dataInput, @Nonnull INBTVisitor visitor) throws IOException;

	@Nonnull
	INBTVisitor createWriter(@Nonnull DataOutput dataOutput);

	@Nonnull
	INBTValue<String> wrapValue(@Nonnull String value);

	@Nonnull
	INBTValue<byte[]> wrapValue(@Nonnull byte[] value);

	@Nonnull
	INBTValue<int[]> wrapValue(@Nonnull int[] value);

	@Nonnull
	INBTValue<long[]> wrapValue(@Nonnull long[] value);

}
