package net.lax1dude.eaglercraft.backend.server.api.notifications;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsReleaseV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface INotificationManager<PlayerObject> {

	@Nonnull
	Collection<IEaglerPlayer<PlayerObject>> getPlayerList();

	@Nonnull
	INotificationService<PlayerObject> getNotificationService();

	void registerNotificationIcon(@Nonnull UUID iconUUID);

	void registerNotificationIcons(@Nonnull Collection<UUID> iconUUIDs);

	void registerUnmanagedNotificationIcon(@Nonnull UUID iconUUID, @Nonnull PacketImageData icon);

	void registerUnmanagedNotificationIcons(@Nonnull Collection<IconDef> icons);

	void registerUnmanagedNotificationIconsRaw(@Nonnull Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> icons);

	void releaseUnmanagedNotificationIcon(@Nonnull UUID iconUUID);

	void releaseUnmanagedNotificationIcons(@Nonnull Collection<UUID> iconUUIDs);

	void releaseUnmanagedNotificationIconsRaw(@Nonnull Collection<SPacketNotifIconsReleaseV4EAG.DestroyIcon> iconUUIDs);

	void releaseNotificationIcon(@Nonnull UUID iconUUID);

	void releaseNotificationIcons(@Nonnull Collection<UUID> iconUUIDs);

	void releaseNotificationIcons();

	void showNotificationBadge(@Nonnull INotificationBuilder<?> builder);

	void showUnmanagedNotificationBadge(@Nonnull INotificationBuilder<?> builder);

	void showNotificationBadge(@Nonnull SPacketNotifBadgeShowV4EAG packet);

	void showUnmanagedNotificationBadge(@Nonnull SPacketNotifBadgeShowV4EAG packet);

	void hideNotificationBadge(@Nonnull UUID badgeUUID);

}
