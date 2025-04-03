package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

class SkinTypesHelper {

	static IEaglerPlayerSkin wrap(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin skin) {
		return new PlayerSkinLocal(skin);
	}

	static IEaglerPlayerCape wrap(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape) {
		return new PlayerCapeLocal(cape);
	}

	static EnumPresetSkins wrap(net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins skin) {
		return EnumPresetSkins.getByIdOrDefault(skin.getId());
	}

	static EnumPresetCapes wrap(net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes cape) {
		return EnumPresetCapes.getByIdOrDefault(cape.getId());
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin unwrap(IEaglerPlayerSkin skin) {
		return ((PlayerSkinLocal) skin).skin;
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape unwrap(IEaglerPlayerCape cape) {
		return ((PlayerCapeLocal) cape).cape;
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins unwrap(EnumPresetSkins skin) {
		return net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins.getByIdOrDefault(skin.getId());
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes unwrap(EnumPresetCapes cape) {
		return net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes.getByIdOrDefault(cape.getId());
	}

	static EnumEnableFNAW wrap(net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW en) {
		switch(en) {
		case DISABLED:
		default:
			return EnumEnableFNAW.DISABLED;
		case ENABLED:
			return EnumEnableFNAW.ENABLED;
		case FORCED:
			return EnumEnableFNAW.FORCED;
		}
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW unwrap(EnumEnableFNAW en) {
		switch(en) {
		case DISABLED:
		default:
			return net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW.DISABLED;
		case ENABLED:
			return net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW.ENABLED;
		case FORCED:
			return net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW.FORCED;
		}
	}

	static class PlayerSkinLocal implements IEaglerPlayerSkin {

		final net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin skin;

		PlayerSkinLocal(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin skin) {
			this.skin = skin;
		}

		@Override
		public boolean isSuccess() {
			return skin.isSuccess();
		}

		@Override
		public boolean isSkinPreset() {
			return skin.isSkinPreset();
		}

		@Override
		public int getPresetSkinId() {
			return skin.getPresetSkinId();
		}

		@Override
		public EnumPresetSkins getPresetSkin() {
			return EnumPresetSkins.getByIdOrDefault(skin.getPresetSkinId());
		}

		@Override
		public boolean isSkinCustom() {
			return skin.isSkinCustom();
		}

		@Override
		public void getCustomSkinPixels_RGBA8_64x64(byte[] array, int offset) {
			skin.getCustomSkinPixels_RGBA8_64x64(array, offset);
		}

		@Override
		public void getCustomSkinPixels_eagler(byte[] array, int offset) {
			skin.getCustomSkinPixels_eagler(array, offset);
		}

		@Override
		public EnumSkinModel getCustomSkinModelId() {
			return EnumSkinModel.getById(skin.getCustomSkinModelId().getId());
		}

		@Override
		public int hashCode() {
			return skin.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj || ((obj instanceof PlayerSkinLocal) && skin.equals(((PlayerSkinLocal) obj).skin));
		}

	}

	static class PlayerCapeLocal implements IEaglerPlayerCape {

		final net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape;

		PlayerCapeLocal(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape) {
			this.cape = cape;
		}

		@Override
		public boolean isSuccess() {
			return cape.isSuccess();
		}

		@Override
		public boolean isCapeEnabled() {
			return cape.isCapeEnabled();
		}

		@Override
		public boolean isCapePreset() {
			return cape.isCapePreset();
		}

		@Override
		public int getPresetCapeId() {
			return cape.getPresetCapeId();
		}

		@Override
		public EnumPresetCapes getPresetCape() {
			return EnumPresetCapes.getByIdOrDefault(cape.getPresetCapeId());
		}

		@Override
		public boolean isCapeCustom() {
			return cape.isCapeCustom();
		}

		@Override
		public void getCustomCapePixels_RGBA8_32x32(byte[] array, int offset) {
			cape.getCustomCapePixels_RGBA8_32x32(array, offset);
		}

		@Override
		public void getCustomCapePixels_eagler(byte[] array, int offset) {
			cape.getCustomCapePixels_eagler(array, offset);
		}

		@Override
		public int hashCode() {
			return cape.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj || ((obj instanceof PlayerCapeLocal) && cape.equals(((PlayerCapeLocal) obj).cape));
		}

	}

}
