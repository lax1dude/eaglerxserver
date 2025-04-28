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

package net.lax1dude.eaglercraft.backend.server.base.message;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.api.EnumPipelineEvent;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.BufferUtils;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.EaglerInjectedMessageHandler;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.PipelineTransformer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageConstants;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class InjectedMessageController extends MessageController {

	protected final Channel channel;
	protected final ByteBufInputWrapper inputWrapper;
	protected final ByteBufOutputWrapper outputWrapper;
	protected final int[] marks;

	public InjectedMessageController(GamePluginMessageProtocol protocol, ServerMessageHandler handler, Channel channel,
			int defragSendDelay) {
		super(protocol, handler, channel.eventLoop(), defragSendDelay);
		this.channel = channel;
		this.inputWrapper = new ByteBufInputWrapper();
		this.outputWrapper = new ByteBufOutputWrapper();
		this.marks = new int[32];
	}

	public static InjectedMessageController injectEagler(GamePluginMessageProtocol protocol,
			ServerMessageHandler handler, Channel channel, int defragSendDelay) {
		InjectedMessageController controller = new InjectedMessageController(protocol, handler, channel,
				defragSendDelay);
		channel.pipeline().addAfter(PipelineTransformer.HANDLER_FRAME_CODEC, PipelineTransformer.HANDLER_INJECTED,
				new EaglerInjectedMessageHandler(controller));
		channel.pipeline().fireUserEventTriggered(EnumPipelineEvent.EAGLER_INJECTED_MESSAGE_CONTROLLER);
		return controller;
	}

	/**
	 * IMPORTANT: Do not call this outside of the channel's event loop
	 */
	public void readPacket(ByteBuf buffer) {
		try {
			GameMessagePacket pkt;
			if (buffer.readableBytes() > 0) {
				ByteBufInputWrapper is = inputWrapper;
				is.buffer = buffer.skipBytes(1);
				int ii = buffer.readerIndex();
				if (buffer.getUnsignedByte(ii) == (short) 0xFF) {
					try {
						int start = buffer.readerIndex();
						is.readUnsignedByte();
						int count = is.readVarInt();
						for (int i = 0, j, k; i < count; ++i) {
							j = is.readVarInt();
							k = (buffer.readerIndex() - start) + j;
							if (j > is.available()) {
								throw new IOException("Packet fragment is too long: " + j + " > " + is.available());
							}
							pkt = protocol.readPacketV5(GamePluginMessageConstants.CLIENT_TO_SERVER, is);
							if (buffer.readerIndex() - start != k) {
								throw new IOException("Packet fragment was the wrong length: "
										+ (j + (buffer.readerIndex() - start) - k) + " != " + j);
							}
							handlePacket(pkt);
						}
						if (is.available() > 0) {
							throw new IOException(
									"Leftover data after reading multi-packet! (" + is.available() + " bytes)");
						}
					} catch (IndexOutOfBoundsException ex) {
						throw new IOException("Packet buffer underflow! (In multi-packet)", ex);
					}
				} else {
					try {
						pkt = protocol.readPacketV5(GamePluginMessageConstants.CLIENT_TO_SERVER, is);
					} catch (IndexOutOfBoundsException ex) {
						throw new IOException("Packet buffer underflow! (Packet ID 0x"
								+ Integer.toHexString(buffer.getUnsignedByte(ii)) + ")", ex);
					}
					handlePacket(pkt);
				}
			}
		} catch (IOException ex) {
			onException(ex);
		}
	}

	@Override
	protected void writePacket(GameMessagePacket packet) throws IOException {
		if (channel.isActive()) {
			channel.writeAndFlush(new InjectedMessage() {
				@Override
				public void writePacket(List<Object> output) {
					ByteBufOutputWrapper os = outputWrapper;
					ByteBuf buf = channel.alloc().buffer();
					buf.writeByte(0xEE);
					os.buffer = buf;
					try {
						protocol.writePacketV5(GamePluginMessageConstants.SERVER_TO_CLIENT, os, packet);
					} catch (IOException e) {
						buf.release();
						onException(e);
						output.add(Unpooled.EMPTY_BUFFER);
						return;
					} finally {
						os.buffer = null;
					}
					output.add(buf);
				}
			}, channel.voidPromise());
		}
	}

	@Override
	protected void writeMultiPacket(GameMessagePacket[] packets) throws IOException {
		if (channel.isActive()) {
			channel.writeAndFlush(new InjectedMessage() {
				@Override
				public void writePacket(List<Object> output) {
					ByteBufOutputWrapper os = outputWrapper;
					ByteBuf buf = channel.alloc().buffer();
					boolean shit = true;
					try {
						int total = packets.length;
						int[] marks;
						if (total > 16) {
							marks = new int[total << 1];
						} else {
							marks = InjectedMessageController.this.marks;
						}
						os.buffer = buf;
						int j, k;
						for (int i = 0; i < total; ++i) {
							j = i << 1;
							buf.writeByte(0xEE);
							k = buf.writerIndex();
							marks[j] = k;
							protocol.writePacketV5(GamePluginMessageConstants.SERVER_TO_CLIENT, os, packets[i]);
							marks[j + 1] = buf.writerIndex() - k;
						}
						int start = 0;
						int i, sendCount, totalLen, lastLen;
						while (total - start > 0) {
							sendCount = 0;
							totalLen = 0;
							do {
								i = marks[((start + sendCount) << 1) + 1];
								lastLen = GamePacketOutputBuffer.getVarIntSize(i) + i;
								totalLen += lastLen;
								++sendCount;
							} while (totalLen < 32760 && total - start - sendCount > 0);
							if (totalLen >= 32760) {
								--sendCount;
								totalLen -= lastLen;
							}
							if (sendCount <= 1) {
								i = start << 1;
								output.add(buf.retainedSlice(marks[i] - 1, marks[i + 1] + 1));
								shit = false;
								++start;
								continue;
							}
							i = 2 + totalLen + GamePacketOutputBuffer.getVarIntSize(sendCount);
							ByteBuf sendBuffer = channel.alloc().buffer(i, i);
							try {
								sendBuffer.writeShort(0xEEFF);
								BufferUtils.writeVarInt(sendBuffer, sendCount);
								for (j = 0; j < sendCount; ++j) {
									i = start << 1;
									lastLen = marks[i + 1];
									BufferUtils.writeVarInt(sendBuffer, lastLen);
									sendBuffer.writeBytes(buf, marks[i], lastLen);
									++start;
								}
								output.add(sendBuffer.retain());
								shit = false;
							} finally {
								sendBuffer.release();
							}
						}
					} catch (IOException e) {
						onException(e);
						if (shit) {
							output.add(Unpooled.EMPTY_BUFFER);
						}
						return;
					} finally {
						buf.release();
						os.buffer = null;
					}
				}
			}, channel.voidPromise());
		}
	}

}
