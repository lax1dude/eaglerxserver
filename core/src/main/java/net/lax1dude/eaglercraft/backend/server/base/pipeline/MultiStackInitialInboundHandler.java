package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageCodec;
import net.lax1dude.eaglercraft.backend.server.base.EaglerListener;
import net.lax1dude.eaglercraft.backend.server.base.ISSLContextProvider;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;

public class MultiStackInitialInboundHandler extends ByteToMessageCodec<ByteBuf> {

	private final PipelineTransformer transformer;
	private final NettyPipelineData pipelineData;
	private final List<ChannelHandler> componentsToRemove;
	private List<ByteBuf> waitingOutboundFrames;

	public MultiStackInitialInboundHandler(PipelineTransformer transformer, NettyPipelineData pipelineData, List<ChannelHandler> componentsToRemove) {
		this.transformer = transformer;
		this.pipelineData = pipelineData;
		this.componentsToRemove = componentsToRemove;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (!ctx.channel().isActive()) {
			in.skipBytes(in.readableBytes());
		}else {
			EaglerListener listener = pipelineData.listenerInfo;
			if(listener.isDualStack() && isVanillaHandshake(in)) {
				setVanillaHandler(ctx, in.readRetainedSlice(in.readableBytes()));
			}else {
				int state = isValidHTTPRequestLine(in);
				if(state == 1) {
					if(!listener.isTLSEnabled() || !listener.isTLSRequired()) {
						setHTTPHandler(ctx, null, in.readRetainedSlice(in.readableBytes()));
					}else {
						ctx.close();
					}
				}else if(state == 2) {
					if(listener.isTLSEnabled()) {
						setHTTPHandler(ctx, listener.getSSLContext(), in.readRetainedSlice(in.readableBytes()));
					}else {
						ctx.close();
					}
				}else if(state == 3) {
					if(listener.isDualStack()) {
						setVanillaHandler(ctx, in.readRetainedSlice(in.readableBytes()));
					}else {
						ctx.close();
					}
				}
			}
		}
	}

	private boolean isVanillaHandshake(ByteBuf buffer) {
		if(buffer.readableBytes() > 5) {
			buffer.markReaderIndex();
			try {
				int frameLen = BufferUtils.readVarInt(buffer, 3);
				if(frameLen == 2 && buffer.readableBytes() >= 9) {
					// pre netty-rewrite handshake
					buffer.readUnsignedByte(); // skip protocol version
					int strLen = buffer.readUnsignedShort();
					if(strLen > 16) {
						throw new IndexOutOfBoundsException();
					}
					buffer.skipBytes(strLen * 2); // skip username string
					strLen = buffer.readUnsignedShort();
					if(strLen > 255) {
						throw new IndexOutOfBoundsException();
					}
					buffer.skipBytes(strLen * 2); // skip host string
					buffer.readInt(); // skip port
					return true;
				}else if(frameLen <= 267 && buffer.readableBytes() >= frameLen) {
					// post netty rewrite handshake
					int packetId = BufferUtils.readVarInt(buffer, 5);
					if(packetId == 0) {
						BufferUtils.readVarInt(buffer, 5); // validate protocol version
						int strLen = BufferUtils.readVarInt(buffer, 5); // validate host string length
						if(strLen > 255) {
							throw new IndexOutOfBoundsException();
						}
						buffer.skipBytes(strLen); // validate host string
						buffer.readUnsignedShort(); // validate port number
						BufferUtils.readVarInt(buffer, 5); // validate next state
						return true;
					}
				}
			}catch(IndexOutOfBoundsException ex) {
			}finally {
				buffer.resetReaderIndex();
			}
		}
		return false;
	}

	private int isValidHTTPRequestLine(ByteBuf buffer) {
		int len = buffer.readableBytes();
		if(len > 4) {
			buffer.markReaderIndex();
			try {
				char firstChar = (char) buffer.readUnsignedByte();
				switch(firstChar) {
				case 0x16:
					// TLS
					if(buffer.readUnsignedByte() == 0x03) {
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
						if(b2 == 'U' && b3 == 'T' && b4 == ' ') {
							return isValidHTTPRequestLinePart2(buffer);
						}
						
						if (len >= 5) {
							short b5 = buffer.readUnsignedByte();
							
							// "POST "
							if(b2 == 'O' && b3 == 'S' && b4 == 'T' && b5 == ' ') {
								return isValidHTTPRequestLinePart2(buffer);
							}
							
							if (len < 6) {
								return 0;
							}else {
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
			}catch(IndexOutOfBoundsException ex) {
			}finally {
				buffer.resetReaderIndex();
			}
		}
		return 0;
	}

	private int isValidHTTPRequestLinePart2(ByteBuf buffer) {
		
		// this is a ring buffer
		char[] requestLineEnd = new char[9];
		
		int i = 0;
		for(;;) {
			if(!buffer.isReadable()) {
				return 0;
			}
			char c = (char) buffer.readUnsignedByte();
			if(c == '\n') {
				break;
			}
			requestLineEnd[i++ % 9] = c;
		}
		
		if(i < 9) {
			return 3;
		}
		
		// Make sure the request line ended with " HTTP/1.0" or " HTTP/1.1"
		if(requestLineEnd[(i - 8) % 9] == 'H' &&
			requestLineEnd[(i - 7) % 9] == 'T' &&
			requestLineEnd[(i - 6) % 9] == 'T' &&
			requestLineEnd[(i - 5) % 9] == 'P' &&
			requestLineEnd[(i - 4) % 9] == '/' &&
			requestLineEnd[(i - 3) % 9] == '1' &&
			requestLineEnd[(i - 2) % 9] == '.' &&
			(requestLineEnd[(i - 1) % 9] == '1' ||
			requestLineEnd[(i - 1) % 9] == '0')) {
			return 1;
		}
		
		return 3;
	}

	private void setVanillaHandler(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {
			pipelineData.listenerInfo = null;
			if(waitingOutboundFrames != null) {
				for(ByteBuf buf : waitingOutboundFrames) {
					ctx.write(buf, ctx.voidPromise());
				}
				ctx.flush();
				waitingOutboundFrames = null;
			}
			ChannelPipeline p = ctx.pipeline();
			p.remove(PipelineTransformer.HANDLER_MULTI_STACK_INITIAL);
			p.fireChannelRead(buffer.retain());
		}finally {
			buffer.release();
		}
	}

	private void setHTTPHandler(ChannelHandlerContext ctx, ISSLContextProvider ssl, ByteBuf buffer) {
		try {
			ChannelPipeline p = ctx.pipeline();
			if(componentsToRemove != null) {
				for(ChannelHandler handler : componentsToRemove) {
					p.remove(handler);
				}
			}
			List<ByteBuf> waiting2 = null;
			if(waitingOutboundFrames != null) {
				waiting2 = sliceVarIntFrames(waitingOutboundFrames);
			}
			try {
				transformer.initializeHTTPHandler(pipelineData, ssl, p, PipelineTransformer.HANDLER_MULTI_STACK_INITIAL, waiting2);
			}finally {
				if(waiting2 != null) {
					for(ByteBuf buf : waiting2) {
						buf.release();
					}
				}
			}
			p.remove(PipelineTransformer.HANDLER_MULTI_STACK_INITIAL);
			if (ctx.channel().isActive()) {
				p.fireChannelRead(buffer.retain());
			}
		}finally {
			buffer.release();
		}
	}

	@Override
	protected void encode(ChannelHandlerContext arg0, ByteBuf arg1, ByteBuf arg2) throws Exception {
		if (arg0.channel().isActive()) {
			if(waitingOutboundFrames == null) {
				waitingOutboundFrames = new ArrayList<>(4);
			}
			waitingOutboundFrames.add(arg1.retain());
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		try {
			super.channelInactive(ctx);
		}finally {
			release();
		}
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		try {
			super.handlerRemoved(ctx);
		}finally {
			release();
		}
	}

	private List<ByteBuf> sliceVarIntFrames(List<ByteBuf> buffers) {
		List<ByteBuf> framesRet = new ArrayList<>(buffers.size());
		for(ByteBuf buf : buffers) {
			buf.markReaderIndex();
			int maxLen = buf.readableBytes();
			try {
				int len = BufferUtils.readVarInt(buf, 3);
				framesRet.add(buf.readRetainedSlice(len));
			}catch(IndexOutOfBoundsException ex) {
				transformer.server.logger().warn("Dropping " + maxLen + " byte outbound frame with an invalid length");
			}finally {
				buf.resetReaderIndex();
			}
		}
		return framesRet;
	}

	private void release() {
		if (waitingOutboundFrames != null) {
			for (ByteBuf b : waitingOutboundFrames) {
				b.release();
			}
			waitingOutboundFrames = null;
		}
	}

}
