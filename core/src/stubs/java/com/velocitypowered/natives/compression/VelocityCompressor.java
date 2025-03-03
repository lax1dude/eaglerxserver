package com.velocitypowered.natives.compression;

import java.util.zip.DataFormatException;

import com.velocitypowered.natives.Disposable;

import io.netty.buffer.ByteBuf;

// Called directly for performance
public interface VelocityCompressor extends Disposable {

	void inflate(ByteBuf input, ByteBuf output, int uncompressedSize) throws DataFormatException;

	void deflate(ByteBuf input, ByteBuf output) throws DataFormatException;

}
