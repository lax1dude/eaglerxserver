package net.lax1dude.eaglerxserver.adapter;

public interface IPlatformConnectionInitializer<A> {

	void addFrameDecoder(String name, Object newDecoder);

	void setConnectionAttachment(A attachment);

}
