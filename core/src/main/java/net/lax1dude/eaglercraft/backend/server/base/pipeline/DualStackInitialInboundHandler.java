package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

public class DualStackInitialInboundHandler extends ByteToMessageCodec<ByteBuf> {

	private final List<ByteBuf> waitingOutboundFrames = new LinkedList<>();

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (!ctx.channel().isActive()) {
			in.skipBytes(in.readableBytes());
		}else if(isVanillaHandshake(in)) {
			setVanillaHandler(in.readRetainedSlice(in.readableBytes()));
		}else {
			int triState = isValidHTTPRequestLine(in);
			if(triState == 1) {
				setHTTPHandler(in.readRetainedSlice(in.readableBytes()));
			}else if(triState == 2) {
				setVanillaHandler(in.readRetainedSlice(in.readableBytes()));
			}
		}
	}

	private boolean isVanillaHandshake(ByteBuf buffer) {
		if(buffer.readableBytes() > 5) {
			buffer.markReaderIndex();
			try {
				int frameLen = BufferUtils.readVarInt(buffer, 3);
				if(frameLen <= 267 && buffer.readableBytes() >= frameLen) {
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
				// Returns 2 if there's not a chance this is HTTP
				return 2;
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
			return 2;
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
		
		return 2;
	}

	private void setVanillaHandler(ByteBuf buffer) {
		
	}

	private void setHTTPHandler(ByteBuf buffer) {
		
	}

	@Override
	protected void encode(ChannelHandlerContext arg0, ByteBuf arg1, ByteBuf arg2) throws Exception {
		waitingOutboundFrames.add(arg1);
	}

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    	super.handlerRemoved(ctx);
    	for(ByteBuf b : waitingOutboundFrames) {
    		b.release();
    	}
    	waitingOutboundFrames.clear();
    }

}
