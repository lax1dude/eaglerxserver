package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.function.BiConsumer;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;

class RegisterSkinDownloader {

	private static final VarHandle COUNT_DOWN_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			COUNT_DOWN_HANDLE = l.findVarHandle(RegisterSkinDownloader.class, "countDownValue", int.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private volatile int countDownValue = 2;

	private final SkinService<?> skinService;
	private final EaglerPlayerInstance<?> player;
	private final RegisterSkinDelegate state;
	private final BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> onComplete;

	private IEaglerPlayerSkin skinResult;
	private IEaglerPlayerCape capeResult;

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
		if((int)COUNT_DOWN_HANDLE.getAndAdd(this, -1) == 0) {
			state.handleComplete(player, skinResult, capeResult, onComplete);
		}
	}

}
