package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.ISupervisorResolverImpl;

abstract class MultiSvSkinResolver<SkinManager extends ISkinManagerImpl, PlayerObject> {

	private static final VarHandle COUNT_DOWN_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			COUNT_DOWN_HANDLE = l.findVarHandle(MultiSvSkinResolver.class, "countDownValue", int.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private volatile int countDownValue = 2;

	private final SkinManager skinManager;
	private IEaglerPlayerSkin skin;
	private IEaglerPlayerCape cape;

	protected MultiSvSkinResolver(SkinManager skinManager, ISupervisorResolverImpl lookup, UUID lookupUUID, UUID uuid) {
		this.skinManager = skinManager;
		lookup.resolvePlayerSkinKeyed(uuid, lookupUUID, (res) -> {
			this.skin = res;
			countDown();
		});
		lookup.resolvePlayerCapeKeyed(uuid, lookupUUID, (res) -> {
			this.cape = res;
			countDown();
		});
	}

	private void countDown() {
		if((int)COUNT_DOWN_HANDLE.getAndAdd(this, -1) == 0) {
			onComplete(skinManager, skin, cape);
		}
	}

	protected abstract void onComplete(SkinManager mgr, IEaglerPlayerSkin skin, IEaglerPlayerCape cape);

}