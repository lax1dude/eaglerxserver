package net.lax1dude.eaglercraft.backend.server.adapter;

import java.util.zip.DataFormatException;

import io.netty.buffer.ByteBuf;

public interface IPlatformZlib {

	boolean inflateEnabled();

	void inflate(ByteBuf input, ByteBuf output) throws DataFormatException;

	boolean deflateEnabled();

	void deflate(ByteBuf input, ByteBuf output) throws DataFormatException;

	void release();

}
