package net.lax1dude.eaglercraft.backend.server.base.notifications;

import java.util.Collection;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.server.api.notifications.IconDef;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG.CreateIcon;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsReleaseV4EAG.DestroyIcon;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class NotificationManagerLegacy<PlayerObject> extends NotificationManagerPlayer<PlayerObject> {

	public NotificationManagerLegacy(NotificationService<PlayerObject> service,
			EaglerPlayerInstance<PlayerObject> player) {
		super(service, player);
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
		handleNotifBadgePacket(builder.buildPacket());
	}

	@Override
	public void showUnmanagedNotificationBadge(INotificationBuilder<?> builder) {
		handleNotifBadgePacket(builder.buildPacket());
	}

	@Override
	public void showNotificationBadge(SPacketNotifBadgeShowV4EAG packet) {
		handleNotifBadgePacket(packet);
	}

	@Override
	public void showUnmanagedNotificationBadge(SPacketNotifBadgeShowV4EAG packet) {
		handleNotifBadgePacket(packet);
	}

	@Override
	public void hideUnmanagedNotificationBadge(UUID badgeUUID) {
	}

	@Override
	protected void touchIcon(UUID uuid) {
	}

	@Override
	protected void touchIcons(GameMessagePacket packet, UUID uuidA, UUID uuidB) {
	}

	@Override
	protected void touchIcons(Collection<UUID> uuids, Collection<UUID> tmp) {
	}

	@Override
	protected void releaseIcon(UUID uuid) {
	}

	@Override
	protected void releaseIcons() {
	}

	@Override
	protected void releaseIcons(Collection<UUID> uuids, Collection<UUID> tmp) {
	}

	@Override
	protected void sendPacket(GameMessagePacket packet) {
		if(packet instanceof SPacketNotifBadgeShowV4EAG) {
			handleNotifBadgePacket((SPacketNotifBadgeShowV4EAG) packet);
		}
	}

	protected void handleNotifBadgePacket(SPacketNotifBadgeShowV4EAG packet) {
		//TODO
	}

}
