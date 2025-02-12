package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformConnectionInitializer<A> {

	void addFrameDecoder(String name, Object newDecoder);

	void setConnectionAttachment(A attachment);

}
