package net.lax1dude.eaglercraft.backend.server.util;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformZlib;

public class FallbackJava11Zlib implements IPlatformZlib {

	private Deflater deflater;
	private Inflater inflater;

	private static final int ZLIB_BUFFER_SIZE = 8192;

	public FallbackJava11Zlib(Deflater deflater, Inflater inflater) {
		this.deflater = deflater;
		this.inflater = inflater;
	}

	public static FallbackJava11Zlib create(boolean compression, boolean decompression, int compressionLevel) {
		return new FallbackJava11Zlib(compression ? new Deflater(compressionLevel) : null, decompression ? new Inflater() : null);
	}

	@Override
	public boolean inflateEnabled() {
		return inflater != null;
	}

	@Override
	public void inflate(ByteBuf input, ByteBuf output) throws DataFormatException {
		final int origIdx = input.readerIndex();
		inflater.setInput(input.nioBuffer());

		try {
			final int readable = input.readableBytes();
			while (!inflater.finished() && inflater.getBytesRead() < readable) {
				if (!output.isWritable()) {
					output.ensureWritable(ZLIB_BUFFER_SIZE);
				}

				ByteBuffer destNioBuf = output.nioBuffer(output.writerIndex(), output.writableBytes());
				int produced = inflater.inflate(destNioBuf);
				output.writerIndex(output.writerIndex() + produced);
			}

			if (!inflater.finished()) {
				throw new DataFormatException("Received a deflate stream that was too large");
			}
			input.readerIndex(origIdx + inflater.getTotalIn());
		} finally {
			inflater.reset();
		}
	}

	@Override
	public boolean deflateEnabled() {
		return deflater != null;
	}

	@Override
	public void deflate(ByteBuf input, ByteBuf output) throws DataFormatException {
		final int origIdx = input.readerIndex();
		deflater.setInput(input.nioBuffer());
		deflater.finish();

		while (!deflater.finished()) {
			if (!output.isWritable()) {
				output.ensureWritable(ZLIB_BUFFER_SIZE);
			}

			ByteBuffer destNioBuf = output.nioBuffer(output.writerIndex(), output.writableBytes());
			int produced = deflater.deflate(destNioBuf);
			output.writerIndex(output.writerIndex() + produced);
		}

		input.readerIndex(origIdx + deflater.getTotalIn());
		deflater.reset();
	}

	@Override
	public void release() {
		try {
			if(inflater != null) {
				inflater.end();
				inflater = null;
			}
		}finally {
			if(deflater != null) {
				deflater.end();
				deflater = null;
			}
		}
	}

}
