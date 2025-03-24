package net.lax1dude.eaglercraft.backend.server.api.notifications;

import java.util.Collection;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsReleaseV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface INotificationManager<PlayerObject> {

	Collection<IEaglerPlayer<PlayerObject>> getPlayerList();

	INotificationService<PlayerObject> getNotificationService();

	void registerNotificationIcon(UUID iconUUID);

	void registerNotificationIcons(Collection<UUID> iconUUIDs);

	void registerUnmanagedNotificationIcon(UUID iconUUID, PacketImageData icon);

	void registerUnmanagedNotificationIcons(Collection<IconDef> icons);

	void registerUnmanagedNotificationIconsRaw(Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> icons);

	void releaseUnmanagedNotificationIcon(UUID iconUUID);

	void releaseUnmanagedNotificationIcons(Collection<UUID> iconUUIDs);

	void releaseUnmanagedNotificationIconsRaw(Collection<SPacketNotifIconsReleaseV4EAG.DestroyIcon> iconUUIDs);

	void releaseNotificationIcon(UUID iconUUID);

	void releaseNotificationIcons(Collection<UUID> iconUUIDs);

	void releaseNotificationIcons();

	void showNotificationBadge(INotificationBuilder<?> builder);

	void showUnmanagedNotificationBadge(INotificationBuilder<?> builder);

	void showNotificationBadge(SPacketNotifBadgeShowV4EAG packet);

	void showUnmanagedNotificationBadge(SPacketNotifBadgeShowV4EAG packet);

	void hideNotificationBadge(UUID badgeUUID);

}
