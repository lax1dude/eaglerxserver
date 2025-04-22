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

package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCNotifBadgeShow;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG.EnumBadgePriority;

class NotificationRPCHelper {

	static SPacketNotifBadgeShowV4EAG translateRPCPacket(CPacketRPCNotifBadgeShow packet) {
		long badgeUUIDMost = packet.badgeUUID.getMostSignificantBits();
		long badgeUUIDLeast = packet.badgeUUID.getLeastSignificantBits();
		long mainIconUUIDMost, mainIconUUIDLeast;
		if(packet.mainIconUUID != null) {
			mainIconUUIDMost = packet.mainIconUUID.getMostSignificantBits();
			mainIconUUIDLeast = packet.mainIconUUID.getLeastSignificantBits();
		}else {
			mainIconUUIDMost = 0l;
			mainIconUUIDLeast = 0l;
		}
		long titleIconUUIDMost, titleIconUUIDLeast;
		if(packet.titleIconUUID != null) {
			titleIconUUIDMost = packet.titleIconUUID.getMostSignificantBits();
			titleIconUUIDLeast = packet.titleIconUUID.getLeastSignificantBits();
		}else {
			titleIconUUIDMost = 0l;
			titleIconUUIDLeast = 0l;
		}
		EnumBadgePriority priority;
		priority = switch(packet.priority) {
		case HIGHEST -> EnumBadgePriority.HIGHEST;
		case HIGHER ->  EnumBadgePriority.HIGHER;
		default -> EnumBadgePriority.NORMAL;
		case LOW -> EnumBadgePriority.LOW;
		};
		return new SPacketNotifBadgeShowV4EAG(badgeUUIDMost, badgeUUIDLeast, packet.bodyComponent,
				packet.titleComponent, packet.sourceComponent, packet.originalTimestampSec, packet.silent, priority,
				mainIconUUIDMost, mainIconUUIDLeast, titleIconUUIDMost, titleIconUUIDLeast, packet.hideAfterSec,
				packet.expireAfterSec, packet.backgroundColor, packet.bodyTxtColor, packet.titleTxtColor,
				packet.sourceTxtColor);
	}

}
