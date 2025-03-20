package net.lax1dude.eaglercraft.backend.server.base.notifications;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationManager;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationService;
import net.lax1dude.eaglercraft.backend.server.api.notifications.IconDef;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG.CreateIcon;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsReleaseV4EAG.DestroyIcon;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class NotificationManagerNOP<PlayerObject> implements INotificationManager<PlayerObject> {

	private final NotificationService<PlayerObject> owner;

	NotificationManagerNOP(NotificationService<PlayerObject> owner) {
		this.owner = owner;
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getPlayerList() {
		return Collections.emptyList();
	}

	@Override
	public INotificationService<PlayerObject> getNotificationService() {
		return owner;
	}

	@Override
	public void registerNotificationIcon(UUID iconUUID) {
	}

	@Override
	public void registerNotificationIcons(Collection<UUID> iconUUIDs) {
	}

	@Override
	public void registerUnmanagedNotificationIcon(UUID iconUUID, PacketImageData icon) {
	}

	@Override
	public void registerUnmanagedNotificationIcons(Collection<IconDef> icons) {
	}

	@Override
	public void registerUnmanagedNotificationIconsRaw(Collection<CreateIcon> icons) {
	}

	@Override
	public void releaseUnmanagedNotificationIcon(UUID iconUUID) {
	}

	@Override
	public void releaseUnmanagedNotificationIcons(Collection<UUID> iconUUIDs) {
	}

	@Override
	public void releaseUnmanagedNotificationIconsRaw(Collection<DestroyIcon> iconUUIDs) {
	}

	@Override
	public void releaseNotificationIcon(UUID iconUUID) {
	}

	@Override
	public void releaseNotificationIcons(Collection<UUID> iconUUIDs) {
	}

	@Override
	public void releaseNotificationIcons() {
	}

	@Override
	public void showNotificationBadge(INotificationBuilder<?> builder) {
	}

	@Override
	public void showUnmanagedNotificationBadge(INotificationBuilder<?> builder) {
	}

	@Override
	public void showNotificationBadge(SPacketNotifBadgeShowV4EAG packet) {
	}

	@Override
	public void showUnmanagedNotificationBadge(SPacketNotifBadgeShowV4EAG packet) {
	}

	@Override
	public void hideUnmanagedNotificationBadge(UUID badgeUUID) {
	}

}
