/*
 * Copyright (c) 2025 ayunami2000, lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.CPacketGetOtherTexturesV5EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.CPacketVoiceSignalConnectEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.CPacketVoiceSignalDescEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.CPacketVoiceSignalDisconnectPeerV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.CPacketVoiceSignalDisconnectV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.CPacketVoiceSignalICEEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.CPacketVoiceSignalRequestEAG;

public class RewindPacketDecoder<PlayerObject> extends RewindChannelHandler.Decoder<PlayerObject> {

	// TODO: rewrite to use individual named methods for each packet

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		int pktId = in.readUnsignedByte();
		ByteBuf bb = null;
		try {
			fuck: switch (pktId) {
				case 0x00:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x00);
					BufferUtils.writeVarInt(bb, in.readInt());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x03:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x01);
					BufferUtils.convertLegacyMCString(in, bb, 100);
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x07:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x02);
					in.readInt();
					BufferUtils.writeVarInt(bb, in.readInt());
					BufferUtils.writeVarInt(bb, in.readBoolean() ? 1 : 0);
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x0A:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x03);
					bb.writeBoolean(in.readBoolean());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x0B:
					double ppx = in.readDouble();
					double ppy = in.readDouble();
					double ppyf = in.readDouble();
					double ppz = in.readDouble();
					boolean ong = in.readBoolean();
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					if (ppy == -999.0D && ppyf == -999.0D) {
						bb = ctx.alloc().buffer();
						BufferUtils.writeVarInt(bb, 0x0C);
						double tmp0 = Math.toRadians(player().getYaw());
						double tmpc = Math.cos(tmp0);
						double tmps = Math.sin(tmp0);
						double tmpx = ppx * tmpc + ppz * tmps;
						double tmpz = -ppx * tmps + ppz * tmpc;
						float tmpfx = (float) tmpx * 32.0F;
						float tmpfz = (float) tmpz * 32.0F;
						if (tmpfx > 1.0F) {
							tmpfx = 1.0F;
						} else if (tmpfx < -1.0F) {
							tmpfx = -1.0F;
						}
						if (tmpfz > 1.0F) {
							tmpfz = 1.0F;
						} else if (tmpfz < -1.0F) {
							tmpfz = -1.0F;
						}
						bb.writeFloat(tmpfx);
						bb.writeFloat(tmpfz);
						bb.writeByte(player().isSneaking() ? 0x02 : 0x00);
						break;
					}
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x04);
					player().setPos(ppx, ppy, ppz);
					bb.writeDouble(ppx);
					bb.writeDouble(ppy);
					bb.writeDouble(ppz);
					bb.writeBoolean(ong);
					break;
				case 0x0C:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x05);
					float plyaw = in.readFloat();
					float plpitch = in.readFloat();
					ong = in.readBoolean();
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					player().setLook(plyaw, plpitch);
					bb.writeFloat(plyaw);
					bb.writeFloat(plpitch);
					bb.writeBoolean(ong);
					break;
				case 0x0D:
					ppx = in.readDouble();
					ppy = in.readDouble();
					ppyf = in.readDouble();
					ppz = in.readDouble();
					plyaw = in.readFloat();
					plpitch = in.readFloat();
					ong = in.readBoolean();
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					if (ppy == -999.0D && ppyf == -999.0D) {
						bb = ctx.alloc().buffer();
						BufferUtils.writeVarInt(bb, 0x05);
						player().setLook(plyaw, plpitch);
						bb.writeFloat(plyaw);
						bb.writeFloat(plpitch);
						bb.writeBoolean(ong);
						out.add(bb);
						bb = ctx.alloc().buffer();
						BufferUtils.writeVarInt(bb, 0x0C);
						double tmp0 = Math.toRadians(plyaw);
						double tmpc = Math.cos(tmp0);
						double tmps = Math.sin(tmp0);
						double tmpx = ppx * tmpc + ppz * tmps;
						double tmpz = -ppx * tmps + ppz * tmpc;
						float tmpfx = (float) tmpx * 32.0F;
						float tmpfz = (float) tmpz * 32.0F;
						if (tmpfx > 1.0F) {
							tmpfx = 1.0F;
						} else if (tmpfx < -1.0F) {
							tmpfx = -1.0F;
						}
						if (tmpfz > 1.0F) {
							tmpfz = 1.0F;
						} else if (tmpfz < -1.0F) {
							tmpfz = -1.0F;
						}
						bb.writeFloat(tmpfx);
						bb.writeFloat(tmpfz);
						bb.writeByte(player().isSneaking() ? 0x02 : 0x00);
						break;
					}
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x06);
					player().setPos(ppx, ppy, ppz);
					player().setLook(plyaw, plpitch);
					bb.writeDouble(ppx);
					bb.writeDouble(ppy);
					bb.writeDouble(ppz);
					bb.writeFloat(plyaw);
					bb.writeFloat(plpitch);
					bb.writeBoolean(ong);
					break;
				case 0x0E:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x07);
					bb.writeByte(in.readByte());
					bb.writeLong(BufferUtils.createPosition(in.readInt(), in.readUnsignedByte(), in.readInt()));
					bb.writeByte(in.readByte());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x0F:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x08);
					bb.writeLong(BufferUtils.createPosition(in.readInt(), in.readUnsignedByte(), in.readInt()));
					bb.writeByte(in.readUnsignedByte());
					BufferUtils.convertLegacySlot(in, bb, player());
					bb.writeByte(in.readByte());
					bb.writeByte(in.readByte());
					bb.writeByte(in.readByte());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x10:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x09);
					bb.writeShort(in.readShort());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x12:
					if(in.readableBytes() > 5) throw new IndexOutOfBoundsException();
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0A);
					break;
				case 0x13:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0B);
					BufferUtils.writeVarInt(bb, in.readInt());
					int action = in.readUnsignedByte() - 1;
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					BufferUtils.writeVarInt(bb, action);
					BufferUtils.writeVarInt(bb, 0);
					if (action == 0) {
						player().setSneaking(true);
					} else if (action == 1) {
						player().setSneaking(false);
					}
					break;
				case 0x65:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0D);
					bb.writeByte(in.readByte());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x66:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0E);
					byte windowId = in.readByte();
					short slot = in.readShort();
					if (player().getEnchWindows().contains((short) windowId) && slot > 0) {
						++slot;
					}
					bb.writeByte(windowId);
					bb.writeShort(slot);
					bb.writeByte(in.readByte());
					bb.writeShort(in.readShort());
					bb.writeByte(in.readByte());
					BufferUtils.convertLegacySlot(in, bb, player());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x6A:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0F);
					bb.writeByte(in.readByte());
					bb.writeShort(in.readShort());
					bb.writeBoolean(in.readBoolean());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x6B:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x10);
					bb.writeShort(in.readShort());
					BufferUtils.convertLegacySlot(in, bb, player());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x6C:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x11);
					bb.writeByte(in.readByte());
					bb.writeByte(in.readByte());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0x82:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x12);
					bb.writeLong(BufferUtils.createPosition(in.readInt(), in.readShort(), in.readInt()));
					for (int ii = 0; ii < 4; ++ii) {
						BufferUtils.writeMCString(bb, "\"" + BufferUtils.readLegacyMCString(in, 255).replaceAll("\\\\", "\\\\").replaceAll("\"", "\\\\\"") + "\"", 4095);
					}
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0xCA:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x13);
					bb.writeByte(in.readByte());
					bb.writeFloat(in.readByte());
					bb.writeFloat(in.readByte());
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0xCB:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x14);
					BufferUtils.convertLegacyMCString(in, bb, 255);
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					bb.writeBoolean(false);
					break;
				case 0xCC:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x15);
					BufferUtils.convertLegacyMCString(in, bb, 255);
					bb.writeByte(16 >> in.readByte());
					byte guh = in.readByte();
					bb.writeByte(guh & 3);
					bb.writeBoolean((guh & 8) != 0);
					in.readByte();
					bb.writeByte(in.readBoolean() ? 0xFF : 0xFE);
					if(in.isReadable()) throw new IndexOutOfBoundsException();
					break;
				case 0xCD:
					bb = ctx.alloc().buffer();
					if(in.readableBytes() > 1) throw new IndexOutOfBoundsException();
					BufferUtils.writeVarInt(bb, 0x16);
					BufferUtils.writeVarInt(bb, 0);
					break;
				case 0xFA:
					String name = BufferUtils.readLegacyMCString(in, 255);
					int pmLen = in.readUnsignedShort();
					if (in.readableBytes() != pmLen) {
						throw new IndexOutOfBoundsException();
					}
					switch(name) {
					case "MC|AdvCdm":
						int ri = in.readerIndex();
						int cmdX = in.readInt();
						int cmdY = in.readInt();
						int cmdZ = in.readInt();
						String cmd = BufferUtils.readLegacyMCString(in, 32767);
						if(in.isReadable()) {
							throw new IndexOutOfBoundsException();
						}
						in.readerIndex(ri);
						in.writerIndex(ri);
						in.writeByte(0);
						in.writeInt(cmdX);
						in.writeInt(cmdY);
						in.writeInt(cmdZ);
						BufferUtils.writeMCString(in, cmd, 32767);
						in.writeBoolean(true);
						pmLen = in.writerIndex() - ri;
						break;
					case "MC|ItemName":
						ri = in.readerIndex();
						bb = ctx.alloc().buffer();
						in.readBytes(bb, pmLen);
						if(in.isReadable()) {
							throw new IndexOutOfBoundsException();
						}
						in.readerIndex(ri);
						in.writerIndex(ri);
						BufferUtils.writeVarInt(in, pmLen);
						in.writeBytes(bb);
						bb.release();
						pmLen = in.writerIndex() - ri;
						break;
					case "MC|BEdit":
					case "MC|BSign":
						ri = in.readerIndex();
						bb = ctx.alloc().buffer();
						BufferUtils.convertLegacySlot(in, bb, player());
						if(in.isReadable()) {
							throw new IndexOutOfBoundsException();
						}
						in.readerIndex(ri);
						in.writerIndex(ri);
						in.writeBytes(bb);
						pmLen = bb.writerIndex();
						bb.release();
						break;
					case "EAG|FetchSkin":
						ri = in.readerIndex();
						int cookie = in.readUnsignedShort();
						String username = BufferUtils.readCharSequence(in, pmLen - 2, StandardCharsets.US_ASCII).toString();
						if(in.isReadable()) {
							throw new IndexOutOfBoundsException();
						}
						TabListTracker.ListItem playerItem = tabList().getItemByName(username);
						if(playerItem != null) {
							UUID uuid = playerItem.playerUUID;
							player().addSkinRequest(uuid, cookie);
							messageController().recieveInboundMessage(new CPacketGetOtherTexturesV5EAG(
									uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
						}
						break fuck;
					case "EAG|Voice":
						ri = in.readerIndex();
						int sig = in.readUnsignedByte();
						switch(sig) {
						case 0: // VOICE_SIGNAL_REQUEST
							String target = BufferUtils.readASCIIStr(in);
							if(in.isReadable()) {
								throw new IndexOutOfBoundsException();
							}
							UUID uuid = player().getVoicePlayerByName(target);
							if(uuid != null) {
								messageController().recieveInboundMessage(new CPacketVoiceSignalRequestEAG(
										uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
							}
							break;
						case 1: // VOICE_SIGNAL_CONNECT
							if(in.isReadable()) {
								throw new IndexOutOfBoundsException();
							}
							messageController().recieveInboundMessage(new CPacketVoiceSignalConnectEAG());
							break;
						case 2: // VOICE_SIGNAL_DISCONNECT
							if(in.isReadable()) {
								target = BufferUtils.readASCIIStr(in);
								if(in.isReadable()) {
									throw new IndexOutOfBoundsException();
								}
								uuid = player().getVoicePlayerByName(target);
								if(uuid != null) {
									messageController().recieveInboundMessage(new CPacketVoiceSignalDisconnectPeerV4EAG(
											uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));
								}
							}else {
								if(in.isReadable()) {
									throw new IndexOutOfBoundsException();
								}
								player().releaseVoiceGlobalMap();
								messageController().recieveInboundMessage(new CPacketVoiceSignalDisconnectV4EAG());
							}
							break;
						case 3: // VOICE_SIGNAL_ICE
							uuid = player().getVoicePlayerByName(BufferUtils.readASCIIStr(in));
							if(uuid != null) {
								byte[] data = new byte[in.readUnsignedShort()];
								in.readBytes(data);
								if(in.isReadable()) {
									throw new IndexOutOfBoundsException();
								}
								messageController().recieveInboundMessage(new CPacketVoiceSignalICEEAG(
										uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), data));
							}
							break;
						case 4: // VOICE_SIGNAL_DESC
							uuid = player().getVoicePlayerByName(BufferUtils.readASCIIStr(in));
							if(uuid != null) {
								byte[] data = new byte[in.readUnsignedShort()];
								in.readBytes(data);
								if(in.isReadable()) {
									throw new IndexOutOfBoundsException();
								}
								messageController().recieveInboundMessage(new CPacketVoiceSignalDescEAG(
										uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), data));
							}
							break;
						default:
							throw new IndexOutOfBoundsException();
						}
						break fuck;
					default:
						//
					}
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x17);
					BufferUtils.writeMCString(bb, name, 255);
					bb.writeBytes(in, pmLen);
					break;
				case 0xFF:
					int len = in.readShort();
					if(in.readableBytes() != len << 1) {
						throw new IndexOutOfBoundsException();
					}
					break;
				default:
					throw new IllegalStateException("Unknown packet: " + pktId);
			}
			if (bb != null) {
				out.add(bb.retain());
			}
		} finally {
			if (bb != null) {
				bb.release();
			}
		}
	}

}
