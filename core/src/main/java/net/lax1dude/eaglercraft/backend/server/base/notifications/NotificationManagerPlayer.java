package net.lax1dude.eaglercraft.backend.server.base.notifications;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsReleaseV4EAG;

public class NotificationManagerPlayer<PlayerObject> extends NotificationManagerBase<PlayerObject> {

	final EaglerPlayerInstance<PlayerObject> player;
	Set<UUID> knownIcons;

	public NotificationManagerPlayer(NotificationService<PlayerObject> service,
			EaglerPlayerInstance<PlayerObject> player) {
		super(service);
		this.player = player;
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getPlayerList() {
		return Collections.singleton(player);
	}

	@Override
	protected void touchIcon(UUID uuid) {
		if(uuid != null) {
			synchronized(this) {
				if(knownIcons == null) knownIcons = new HashSet<>();
				if(!knownIcons.add(uuid)) {
					uuid = null;
				}
			}
			if(uuid != null) {
				Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> lst = service.getRegisteredIcon(uuid);
				if(lst.size() > 0) {
					player.sendEaglerMessage(new SPacketNotifIconsRegisterV4EAG(lst));
				}
			}
		}
	}

	@Override
	protected void touchIcons(GameMessagePacket packet, UUID uuidA, UUID uuidB) {
		eagler: if(uuidA != null || uuidB != null) {
			synchronized(this) {
				if(knownIcons == null) knownIcons = new HashSet<>();
				if(uuidA != null) {
					if(!knownIcons.add(uuidA)) {
						uuidA = null;
					}
				}
				if(uuidB != null) {
					if(!knownIcons.add(uuidB)) {
						uuidB = null;
					}
				}
			}
			Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> lst;
			if(uuidA != null) {
				if(uuidB != null) {
					lst = service.getRegisteredIcon2(uuidA, uuidB);
				}else {
					lst = service.getRegisteredIcon(uuidA);
				}
			}else if(uuidB != null) {
				lst = service.getRegisteredIcon(uuidB);
			}else {
				break eagler;
			}
			if(lst.size() > 0) {
				player.sendEaglerMessage(new SPacketNotifIconsRegisterV4EAG(lst));
			}
		}
		player.sendEaglerMessage(packet);
	}

	@Override
	protected void touchIcons(Collection<UUID> uuids, Collection<UUID> tmp) {
		tmp.clear();
		Iterator<UUID> itr = uuids.iterator();
		synchronized(this) {
			if(knownIcons == null) knownIcons = new HashSet<>();
			while(itr.hasNext()) {
				UUID uuid = itr.next();
				if(knownIcons.add(uuid)) {
					tmp.add(uuid);
				}
			}
		}
		if(tmp.size() == 0) {
			return;
		}
		Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> lst = service.getRegisteredIcons(tmp);
		if(lst.size() > 0) {
			player.sendEaglerMessage(new SPacketNotifIconsRegisterV4EAG(lst));
		}
	}

	@Override
	protected void releaseIcon(UUID uuid) {
		synchronized(this) {
			if(knownIcons == null || !knownIcons.remove(uuid)) {
				return;
			}
			if(knownIcons.size() == 0) {
				knownIcons = null;
			}
		}
		player.sendEaglerMessage(new SPacketNotifIconsReleaseV4EAG(
				Collections.singleton(new SPacketNotifIconsReleaseV4EAG.DestroyIcon(uuid.getMostSignificantBits(),
						uuid.getLeastSignificantBits()))));
	}

	@Override
	protected void releaseIcons() {
		Collection<UUID> toRelease;
		synchronized(this) {
			toRelease = knownIcons;
			if(toRelease == null) {
				return;
			}
			knownIcons = null;
		}
		int l = toRelease.size();
		SPacketNotifIconsReleaseV4EAG.DestroyIcon[] icns = new SPacketNotifIconsReleaseV4EAG.DestroyIcon[l];
		for(UUID uuid : toRelease) {
			icns[--l] = new SPacketNotifIconsReleaseV4EAG.DestroyIcon(uuid.getMostSignificantBits(),
					uuid.getLeastSignificantBits());
		}
		player.sendEaglerMessage(new SPacketNotifIconsReleaseV4EAG(Arrays.asList(icns)));
	}

	@Override
	protected void releaseIcons(Collection<UUID> uuids, Collection<UUID> tmp) {
		tmp.clear();
		synchronized(this) {
			if(knownIcons == null) {
				return;
			}
			Iterator<UUID> itr = knownIcons.iterator();
			while(itr.hasNext()) {
				UUID n = itr.next();
				if(uuids.contains(n)) {
					tmp.add(n);
					itr.remove();
				}
			}
			if(knownIcons.size() == 0) {
				knownIcons = null;
			}
		}
		int l = tmp.size();
		if(l == 0) {
			return;
		}
		SPacketNotifIconsReleaseV4EAG.DestroyIcon[] icns = new SPacketNotifIconsReleaseV4EAG.DestroyIcon[l];
		for(UUID uuid : tmp) {
			icns[--l] = new SPacketNotifIconsReleaseV4EAG.DestroyIcon(uuid.getMostSignificantBits(),
					uuid.getLeastSignificantBits());
		}
		player.sendEaglerMessage(new SPacketNotifIconsReleaseV4EAG(Arrays.asList(icns)));
	}

	@Override
	protected void sendPacket(GameMessagePacket packet) {
		player.sendEaglerMessage(packet);
	}

}
