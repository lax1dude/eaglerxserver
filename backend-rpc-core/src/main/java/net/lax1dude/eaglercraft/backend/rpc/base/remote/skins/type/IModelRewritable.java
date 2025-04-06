package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

public interface IModelRewritable {

	IEaglerPlayerSkin rewriteModelInternal(int modelId);

}
