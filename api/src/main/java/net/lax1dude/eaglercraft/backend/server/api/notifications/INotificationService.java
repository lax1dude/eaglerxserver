package net.lax1dude.eaglercraft.backend.server.api.notifications;

import java.util.Collection;
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

	INotificationManager<PlayerObject> getNotificationManagerAll();

	INotificationManager<PlayerObject> getNotificationManagerMulti(Collection<PlayerObject> players);

	INotificationManager<PlayerObject> getNotificationManagerMultiEagler(Collection<IEaglerPlayer<PlayerObject>> players);

	void registerNotificationIcon(UUID iconUUID, PacketImageData icon);

	void registerNotificationIcons(Collection<IconDef> icons);

	void unregisterNotificationIcon(UUID iconUUID);

	void unregisterNotificationIcons(Collection<UUID> iconUUIDs);

	void showNotificationBadge(INotificationBuilder<?> builder);

	void showNotificationBadge(SPacketNotifBadgeShowV4EAG packet);

}
