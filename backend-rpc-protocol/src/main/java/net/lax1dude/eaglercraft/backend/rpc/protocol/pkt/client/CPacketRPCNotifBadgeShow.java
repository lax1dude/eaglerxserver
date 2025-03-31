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

import static net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket.readString;
import static net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket.writeString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCNotifBadgeShow implements EaglerBackendRPCPacket {

	public static enum EnumBadgePriority {
		LOW(0), NORMAL(1), HIGHER(2), HIGHEST(3);
		
		public final int priority;
		
		private EnumBadgePriority(int priority) {
			this.priority = priority;
		}
		
		private static final EnumBadgePriority[] lookup = new EnumBadgePriority[4];
		
		public static EnumBadgePriority getByID(int id) {
			if(id >= 0 && id < lookup.length) {
				return lookup[id];
			}else {
				return NORMAL;
			}
		}
		
		static {
			EnumBadgePriority[] _values = values();
			for(int i = 0; i < _values.length; ++i) {
				lookup[_values[i].priority] = _values[i];
			}
		}
	}

	public UUID badgeUUID;
	public String bodyComponent;
	public String titleComponent;
	public String sourceComponent;
	public long originalTimestampSec;
	public boolean silent;
	public boolean managed;
	public EnumBadgePriority priority;
	public UUID mainIconUUID;
	public UUID titleIconUUID;
	public int hideAfterSec;
	public int expireAfterSec;
	public int backgroundColor;
	public int bodyTxtColor;
	public int titleTxtColor;
	public int sourceTxtColor;

	public CPacketRPCNotifBadgeShow() {
	}

	public CPacketRPCNotifBadgeShow(UUID badgeUUID, String bodyComponent, String titleComponent, String sourceComponent,
			long originalTimestampSec, UUID mainIconUUID, UUID titleIconUUID, boolean silent, boolean managed,
			EnumBadgePriority priority, int hideAfterSec, int expireAfterSec, int backgroundColor, int bodyTxtColor,
			int titleTxtColor, int sourceTxtColor) {
		this.badgeUUID = badgeUUID;
		this.bodyComponent = bodyComponent;
		this.titleComponent = titleComponent;
		this.sourceComponent = sourceComponent;
		this.originalTimestampSec = originalTimestampSec;
		this.mainIconUUID = mainIconUUID;
		this.titleIconUUID = titleIconUUID;
		this.silent = silent;
		this.managed = managed;
		this.priority = priority;
		this.hideAfterSec = hideAfterSec;
		this.expireAfterSec = expireAfterSec;
		this.backgroundColor = backgroundColor;
		this.bodyTxtColor = bodyTxtColor;
		this.titleTxtColor = titleTxtColor;
		this.sourceTxtColor = sourceTxtColor;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		badgeUUID = new UUID(buffer.readLong(), buffer.readLong());
		bodyComponent = readString(buffer, 32767, true, StandardCharsets.UTF_8);
		titleComponent = readString(buffer, 255, false, StandardCharsets.UTF_8);
		sourceComponent = readString(buffer, 255, false, StandardCharsets.UTF_8);
		originalTimestampSec = ((long)buffer.readUnsignedShort() << 32l) | ((long)buffer.readInt() & 0xFFFFFFFFl);
		int flags = buffer.readUnsignedByte();
		silent = (flags & 1) != 0;
		priority = EnumBadgePriority.getByID((flags >>> 1) & 3);
		hideAfterSec = buffer.readUnsignedByte();
		expireAfterSec = buffer.readUnsignedShort();
		mainIconUUID = (flags & 8) != 0 ? new UUID(buffer.readLong(), buffer.readLong()) : null;
		titleIconUUID = (flags & 16) != 0 ? new UUID(buffer.readLong(), buffer.readLong()) : null;
		managed = (flags & 32) != 0;
		backgroundColor = (buffer.readUnsignedByte() << 16) | (buffer.readUnsignedByte() << 8) | buffer.readUnsignedByte();
		bodyTxtColor = (buffer.readUnsignedByte() << 16) | (buffer.readUnsignedByte() << 8) | buffer.readUnsignedByte();
		titleTxtColor = (buffer.readUnsignedByte() << 16) | (buffer.readUnsignedByte() << 8) | buffer.readUnsignedByte();
		sourceTxtColor = (buffer.readUnsignedByte() << 16) | (buffer.readUnsignedByte() << 8) | buffer.readUnsignedByte();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeLong(badgeUUID.getMostSignificantBits());
		buffer.writeLong(badgeUUID.getLeastSignificantBits());
		writeString(buffer, bodyComponent, true, StandardCharsets.UTF_8);
		writeString(buffer, titleComponent, false, StandardCharsets.UTF_8);
		writeString(buffer, sourceComponent, false, StandardCharsets.UTF_8);
		buffer.writeShort((int)((originalTimestampSec >> 32l) & 0xFFFFl));
		buffer.writeInt((int)(originalTimestampSec & 0xFFFFFFFFl));
		int flags = 0;
		if(silent) flags |= 1;
		flags |= ((priority != null ? priority.priority : 1) << 1);
		if(mainIconUUID != null) flags |= 8;
		if(titleIconUUID != null) flags |= 16;
		if(managed) flags |= 32;
		buffer.writeByte(flags);
		buffer.writeByte(hideAfterSec);
		buffer.writeShort(expireAfterSec);
		if(mainIconUUID != null) {
			buffer.writeLong(mainIconUUID.getMostSignificantBits());
			buffer.writeLong(mainIconUUID.getLeastSignificantBits());
		}
		if(titleIconUUID != null) {
			buffer.writeLong(titleIconUUID.getMostSignificantBits());
			buffer.writeLong(titleIconUUID.getLeastSignificantBits());
		}
		buffer.writeByte((backgroundColor >>> 16) & 0xFF);
		buffer.writeByte((backgroundColor >>> 8) & 0xFF);
		buffer.writeByte(backgroundColor & 0xFF);
		buffer.writeByte((bodyTxtColor >>> 16) & 0xFF);
		buffer.writeByte((bodyTxtColor >>> 8) & 0xFF);
		buffer.writeByte(bodyTxtColor & 0xFF);
		buffer.writeByte((titleTxtColor >>> 16) & 0xFF);
		buffer.writeByte((titleTxtColor >>> 8) & 0xFF);
		buffer.writeByte(titleTxtColor & 0xFF);
		buffer.writeByte((sourceTxtColor >>> 16) & 0xFF);
		buffer.writeByte((sourceTxtColor >>> 8) & 0xFF);
		buffer.writeByte(sourceTxtColor & 0xFF);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return -1;
	}

}