/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.lax1dude.eaglercraft.backend.server.base.EaglerListener;
import net.lax1dude.eaglercraft.backend.server.base.ISSLContextProvider;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;

public class MultiStackInitialInboundHandler extends ByteToMessageDecoder {

	private final PipelineTransformer transformer;
	private final NettyPipelineData pipelineData;
	private final List<ChannelHandler> componentsToRemove;
	private final String bungeeHack;

	public MultiStackInitialInboundHandler(PipelineTransformer transformer, NettyPipelineData pipelineData,
			List<ChannelHandler> componentsToRemove, String bungeeHack) {
		this.transformer = transformer;
		this.pipelineData = pipelineData;
		this.componentsToRemove = componentsToRemove;
		this.bungeeHack = bungeeHack;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (!ctx.channel().isActive()) {
			in.skipBytes(in.readableBytes());
		} else {
			EaglerListener listener = pipelineData.listenerInfo;
			if (listener.isDualStack() && isVanillaHandshake(in)) {
				setVanillaHandler(ctx, BufferUtils.readRetainedSlice(in, in.readableBytes()));
			} else {
				int state = isValidHTTPRequestLine(in);
				if (state == 1) {
					if (!listener.isTLSEnabled() || !listener.isTLSRequired()) {
						setHTTPHandler(ctx, null, BufferUtils.readRetainedSlice(in, in.readableBytes()));
					} else {
						ctx.close();
					}
				} else if (state == 2) {
					if (listener.isTLSEnabled()) {
						setHTTPHandler(ctx, listener.getSSLContext(),
								BufferUtils.readRetainedSlice(in, in.readableBytes()));
					} else {
						ctx.close();
					}
				} else if (state == 3) {
					if (listener.isDualStack()) {
						setVanillaHandler(ctx, BufferUtils.readRetainedSlice(in, in.readableBytes()));
					} else {
						ctx.close();
					}
				}
			}
		}
	}

	private boolean isVanillaHandshake(ByteBuf buffer) {
		if (buffer.readableBytes() > 5) {
			buffer.markReaderIndex();
			try {
				int frameLen = BufferUtils.readVarInt(buffer, 3);
				if (frameLen == 2 && buffer.readableBytes() >= 9) {
					// pre netty-rewrite handshake
					buffer.readUnsignedByte(); // skip protocol version
					int strLen = buffer.readUnsignedShort();
					if (strLen > 16) {
						throw new IndexOutOfBoundsException();
					}
					buffer.skipBytes(strLen * 2); // skip username string
					strLen = buffer.readUnsignedShort();
					if (strLen > 255) {
						throw new IndexOutOfBoundsException();
					}
					buffer.skipBytes(strLen * 2); // skip host string
					buffer.readInt(); // skip port
					return true;
				} else if (frameLen <= 267 && buffer.readableBytes() >= frameLen) {
					// post netty rewrite handshake
					int packetId = BufferUtils.readVarInt(buffer, 5);
					if (packetId == 0) {
						BufferUtils.readVarInt(buffer, 5); // validate protocol version
						int strLen = BufferUtils.readVarInt(buffer, 5); // validate host string length
						if (strLen > 255) {
							throw new IndexOutOfBoundsException();
						}
						buffer.skipBytes(strLen); // validate host string
						buffer.readUnsignedShort(); // validate port number
						BufferUtils.readVarInt(buffer, 5); // validate next state
						return true;
					}
				}
			} catch (IndexOutOfBoundsException ex) {
			} finally {
				buffer.resetReaderIndex();
			}
		}
		return false;
	}

	private int isValidHTTPRequestLine(ByteBuf buffer) {
		int len = buffer.readableBytes();
		if (len > 4) {
			buffer.markReaderIndex();
			try {
				char firstChar = (char) buffer.readUnsignedByte();
				switch (firstChar) {
				case 0x16:
					// TLS
					if (buffer.readUnsignedByte() == 0x03) {
						return 2;
					}
					break;
				case 'G':
					// "GET "
					if (len >= 4 && buffer.readUnsignedByte() == 'E' && buffer.readUnsignedByte() == 'T'
							&& buffer.readUnsignedByte() == ' ') {
						return isValidHTTPRequestLinePart2(buffer);
					}
					break;
				case 'H':
					// "HEAD "
					if (len >= 5 && buffer.readUnsignedByte() == 'E' && buffer.readUnsignedByte() == 'A'
							&& buffer.readUnsignedByte() == 'D' && buffer.readUnsignedByte() == ' ') {
						return isValidHTTPRequestLinePart2(buffer);
					}
					break;
				case 'O':
					// "OPTIONS "
					if (len >= 8 && buffer.readUnsignedByte() == 'P' && buffer.readUnsignedByte() == 'T'
							&& buffer.readUnsignedByte() == 'I' && buffer.readUnsignedByte() == 'O'
							&& buffer.readUnsignedByte() == 'N' && buffer.readUnsignedByte() == 'S'
							&& buffer.readUnsignedByte() == ' ') {
						return isValidHTTPRequestLinePart2(buffer);
					}
					break;
				case 'T':
					// "TRACE "
					if (len < 6) {
						return 0;
					} else if (buffer.readUnsignedByte() == 'R' && buffer.readUnsignedByte() == 'A'
							&& buffer.readUnsignedByte() == 'C' && buffer.readUnsignedByte() == 'E'
							&& buffer.readUnsignedByte() == ' ') {
						return isValidHTTPRequestLinePart2(buffer);
					}
					break;
				case 'P':
					if (len >= 4) {
						short b2 = buffer.readUnsignedByte();
						short b3 = buffer.readUnsignedByte();
						short b4 = buffer.readUnsignedByte();

						// "PUT "
						if (b2 == 'U' && b3 == 'T' && b4 == ' ') {
							return isValidHTTPRequestLinePart2(buffer);
						}

						if (len >= 5) {
							short b5 = buffer.readUnsignedByte();

							// "POST "
							if (b2 == 'O' && b3 == 'S' && b4 == 'T' && b5 == ' ') {
								return isValidHTTPRequestLinePart2(buffer);
							}

							if (len < 6) {
								return 0;
							} else {
								// "PATCH "
								if (b2 == 'A' && b3 == 'T' && b4 == 'C' && b5 == 'H'
										&& buffer.readUnsignedByte() == ' ') {
									return isValidHTTPRequestLinePart2(buffer);
								}
							}
						}
					}
					break;
				case 'D':
					// "DELETE "
					if (len >= 8 && buffer.readUnsignedByte() == 'P' && buffer.readUnsignedByte() == 'T'
							&& buffer.readUnsignedByte() == 'I' && buffer.readUnsignedByte() == 'O'
							&& buffer.readUnsignedByte() == 'N' && buffer.readUnsignedByte() == 'S'
							&& buffer.readUnsignedByte() == ' ') {
						return isValidHTTPRequestLinePart2(buffer);
					}
					break;
				}
				// Returns 3 if there's not a chance this is HTTP
				return 3;
			} catch (IndexOutOfBoundsException ex) {
			} finally {
				buffer.resetReaderIndex();
			}
		}
		return 0;
	}

	private int isValidHTTPRequestLinePart2(ByteBuf buffer) {
		int maxLineLen = pipelineData.server.getConfig().getSettings().getHTTPMaxInitialLineLength();

		char[] requestLineEnd = new char[9];

		int i = 0;
		for (;;) {
			if (i > maxLineLen) {
				return 3;
			}
			if (!buffer.isReadable()) {
				return 0;
			}
			char c = (char) buffer.readUnsignedByte();
			if (c == '\r' || c == '\n') {
				break;
			}
			requestLineEnd[i++ % 9] = c;
		}

		if (i < 9) {
			return 3;
		}

		// Make sure the request line ended with " HTTP/1.0" or " HTTP/1.1"
		if (requestLineEnd[(i - 8) % 9] == 'H' && requestLineEnd[(i - 7) % 9] == 'T'
				&& requestLineEnd[(i - 6) % 9] == 'T' && requestLineEnd[(i - 5) % 9] == 'P'
				&& requestLineEnd[(i - 4) % 9] == '/' && requestLineEnd[(i - 3) % 9] == '1'
				&& requestLineEnd[(i - 2) % 9] == '.'
				&& (requestLineEnd[(i - 1) % 9] == '1' || requestLineEnd[(i - 1) % 9] == '0')) {
			return 1;
		}

		return 3;
	}

	private void setVanillaHandler(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {
			pipelineData.listenerInfo = null;
			ChannelPipeline p = ctx.pipeline();
			p.remove(PipelineTransformer.HANDLER_OUTBOUND_THROW);
			ctx.fireChannelRead(buffer.retain());
			p.remove(PipelineTransformer.HANDLER_MULTI_STACK_INITIAL);
		} finally {
			buffer.release();
		}
	}

	private void setHTTPHandler(ChannelHandlerContext ctx, ISSLContextProvider ssl, ByteBuf buffer) {
		try {
			ChannelPipeline p = ctx.pipeline();
			if (componentsToRemove != null) {
				for (ChannelHandler handler : componentsToRemove) {
					p.remove(handler);
				}
			}
			if (bungeeHack != null) {
				p.addLast(bungeeHack, NOPDummyHandler.INSTANCE);
			}
			transformer.initializeHTTPHandler(pipelineData, ssl, p, PipelineTransformer.HANDLER_MULTI_STACK_INITIAL);
			if (ctx.channel().isActive()) {
				ctx.fireChannelRead(buffer.retain());
			}
			p.remove(PipelineTransformer.HANDLER_MULTI_STACK_INITIAL);
		} finally {
			buffer.release();
		}
	}

}
