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
		switch(packet.priority) {
		case HIGHEST:
			priority = EnumBadgePriority.HIGHEST;
			break;
		case HIGHER:
			priority = EnumBadgePriority.HIGHER;
			break;
		case NORMAL:
		default:
			priority = EnumBadgePriority.NORMAL;
			break;
		case LOW:
			priority = EnumBadgePriority.LOW;
			break;
		}
		return new SPacketNotifBadgeShowV4EAG(badgeUUIDMost, badgeUUIDLeast, packet.bodyComponent,
				packet.titleComponent, packet.sourceComponent, packet.originalTimestampSec, packet.silent, priority,
				mainIconUUIDMost, mainIconUUIDLeast, titleIconUUIDMost, titleIconUUIDLeast, packet.hideAfterSec,
				packet.expireAfterSec, packet.backgroundColor, packet.bodyTxtColor, packet.titleTxtColor,
				packet.sourceTxtColor);
	}

}
