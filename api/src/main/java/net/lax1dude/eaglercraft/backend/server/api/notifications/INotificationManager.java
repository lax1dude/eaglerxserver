package net.lax1dude.eaglercraft.backend.server.api.notifications;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface INotificationManager<PlayerObject> {

	Collection<IEaglerPlayer<PlayerObject>> getPlayerList();

	INotificationService<PlayerObject> getNotificationService();

	void registerNotificationIcon(UUID iconUUID, PacketImageData icon);

	void registerNotificationIcons(Map<UUID, PacketImageData> icons);

	void releaseNotificationIcon(UUID iconUUID);

	void releaseNotificationIcons(Collection<UUID> iconUUIDs);

	void showNotificationBadge(INotificationBuilder<?> builder);

	void showNotificationBadge(SPacketNotifBadgeShowV4EAG packet);

	void hideNotificationBadge(UUID badgeUUID);

}
