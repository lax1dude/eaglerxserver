package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingSkin;
import net.lax1dude.eaglercraft.backend.server.util.KeyedConcurrentLazyLoader;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetSkinByURL;

class ForeignSkin extends KeyedConcurrentLazyLoader<UUID, IEaglerPlayerSkin> {

	protected final SupervisorResolver owner;
	protected int skinModel = -1;
	protected final String url;

	protected ForeignSkin(SupervisorResolver owner, String url) {
		this.owner = owner;
		this.url = url;
	}

	protected ForeignSkin(SupervisorResolver owner, IEaglerPlayerSkin data, int modelId) {
		this.owner = owner;
		this.url = null;
		this.skinModel = modelId;
		this.result = data;
	}

	@Override
	protected void loadImpl(Consumer<IEaglerPlayerSkin> callback) {
		SupervisorConnection handler = owner.getConnection();
		if(handler != null) {
			UUID lookupUUID = UUID.randomUUID();
			owner.addWaitingForeignURLSkinLookup(lookupUUID, callback);
			handler.sendSupervisorPacket(new CPacketSvGetSkinByURL(lookupUUID, skinModel, url));
		}else {
			owner.addDeferred((fail) -> {
				if(fail) {
					callback.accept(MissingSkin.UNAVAILABLE_SKIN);
				}else {
					loadImpl(callback);
				}
			});
		}
	}

	void load(int modelId, UUID key, Consumer<IEaglerPlayerSkin> callback) {
		this.skinModel = modelId;
		cmpXchgRelease(MissingSkin.UNAVAILABLE_SKIN, null);
		this.load(key, callback);
	}

}
