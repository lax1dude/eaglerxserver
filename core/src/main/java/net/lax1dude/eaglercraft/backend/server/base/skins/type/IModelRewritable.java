package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

public interface IModelRewritable {

	IEaglerPlayerSkin rewriteModelInternal(int modelId);

}
