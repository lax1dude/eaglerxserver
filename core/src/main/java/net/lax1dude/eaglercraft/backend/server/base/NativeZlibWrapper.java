package net.lax1dude.eaglercraft.backend.server.base;

import java.util.zip.DataFormatException;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformZlib;
import net.lax1dude.eaglercraft.backend.server.api.INativeZlib;

public class NativeZlibWrapper implements INativeZlib, INativeZlib.NettyUnsafe {

	private final IPlatformZlib delegate;

	public NativeZlibWrapper(IPlatformZlib delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean inflateEnabled() {
		return delegate.inflateEnabled();
	}

	@Override
	public boolean deflateEnabled() {
		return delegate.deflateEnabled();
	}

	@Override
	public void release() {
		delegate.release();
	}

	@Override
	public NettyUnsafe getNettyUnsafe() {
		return this;
	}

	@Override
	public void inflate(ByteBuf input, ByteBuf output) throws DataFormatException {
		delegate.inflate(input, output);
	}

	@Override
	public void deflate(ByteBuf input, ByteBuf output) throws DataFormatException {
		delegate.deflate(input, output);
	}

}
