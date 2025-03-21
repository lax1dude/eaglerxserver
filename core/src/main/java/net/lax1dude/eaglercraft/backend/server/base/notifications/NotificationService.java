package net.lax1dude.eaglercraft.backend.server.base.notifications;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationManager;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationService;
import net.lax1dude.eaglercraft.backend.server.api.notifications.IconDef;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.PacketImageLoader;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class NotificationService<PlayerObject> implements INotificationService<PlayerObject> {

	final EaglerXServer<PlayerObject> server;
	final IPlatformComponentHelper componentHelper;
	final Class<?> componentType;

	private final ReadWriteLock registeredIconLock;
	private final Map<UUID, PacketImageData> registeredIcons;
	private final NotificationManagerMultiAll<PlayerObject> allPlayersManager;
	private final NotificationManagerNOP<PlayerObject> nopPlayersManager;

	public NotificationService(EaglerXServer<PlayerObject> server) {
		this.server = server;
		this.componentHelper = server.getPlatform().getComponentHelper();
		this.componentType = componentHelper.getComponentType();
		this.registeredIconLock = new ReentrantReadWriteLock();
		this.registeredIcons = new HashMap<>();
		this.allPlayersManager = new NotificationManagerMultiAll<>(this);
		this.nopPlayersManager = new NotificationManagerNOP<>(this);
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public <ComponentObject> INotificationBuilder<ComponentObject> createNotificationBuilder(
			Class<ComponentObject> componentType) {
		if(componentType == this.componentType) {
			return new NotificationBuilder<ComponentObject>(componentHelper);
		}else {
			throw new ClassCastException("Component class " + componentType.getName() + " is not supported on this platform!");
		}
	}

	@Override
	public INotificationManager<PlayerObject> getNotificationManagerAll() {
		return allPlayersManager;
	}

	@Override
	public INotificationManager<PlayerObject> getNotificationManagerMulti(Collection<PlayerObject> players) {
		if(players.size() == 0) {
			return nopPlayersManager;
		}
		Collection<NotificationManagerPlayer<PlayerObject>> lst = players.stream().map(server::getEaglerPlayer)
				.filter((e) -> e != null)
				.map(EaglerPlayerInstance<PlayerObject>::getNotificationManagerOrNull)
				.filter((e) -> e != null)
				.collect(ImmutableList.toImmutableList());
		if(lst.size() > 0) {
			return new NotificationManagerMultiPlayers<PlayerObject>(this, lst);
		}else {
			return nopPlayersManager;
		}
	}

	@Override
	public INotificationManager<PlayerObject> getNotificationManagerMultiEagler(
			Collection<IEaglerPlayer<PlayerObject>> players) {
		if(players.size() == 0) {
			return nopPlayersManager;
		}
		Collection<NotificationManagerPlayer<PlayerObject>> lst = players.stream()
				.map((p) -> ((EaglerPlayerInstance<PlayerObject>)p).getNotificationManagerOrNull())
				.filter((e) -> e != null)
				.collect(ImmutableList.toImmutableList());
		if(lst.size() > 0) {
			return new NotificationManagerMultiPlayers<PlayerObject>(this, lst);
		}else {
			return nopPlayersManager;
		}
	}

	@Override
	public void registerNotificationIcon(UUID iconUUID, PacketImageData icon) {
		registeredIconLock.writeLock().lock();
		try {
			registeredIcons.put(iconUUID, icon);
		}finally {
			registeredIconLock.writeLock().unlock();
		}
	}

	@Override
	public void registerNotificationIcons(Collection<IconDef> icons) {
		registeredIconLock.writeLock().lock();
		try {
			for(IconDef def : icons) {
				registeredIcons.put(def.getUUID(), def.getIcon());
			}
		}finally {
			registeredIconLock.writeLock().unlock();
		}
	}

	@Override
	public void unregisterNotificationIcon(UUID iconUUID) {
		registeredIconLock.writeLock().lock();
		try {
			registeredIcons.remove(iconUUID);
		}finally {
			registeredIconLock.writeLock().unlock();
		}
	}

	@Override
	public void unregisterNotificationIcons(Collection<UUID> iconUUIDs) {
		registeredIconLock.writeLock().lock();
		try {
			registeredIcons.keySet().removeAll(iconUUIDs);
		}finally {
			registeredIconLock.writeLock().unlock();
		}
	}

	final Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> getRegisteredIcon(UUID iconUUID) {
		PacketImageData data;
		registeredIconLock.readLock().lock();
		try {
			data = registeredIcons.get(iconUUID);
		}finally {
			registeredIconLock.readLock().unlock();
		}
		if(data != null) {
			return Collections.singleton(new SPacketNotifIconsRegisterV4EAG.CreateIcon(iconUUID.getMostSignificantBits(),
					iconUUID.getLeastSignificantBits(), data));
		}else {
			return Collections.emptyList();
		}
	}

	final Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> getRegisteredIcon2(UUID iconUUID1, UUID iconUUID2) {
		PacketImageData data1;
		PacketImageData data2;
		registeredIconLock.readLock().lock();
		try {
			data1 = iconUUID1 != null ? registeredIcons.get(iconUUID1) : null;
			data2 = iconUUID2 != null ? registeredIcons.get(iconUUID2) : null;
		}finally {
			registeredIconLock.readLock().unlock();
		}
		if(data1 != null) {
			if(data2 != null) {
				return Arrays.asList(
						new SPacketNotifIconsRegisterV4EAG.CreateIcon(iconUUID1.getMostSignificantBits(),
								iconUUID1.getLeastSignificantBits(), data1),
						new SPacketNotifIconsRegisterV4EAG.CreateIcon(iconUUID2.getMostSignificantBits(),
								iconUUID2.getLeastSignificantBits(), data2));
			}else {
				return Collections.singleton(new SPacketNotifIconsRegisterV4EAG.CreateIcon(
						iconUUID1.getMostSignificantBits(), iconUUID1.getLeastSignificantBits(), data1));
			}
		}else if(data2 != null) {
			return Collections.singleton(new SPacketNotifIconsRegisterV4EAG.CreateIcon(
					iconUUID2.getMostSignificantBits(), iconUUID2.getLeastSignificantBits(), data2));
		}else {
			return Collections.emptyList();
		}
	}

	final Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> getRegisteredIcons(Collection<UUID> iconUUID) {
		Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> ret = new ArrayList<>(iconUUID.size());
		registeredIconLock.readLock().lock();
		try {
			for(UUID uuid : iconUUID) {
				PacketImageData data = registeredIcons.get(uuid);
				if(data != null) {
					ret.add(new SPacketNotifIconsRegisterV4EAG.CreateIcon(uuid.getMostSignificantBits(),
							uuid.getLeastSignificantBits(), data));
				}
			}
		}finally {
			registeredIconLock.readLock().unlock();
		}
		return ret;
	}

	@Override
	public void showNotificationBadge(INotificationBuilder<?> builder) {
		allPlayersManager.showNotificationBadge(builder);
	}

	@Override
	public void showNotificationBadge(SPacketNotifBadgeShowV4EAG packet) {
		allPlayersManager.showNotificationBadge(packet);
	}

	@Override
	public PacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height) {
		return PacketImageLoader.loadPacketImageData(pixelsARGB8, width, height);
	}

	@Override
	public PacketImageData loadPacketImageData(BufferedImage bufferedImage, int maxWidth, int maxHeight) {
		return PacketImageLoader.loadPacketImageData(bufferedImage, maxWidth, maxHeight);
	}

	@Override
	public PacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight)
			throws IOException {
		return PacketImageLoader.loadPacketImageData(inputStream, maxWidth, maxHeight);
	}

	@Override
	public PacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException {
		return PacketImageLoader.loadPacketImageData(imageFile, maxWidth, maxHeight);
	}

	public NotificationManagerPlayer<PlayerObject> createPlayerManager(EaglerPlayerInstance<PlayerObject> player) {
		if(player.getEaglerProtocol().ver >= 4) {
			return new NotificationManagerPlayer<PlayerObject>(this, player);
		}else {
			return null;
		}
	}

}
