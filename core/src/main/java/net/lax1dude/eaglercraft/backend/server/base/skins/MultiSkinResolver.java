package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

abstract class MultiSkinResolver<SkinManager extends ISkinManagerImpl, PlayerObject> extends AtomicInteger {

	private final SkinManager skinManager;
	private volatile IEaglerPlayerSkin skin;
	private volatile IEaglerPlayerCape cape;

	protected MultiSkinResolver(SkinManager skinManager, IEaglerPlayerSkin skin, IEaglerPlayerCape cape, UUID uuid) {
		super(2);
		this.skinManager = skinManager;
		if(skin == null) {
			skinManager.resolvePlayerSkinKeyed(uuid, (res) -> {
				this.skin = res;
				countDown();
			});
		}else {
			this.skin = skin;
			countDown();
		}
		if(cape == null) {
			skinManager.resolvePlayerCapeKeyed(uuid, (res) -> {
				this.cape = res;
				countDown();
			});
		}else {
			this.cape = cape;
			countDown();
		}
	}

	private void countDown() {
		if(decrementAndGet() == 0) {
			onComplete(skinManager, skin, cape);
		}
	}

	protected abstract void onComplete(SkinManager mgr, IEaglerPlayerSkin skin, IEaglerPlayerCape cape);

}