package net.lax1dude.eaglercraft.backend.server.base.notifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationManager;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationService;
import net.lax1dude.eaglercraft.backend.server.api.notifications.IconDef;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeHideV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsReleaseV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public abstract class NotificationManagerBase<PlayerObject> implements INotificationManager<PlayerObject> {

	protected final NotificationService<PlayerObject> service;

	public NotificationManagerBase(NotificationService<PlayerObject> service) {
		this.service = service;
	}

	protected abstract void touchIcon(UUID uuid);

	protected abstract void touchIcons(GameMessagePacket packet, UUID uuidA, UUID uuidB);

	protected abstract void touchIcons(Collection<UUID> uuids, Collection<UUID> tmp);

	protected abstract void releaseIcon(UUID uuid);

	protected abstract void releaseIcons();

	protected abstract void releaseIcons(Collection<UUID> uuids, Collection<UUID> tmp);

	protected abstract void sendPacket(GameMessagePacket packet);

	@Override
	public INotificationService<PlayerObject> getNotificationService() {
		return service;
	}

	@Override
	public void registerNotificationIcon(UUID iconUUID) {
		touchIcon(iconUUID);
	}

	@Override
	public void registerNotificationIcons(Collection<UUID> iconUUIDs) {
		int s = iconUUIDs.size();
		if(s == 0) {
			return;
		}
		if(s == 1) {
			touchIcon(iconUUIDs.iterator().next());
			return;
		}
		touchIcons(iconUUIDs, new ArrayList<>(s));
	}

	@Override
	public void registerUnmanagedNotificationIcon(UUID iconUUID, PacketImageData icon) {
		sendPacket(new SPacketNotifIconsRegisterV4EAG(
				Collections.singleton(new SPacketNotifIconsRegisterV4EAG.CreateIcon(iconUUID.getMostSignificantBits(),
						iconUUID.getLeastSignificantBits(), icon))));
	}

	@Override
	public void registerUnmanagedNotificationIcons(Collection<IconDef> icons) {
		int l = icons.size();
		SPacketNotifIconsRegisterV4EAG.CreateIcon[] arr = new SPacketNotifIconsRegisterV4EAG.CreateIcon[l];
		int i = 0;
		for(IconDef etr : icons) {
			if(i >= l) {
				break;
			}
			arr[i] = etr.toPacket();
		}
		if(i != l) {
			throw new IllegalStateException();
		}
		registerUnmanagedNotificationIconsRaw(Arrays.asList(arr));
	}

	@Override
	public void registerUnmanagedNotificationIconsRaw(Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> icons) {
		sendPacket(new SPacketNotifIconsRegisterV4EAG(icons));
	}

	@Override
	public void releaseUnmanagedNotificationIcon(UUID iconUUID) {
		sendPacket(new SPacketNotifIconsReleaseV4EAG(
				Collections.singleton(new SPacketNotifIconsReleaseV4EAG.DestroyIcon(iconUUID.getMostSignificantBits(),
						iconUUID.getLeastSignificantBits()))));
	}

	@Override
	public void releaseUnmanagedNotificationIcons(Collection<UUID> iconUUIDs) {
		int l = iconUUIDs.size();
		SPacketNotifIconsReleaseV4EAG.DestroyIcon[] arr = new SPacketNotifIconsReleaseV4EAG.DestroyIcon[l];
		int i = 0;
		for(UUID etr : iconUUIDs) {
			if(i >= l) {
				break;
			}
			arr[i] = new SPacketNotifIconsReleaseV4EAG.DestroyIcon(etr.getMostSignificantBits(),
					etr.getLeastSignificantBits());
		}
		if(i != l) {
			throw new IllegalStateException();
		}
		releaseUnmanagedNotificationIconsRaw(Arrays.asList(arr));
	}

	@Override
	public void releaseUnmanagedNotificationIconsRaw(Collection<SPacketNotifIconsReleaseV4EAG.DestroyIcon> iconUUIDs) {
		sendPacket(new SPacketNotifIconsReleaseV4EAG(iconUUIDs));
	}

	@Override
	public void releaseNotificationIcon(UUID iconUUID) {
		releaseIcon(iconUUID);
	}

	@Override
	public void releaseNotificationIcons(Collection<UUID> iconUUIDs) {
		int s = iconUUIDs.size();
		if(s == 0) {
			return;
		}
		if(s == 1) {
			releaseIcon(iconUUIDs.iterator().next());
			return;
		}
		releaseIcons(iconUUIDs, new ArrayList<>(s));
	}

	@Override
	public void releaseNotificationIcons() {
		releaseIcons();
	}

	@Override
	public void showNotificationBadge(INotificationBuilder<?> builder) {
		UUID iconA = builder.getMainIconUUID();
		UUID iconB = builder.getTitleIconUUID();
		if(iconA != null || iconB != null) {
			touchIcons(builder.buildPacket(), iconA, iconB);
		}else {
			sendPacket(builder.buildPacket());
		}
	}

	@Override
	public void showUnmanagedNotificationBadge(INotificationBuilder<?> builder) {
		showUnmanagedNotificationBadge(builder.buildPacket());
	}

	@Override
	public void showNotificationBadge(SPacketNotifBadgeShowV4EAG packet) {
		long iconAH = packet.mainIconUUIDMost;
		long iconAL = packet.mainIconUUIDLeast;
		long iconBH = packet.titleIconUUIDMost;
		long iconBL = packet.titleIconUUIDLeast;
		UUID iconA = null;
		UUID iconB = null;
		if(iconAH != 0l || iconAL != 0l) {
			iconA = new UUID(iconAH, iconAL);
		}
		if(iconBH != 0l || iconBL != 0l) {
			iconB = new UUID(iconBH, iconBL);
		}
		if(iconA != null || iconB != null) {
			touchIcons(packet, iconA, iconB);
		}else {
			sendPacket(packet);
		}
	}

	@Override
	public void showUnmanagedNotificationBadge(SPacketNotifBadgeShowV4EAG packet) {
		sendPacket(packet);
	}

	@Override
	public void hideNotificationBadge(UUID badgeUUID) {
		sendPacket(new SPacketNotifBadgeHideV4EAG(badgeUUID.getMostSignificantBits(),
				badgeUUID.getLeastSignificantBits()));
	}

}
