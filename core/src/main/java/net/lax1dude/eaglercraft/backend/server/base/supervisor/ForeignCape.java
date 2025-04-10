package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.server.util.KeyedConcurrentLazyLoader;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetCapeByURL;

class ForeignCape extends KeyedConcurrentLazyLoader<UUID, IEaglerPlayerCape> {

	protected final SupervisorResolver owner;
	protected final String url;

	protected ForeignCape(SupervisorResolver owner, String url) {
		this.owner = owner;
		this.url = url;
	}

	@Override
	protected void loadImpl(Consumer<IEaglerPlayerCape> callback) {
		SupervisorConnection handler = owner.getConnection();
		if(handler != null) {
			UUID lookupUUID = UUID.randomUUID();
			owner.addWaitingForeignURLCapeLookup(lookupUUID, callback);
			handler.sendSupervisorPacket(new CPacketSvGetCapeByURL(lookupUUID, url));
		}else {
			callback.accept(MissingCape.UNAVAILABLE_CAPE);
		}
	}

	@Override
	public void load(UUID key, Consumer<IEaglerPlayerCape> callback) {
		if(result == MissingCape.UNAVAILABLE_CAPE) {
			result = null;
		}
		super.load(key, callback);
	}

}
