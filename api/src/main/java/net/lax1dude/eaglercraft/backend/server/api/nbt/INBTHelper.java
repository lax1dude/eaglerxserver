package net.lax1dude.eaglercraft.backend.server.api.nbt;

public interface INBTHelper extends INBTContext {

	INBTContext createThreadContext(int bufferSize);

}
