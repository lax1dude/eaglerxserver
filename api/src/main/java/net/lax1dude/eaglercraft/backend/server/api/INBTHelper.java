package net.lax1dude.eaglercraft.backend.server.api;

public interface INBTHelper extends INBTContext {

	INBTContext createThreadContext(int bufferSize);

}
