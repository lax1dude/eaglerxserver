package net.lax1dude.eaglercraft.backend.server.api.nbt;

import javax.annotation.Nonnull;

public interface INBTHelper extends INBTContext {

	@Nonnull
	INBTContext createThreadContext(int bufferSize);

}
