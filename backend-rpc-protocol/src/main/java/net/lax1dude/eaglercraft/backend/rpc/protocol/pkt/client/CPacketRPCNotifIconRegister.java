/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData;

import java.util.UUID;

public class CPacketRPCNotifIconRegister implements EaglerBackendRPCPacket {

	public static class RegisterIcon {

		public final UUID uuid;
		public final PacketImageData image;

		public RegisterIcon(UUID uuid, PacketImageData image) {
			this.uuid = uuid;
			this.image = image;
		}

	}

	public Collection<RegisterIcon> notifIcons;

	public CPacketRPCNotifIconRegister() {
	}

	public CPacketRPCNotifIconRegister(Collection<RegisterIcon> notifIcons) {
		this.notifIcons = notifIcons;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		int cnt = buffer.readUnsignedByte();
		if (cnt > 0) {
			RegisterIcon[] icns = new RegisterIcon[cnt];
			for (int i = 0; i < cnt; ++i) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				PacketImageData img = PacketImageData.readRGB16(buffer);
				icns[i] = new RegisterIcon(uuid, img);
			}
			notifIcons = Arrays.asList(icns);
		} else {
			notifIcons = null;
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if (notifIcons != null) {
			int l = notifIcons.size();
			if (l > 255) {
				throw new IOException("Too many notification icons in packet! (Max is 255, got " + l + " total)");
			}
			buffer.writeByte(l);
			if (l > 0) {
				if (notifIcons instanceof RandomAccess) {
					List<RegisterIcon> lst = (List<RegisterIcon>) notifIcons;
					for (int i = 0; i < l; ++i) {
						RegisterIcon icn = lst.get(i);
						buffer.writeLong(icn.uuid.getMostSignificantBits());
						buffer.writeLong(icn.uuid.getLeastSignificantBits());
						PacketImageData.writeRGB16(buffer, icn.image);
					}
				} else {
					for (RegisterIcon icn : notifIcons) {
						buffer.writeLong(icn.uuid.getMostSignificantBits());
						buffer.writeLong(icn.uuid.getLeastSignificantBits());
						PacketImageData.writeRGB16(buffer, icn.image);
					}
				}
			}
		} else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		if (notifIcons == null) {
			return 1;
		}
		int l = notifIcons.size();
		int i = 1 + (l << 4);
		if (l > 0) {
			if (notifIcons instanceof RandomAccess) {
				List<RegisterIcon> lst = (List<RegisterIcon>) notifIcons;
				for (int j = 0; j < l; ++j) {
					i += lst.get(j).image.getByteLengthRGB16();
				}
			} else {
				for (RegisterIcon icn : notifIcons) {
					i += icn.image.getByteLengthRGB16();
				}
			}
		}
		return i;
	}

}