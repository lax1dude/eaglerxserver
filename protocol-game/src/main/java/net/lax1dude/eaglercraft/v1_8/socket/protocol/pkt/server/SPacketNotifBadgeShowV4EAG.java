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

package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class SPacketNotifBadgeShowV4EAG implements GameMessagePacket {

	public static enum EnumBadgePriority {
		LOW(0), NORMAL(1), HIGHER(2), HIGHEST(3);

		public final int priority;

		private EnumBadgePriority(int priority) {
			this.priority = priority;
		}

		private static final EnumBadgePriority[] lookup = new EnumBadgePriority[4];

		public static EnumBadgePriority getByID(int id) {
			if (id >= 0 && id < lookup.length) {
				return lookup[id];
			} else {
				return NORMAL;
			}
		}

		static {
			EnumBadgePriority[] _values = values();
			for (int i = 0; i < _values.length; ++i) {
				lookup[_values[i].priority] = _values[i];
			}
		}
	}

	public long badgeUUIDMost;
	public long badgeUUIDLeast;
	public String bodyComponent;
	public String titleComponent;
	public String sourceComponent;
	public long originalTimestampSec;
	public boolean silent;
	public EnumBadgePriority priority;
	public long mainIconUUIDMost;
	public long mainIconUUIDLeast;
	public long titleIconUUIDMost;
	public long titleIconUUIDLeast;
	public int hideAfterSec;
	public int expireAfterSec;
	public int backgroundColor;
	public int bodyTxtColor;
	public int titleTxtColor;
	public int sourceTxtColor;

	public SPacketNotifBadgeShowV4EAG() {
	}

	public SPacketNotifBadgeShowV4EAG(long badgeUUIDMost, long badgeUUIDLeast, String bodyComponent,
			String titleComponent, String sourceComponent, long originalTimestampSec, boolean silent,
			EnumBadgePriority priority, long mainIconUUIDMost, long mainIconUUIDLeast, long titleIconUUIDMost,
			long titleIconUUIDLeast, int hideAfterSec, int expireAfterSec, int backgroundColor, int bodyTxtColor,
			int titleTxtColor, int sourceTxtColor) {
		this.badgeUUIDMost = badgeUUIDMost;
		this.badgeUUIDLeast = badgeUUIDLeast;
		this.bodyComponent = bodyComponent;
		this.titleComponent = titleComponent;
		this.sourceComponent = sourceComponent;
		this.originalTimestampSec = originalTimestampSec;
		this.silent = silent;
		this.priority = priority;
		this.mainIconUUIDMost = mainIconUUIDMost;
		this.mainIconUUIDLeast = mainIconUUIDLeast;
		this.titleIconUUIDMost = titleIconUUIDMost;
		this.titleIconUUIDLeast = titleIconUUIDLeast;
		this.hideAfterSec = hideAfterSec;
		this.expireAfterSec = expireAfterSec;
		this.backgroundColor = backgroundColor;
		this.bodyTxtColor = bodyTxtColor;
		this.titleTxtColor = titleTxtColor;
		this.sourceTxtColor = sourceTxtColor;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		badgeUUIDMost = buffer.readLong();
		badgeUUIDLeast = buffer.readLong();
		bodyComponent = buffer.readStringMC(32767);
		titleComponent = buffer.readStringMC(255);
		sourceComponent = buffer.readStringMC(255);
		originalTimestampSec = ((long) buffer.readUnsignedShort() << 32l) | ((long) buffer.readInt() & 0xFFFFFFFFl);
		int flags = buffer.readUnsignedByte();
		silent = (flags & 1) != 0;
		priority = EnumBadgePriority.getByID((flags >>> 1) & 3);
		hideAfterSec = buffer.readUnsignedByte();
		expireAfterSec = buffer.readUnsignedShort();
		mainIconUUIDMost = (flags & 8) != 0 ? buffer.readLong() : 0l;
		mainIconUUIDLeast = (flags & 8) != 0 ? buffer.readLong() : 0l;
		titleIconUUIDMost = (flags & 16) != 0 ? buffer.readLong() : 0l;
		titleIconUUIDLeast = (flags & 16) != 0 ? buffer.readLong() : 0l;
		backgroundColor = (buffer.readUnsignedByte() << 16) | (buffer.readUnsignedByte() << 8)
				| buffer.readUnsignedByte();
		bodyTxtColor = (buffer.readUnsignedByte() << 16) | (buffer.readUnsignedByte() << 8) | buffer.readUnsignedByte();
		titleTxtColor = (buffer.readUnsignedByte() << 16) | (buffer.readUnsignedByte() << 8)
				| buffer.readUnsignedByte();
		sourceTxtColor = (buffer.readUnsignedByte() << 16) | (buffer.readUnsignedByte() << 8)
				| buffer.readUnsignedByte();
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		buffer.writeLong(badgeUUIDMost);
		buffer.writeLong(badgeUUIDLeast);
		buffer.writeStringMC(bodyComponent);
		buffer.writeStringMC(titleComponent);
		buffer.writeStringMC(sourceComponent);
		buffer.writeShort((int) ((originalTimestampSec >>> 32l) & 0xFFFFl));
		buffer.writeInt((int) (originalTimestampSec & 0xFFFFFFFFl));
		int flags = ((priority != null ? priority.priority : 1) << 1);
		if (silent)
			flags |= 1;
		if (mainIconUUIDMost != 0l || mainIconUUIDLeast != 0l)
			flags |= 8;
		if (titleIconUUIDMost != 0l || titleIconUUIDLeast != 0l)
			flags |= 16;
		buffer.writeByte(flags);
		buffer.writeByte(hideAfterSec);
		buffer.writeShort(expireAfterSec);
		if ((flags & 8) != 0) {
			buffer.writeLong(mainIconUUIDMost);
			buffer.writeLong(mainIconUUIDLeast);
		}
		if ((flags & 16) != 0) {
			buffer.writeLong(titleIconUUIDMost);
			buffer.writeLong(titleIconUUIDLeast);
		}
		buffer.writeByte(backgroundColor >>> 16);
		buffer.writeByte(backgroundColor >>> 8);
		buffer.writeByte(backgroundColor);
		buffer.writeByte(bodyTxtColor >>> 16);
		buffer.writeByte(bodyTxtColor >>> 8);
		buffer.writeByte(bodyTxtColor);
		buffer.writeByte(titleTxtColor >>> 16);
		buffer.writeByte(titleTxtColor >>> 8);
		buffer.writeByte(titleTxtColor);
		buffer.writeByte(sourceTxtColor >>> 16);
		buffer.writeByte(sourceTxtColor >>> 8);
		buffer.writeByte(sourceTxtColor);
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return -1;
	}

}
