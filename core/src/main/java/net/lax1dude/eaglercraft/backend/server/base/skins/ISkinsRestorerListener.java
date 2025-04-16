package net.lax1dude.eaglercraft.backend.server.base.skins;

import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;

public interface ISkinsRestorerListener<PlayerObject> {

	void handleSRSkinApply(BasePlayerInstance<PlayerObject> player, String value, String signature);

}
