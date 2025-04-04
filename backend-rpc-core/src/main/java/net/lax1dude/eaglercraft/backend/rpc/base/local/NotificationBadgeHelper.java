package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.notifications.EnumBadgePriority;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;

class NotificationBadgeHelper {

	static EnumBadgePriority wrap(net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority priority) {
		switch(priority) {
		case HIGHEST:
			return EnumBadgePriority.HIGHEST;
		case HIGHER:
			return EnumBadgePriority.HIGHER;
		case NORMAL:
		default:
			return EnumBadgePriority.NORMAL;
		case LOW:
			return EnumBadgePriority.LOW;
		}
	}

	static net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority unwrap(EnumBadgePriority priority) {
		switch(priority) {
		case HIGHEST:
			return net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority.HIGHEST;
		case HIGHER:
			return net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority.HIGHER;
		case NORMAL:
		default:
			return net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority.NORMAL;
		case LOW:
			return net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority.LOW;
		}
	}

	static INotificationBadge wrap(SPacketNotifBadgeShowV4EAG badge, boolean managed) {
		return new NotificationBadgeLocal(badge, managed);
	}

	static NotificationBadgeLocal unwrap(INotificationBadge badge) {
		return (NotificationBadgeLocal) badge;
	}

	static class NotificationBadgeLocal implements INotificationBadge {

		final SPacketNotifBadgeShowV4EAG packet;
		final boolean managed;

		NotificationBadgeLocal(SPacketNotifBadgeShowV4EAG packet, boolean managed) {
			this.packet = packet;
			this.managed = managed;
		}

	}

}
