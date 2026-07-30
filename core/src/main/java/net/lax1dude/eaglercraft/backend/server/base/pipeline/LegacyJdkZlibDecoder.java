/*
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import static io.netty.util.internal.ObjectUtil.checkPositiveOrZero;

import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;

/**
 * Mostly copied from Netty, only used on old Spigots
 */
public class LegacyJdkZlibDecoder extends ZlibDecoder {

	/**
	 * Maximum allowed size of the decompression buffer.
	 */
	protected final int maxAllocation;

	/**
	 * Allocate or expand the decompression buffer, without exceeding the maximum
	 * allocation. Calls {@link #decompressionBufferExhausted(ByteBuf)} if the
	 * buffer is full and cannot be expanded further.
	 */
	protected ByteBuf prepareDecompressBuffer(ChannelHandlerContext ctx, ByteBuf buffer, int preferredSize) {
		if (buffer == null) {
			if (maxAllocation == 0) {
				return ctx.alloc().heapBuffer(preferredSize);
			}

			return ctx.alloc().heapBuffer(Math.min(preferredSize, maxAllocation), maxAllocation);
		}

		// this always expands the buffer if possible, even if the expansion is less
		// than preferredSize
		// we throw the exception only if the buffer could not be expanded at all
		// this means that one final attempt to deserialize will always be made with the
		// buffer at maxAllocation
		if (buffer.ensureWritable(preferredSize, true) == 1) {
			// buffer must be consumed so subclasses don't add it to output
			// we therefore duplicate it when calling decompressionBufferExhausted() to
			// guarantee non-interference
			// but wait until after to consume it so the subclass can tell how much output
			// is really in the buffer
			decompressionBufferExhausted(buffer.duplicate());
			buffer.skipBytes(buffer.readableBytes());
			throw new DecompressionException("Decompression buffer has reached maximum size: " + buffer.maxCapacity());
		}

		return buffer;
	}

	private Inflater inflater;
	private final byte[] dictionary;

	private enum GzipState {
		HEADER_START, HEADER_END, FLG_READ, XLEN_READ, SKIP_FNAME, SKIP_COMMENT, PROCESS_FHCRC, FOOTER_START,
	}

	private boolean needsRead;

	private static final int DEFAULT_MAX_FORWARD_BYTES = SystemPropertyUtil
			.getInt("io.netty.compression.defaultMaxForwardBytes", 64 * 1024);
	private final int maxForwardBytes;

	private volatile boolean finished;

	private boolean decideZlibOrNone;

	/**
	 * Creates a new instance with the specified preset dictionary and maximum
	 * buffer allocation. The wrapper is always {@link ZlibWrapper#ZLIB} because it
	 * is the only format that supports the preset dictionary.
	 *
	 * @param maxAllocation Maximum size of the decompression buffer. Must be &gt;=
	 *                      0. If zero, maximum size is decided by the
	 *                      {@link ByteBufAllocator}.
	 */
	public LegacyJdkZlibDecoder(byte[] dictionary, int maxAllocation) {
		this(ZlibWrapper.ZLIB, dictionary, maxAllocation);
	}

	/**
	 * Creates a new instance with the specified wrapper and maximum buffer
	 * allocation. Be aware that only {@link ZlibWrapper#GZIP},
	 * {@link ZlibWrapper#ZLIB} and {@link ZlibWrapper#NONE} are supported atm.
	 *
	 * @param maxAllocation Maximum size of the decompression buffer. Must be &gt;=
	 *                      0. If zero, maximum size is decided by the
	 *                      {@link ByteBufAllocator}.
	 */
	public LegacyJdkZlibDecoder(ZlibWrapper wrapper, int maxAllocation) {
		this(wrapper, null, maxAllocation);
	}

	private LegacyJdkZlibDecoder(ZlibWrapper wrapper, byte[] dictionary, int maxAllocation) {
		this.maxAllocation = checkPositiveOrZero(maxAllocation, "maxAllocation");
		this.maxForwardBytes = maxAllocation > 0 ? maxAllocation : DEFAULT_MAX_FORWARD_BYTES;

		ObjectUtil.checkNotNull(wrapper, "wrapper");

		switch (wrapper) {
		case GZIP:
			throw new IllegalStateException();
		case NONE:
			inflater = new Inflater(true);
			break;
		case ZLIB:
			inflater = new Inflater();
			break;
		case ZLIB_OR_NONE:
			// Postpone the decision until decode(...) is called.
			decideZlibOrNone = true;
			break;
		default:
			throw new IllegalArgumentException("Only GZIP or ZLIB is supported, but you used " + wrapper);
		}
		this.dictionary = dictionary;
	}

	@Override
	public boolean isClosed() {
		return finished;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		needsRead = true;
		if (finished) {
			// Skip data received after finished.
			in.skipBytes(in.readableBytes());
			return;
		}

		int readableBytes = in.readableBytes();
		if (readableBytes == 0) {
			return;
		}

		if (decideZlibOrNone) {
			// First two bytes are needed to decide if it's a ZLIB stream.
			if (readableBytes < 2) {
				return;
			}

			boolean nowrap = !looksLikeZlib(in.getShort(in.readerIndex()));
			inflater = new Inflater(nowrap);
			decideZlibOrNone = false;
		}

		if (inflater.needsInput()) {
			if (in.hasArray()) {
				inflater.setInput(in.array(), in.arrayOffset() + in.readerIndex(), readableBytes);
			} else {
				byte[] array = new byte[readableBytes];
				in.getBytes(in.readerIndex(), array);
				inflater.setInput(array);
			}
		}

		ByteBuf decompressed = prepareDecompressBuffer(ctx, null, inflater.getRemaining() << 1);
		try {
			while (!inflater.needsInput()) {
				byte[] outArray = decompressed.array();
				int writerIndex = decompressed.writerIndex();
				int outIndex = decompressed.arrayOffset() + writerIndex;
				int writable = decompressed.writableBytes();
				int outputLength = inflater.inflate(outArray, outIndex, writable);
				if (outputLength > 0) {
					decompressed.writerIndex(writerIndex + outputLength);
					if (maxAllocation == 0 && decompressed.readableBytes() >= maxForwardBytes) {
						// Forward the buffer once it exceeds the threshold to bound memory
						// while avoiding excessive fireChannelRead calls.
						ByteBuf buffer = decompressed;
						decompressed = null;
						needsRead = false;
						ctx.fireChannelRead(buffer);
					}
				} else if (inflater.needsDictionary()) {
					if (dictionary == null) {
						throw new DecompressionException(
								"decompression failure, unable to set dictionary as non was specified");
					}
					inflater.setDictionary(dictionary);
				}

				if (inflater.finished()) {
					finished = true; // Do not decode anymore.
					break;
				} else {
					decompressed = prepareDecompressBuffer(ctx, decompressed, inflater.getRemaining() << 1);
				}
			}

			in.skipBytes(readableBytes - inflater.getRemaining());
		} catch (DataFormatException e) {
			throw new DecompressionException("decompression failure", e);
		} finally {
			if (decompressed != null) {
				if (decompressed.isReadable()) {
					needsRead = false;
					ctx.fireChannelRead(decompressed);
				} else {
					decompressed.release();
				}
			}
		}
	}

	@Override
	protected void decompressionBufferExhausted(ByteBuf buffer) {
		finished = true;
	}

	@Override
	protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved0(ctx);
		if (inflater != null) {
			inflater.end();
		}
	}

	/*
	 * Returns true if the cmf_flg parameter (think: first two bytes of a zlib
	 * stream) indicates that this is a zlib stream. <p> You can lookup the details
	 * in the ZLIB RFC: <a
	 * href="https://tools.ietf.org/html/rfc1950#section-2.2">RFC 1950</a>.
	 */
	private static boolean looksLikeZlib(short cmf_flg) {
		return (cmf_flg & 0x7800) == 0x7800 && cmf_flg % 31 == 0;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// Discard bytes of the cumulation buffer if needed.
		discardSomeReadBytes();

		if (needsRead && !ctx.channel().config().isAutoRead()) {
			ctx.read();
		}
		ctx.fireChannelReadComplete();
	}
}
