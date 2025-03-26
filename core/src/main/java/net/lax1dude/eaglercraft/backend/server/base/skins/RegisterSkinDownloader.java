package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;

class RegisterSkinDownloader {

	private final SkinService<?> skinService;
	private final EaglerPlayerInstance<?> player;
	private final RegisterSkinDelegate state;
	private final BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> onComplete;

	private volatile IEaglerPlayerSkin skinResult;
	private volatile IEaglerPlayerCape capeResult;

	private AtomicInteger countDown = new AtomicInteger(2);

	RegisterSkinDownloader(SkinService<?> skinService, EaglerPlayerInstance<?> player, RegisterSkinDelegate state,
			BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> onComplete) {
		this.skinService = skinService;
		this.player = player;
		this.state = state;
		this.onComplete = onComplete;
	}

	public void run() {
		if(state.skinURL != null) {
			skinService.loadPlayerSkinFromURL(state.skinURL, player.getUniqueId(),
					state.skinModel != null ? state.skinModel : EnumSkinModel.STEVE, (skin) -> {
				if (skin.isSuccess()) {
					skinResult = skin;
				}else {
					skinResult = state.skinOriginal;
				}
				countDown();
			});
		}else {
			skinResult = state.skin != null ? state.skin : state.skinOriginal;
			countDown();
		}
		if(state.capeURL != null) {
			skinService.loadPlayerCapeFromURL(state.capeURL, player.getUniqueId(), (cape) -> {
				if(cape.isSuccess()) {
					capeResult = cape;
				}else {
					capeResult = state.capeOriginal;
				}
				countDown();
			});
		}else {
			capeResult = state.cape != null ? state.cape : state.capeOriginal;
			countDown();
		}
	}

	private void countDown() {
		if(countDown.decrementAndGet() == 0) {
			state.handleComplete(player, skinResult, capeResult, onComplete);
		}
	}

}
