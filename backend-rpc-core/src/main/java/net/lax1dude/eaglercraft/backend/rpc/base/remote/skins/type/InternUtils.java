package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public class InternUtils {

	private static final int POOL_SIZE = 32;

	private static final PresetSkinGeneric[] presetSkinPool = new PresetSkinGeneric[POOL_SIZE];
	private static final Interner<PresetSkinGeneric> presetSkinInterner = Interners.newWeakInterner();
	private static final PresetCapeGeneric[] presetCapePool = new PresetCapeGeneric[POOL_SIZE];
	private static final Interner<PresetCapeGeneric> presetCapeInterner = Interners.newWeakInterner();

	static {
		for(int i = 0; i < POOL_SIZE; ++i) {
			presetSkinPool[i] = presetSkinInterner.intern(new PresetSkinGeneric(i));
			presetCapePool[i] = presetCapeInterner.intern(new PresetCapeGeneric(i));
		}
	}

	public static PresetSkinGeneric getPresetSkin(int presetId) {
		if(presetId >= 0 && presetId < POOL_SIZE) {
			return presetSkinPool[presetId];
		}else {
			return presetSkinInterner.intern(new PresetSkinGeneric(presetId));
		}
	}

	public static PresetCapeGeneric getPresetCape(int presetId) {
		if(presetId >= 0 && presetId < POOL_SIZE) {
			return presetCapePool[presetId];
		}else {
			return presetCapeInterner.intern(new PresetCapeGeneric(presetId));
		}
	}

}
