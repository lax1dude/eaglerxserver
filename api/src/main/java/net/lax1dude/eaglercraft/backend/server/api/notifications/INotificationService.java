package net.lax1dude.eaglercraft.backend.server.api.notifications;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface INotificationService<PlayerObject> extends IPacketImageLoader {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	<ComponentObject> INotificationBuilder<ComponentObject> createNotificationBuilder(Class<ComponentObject> componentObj);

	default INotificationManager<PlayerObject> getNotificationManager(PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getNotificationManager() : null;
	}

	default INotificationManager<PlayerObject> getNotificationManager(IEaglerPlayer<PlayerObject> player) {
		return player.getNotificationManager();
	}

	INotificationManager<PlayerObject> getNotificationManagerMulti(Collection<PlayerObject> players);

	INotificationManager<PlayerObject> getNotificationManagerMultiAll(Collection<IEaglerPlayer<PlayerObject>> players);

	void registerGlobalNotificationIcon(UUID iconUUID, PacketImageData icon);

	void registerGlobalNotificationIcons(Map<UUID, PacketImageData> icons);

	void releaseGlobalNotificationIcon(UUID iconUUID);

	void releaseGlobalNotificationIcons(Collection<UUID> iconUUIDs);

	void showGlobalNotificationBadge(INotificationBuilder<?> builder);

	void showGlobalNotificationBadge(SPacketNotifBadgeShowV4EAG packet);

	void hideGlobalNotificationBadge(UUID badgeUUID);

}
