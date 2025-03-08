package net.lax1dude.eaglercraft.backend.server.api;

import java.util.zip.DataFormatException;

import io.netty.buffer.ByteBuf;

public interface INativeZlib {

	boolean inflateEnabled();

	boolean deflateEnabled();

	void release();

	NettyUnsafe netty();

	public interface NettyUnsafe {

		void inflate(ByteBuf input, ByteBuf output) throws DataFormatException;

		void deflate(ByteBuf input, ByteBuf output) throws DataFormatException;

	}

}
