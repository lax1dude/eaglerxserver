package net.md_5.bungee.jni.zlib;

import java.util.zip.DataFormatException;

import io.netty.buffer.ByteBuf;

public interface BungeeZlib {

	void init(boolean compress, int level);

	void free();

	void process(ByteBuf in, ByteBuf out) throws DataFormatException;

}
