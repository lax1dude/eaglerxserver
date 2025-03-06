package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.InternUtils;

public class SkinManagerVanillaOffline<PlayerObject> implements ISkinManagerBase<PlayerObject>, ISkinManagerImpl {

	private final BasePlayerInstance<PlayerObject> player;
	private final IEaglerPlayerSkin skin;

	SkinManagerVanillaOffline(BasePlayerInstance<PlayerObject> player, IEaglerPlayerSkin skin) {
		this.player = player;
		this.skin = skin;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public ISkinService<PlayerObject> getSkinService() {
		return player.getEaglerXServer().getSkinService();
	}

	@Override
	public boolean isEaglerPlayer() {
		return false;
	}

	@Override
	public ISkinManagerEagler<PlayerObject> asEaglerPlayer() {
		return null;
	}

	@Override
	public IEaglerPlayerSkin getPlayerSkinIfLoaded() {
		return skin;
	}

	@Override
	public IEaglerPlayerCape getPlayerCapeIfLoaded() {
		return InternUtils.getPresetCape(0);
	}

	@Override
	public void resolvePlayerSkin(Consumer<IEaglerPlayerSkin> callback) {
		callback.accept(getPlayerSkinIfLoaded());
	}

	@Override
	public void resolvePlayerCape(Consumer<IEaglerPlayerCape> callback) {
		callback.accept(getPlayerCapeIfLoaded());
	}

	@Override
	public void resolvePlayerSkinKeyed(UUID requester, Consumer<IEaglerPlayerSkin> callback) {
		callback.accept(getPlayerSkinIfLoaded());
	}

	@Override
	public void resolvePlayerCapeKeyed(UUID requester, Consumer<IEaglerPlayerCape> callback) {
		callback.accept(getPlayerCapeIfLoaded());
	}

}
