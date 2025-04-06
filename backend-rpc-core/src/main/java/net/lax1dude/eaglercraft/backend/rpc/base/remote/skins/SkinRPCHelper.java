package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.InternUtils;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.MissingSkin;

public class SkinRPCHelper {

	public static byte[] encodeSkinData(IEaglerPlayerSkin skin) {
		return encodeSkinData(skin, false);
	}

	public static byte[] encodeSkinData(IEaglerPlayerSkin skin, boolean legacy) {
		if(skin.isSkinPreset()) {
			int preset = skin.getPresetSkinId();
			return new byte[] { (byte) 1, (byte)(preset >>> 24), (byte)(preset >>> 16), (byte)(preset >>> 8), (byte)(preset & 0xFF) };
		}else if(legacy) {
			byte[] ret = new byte[2 + 16384];
			ret[0] = 2;
			ret[1] = (byte) skin.getCustomSkinModelId().getId();
			skin.getCustomSkinPixels_RGBA8_64x64(ret, 2);
			return ret;
		}else {
			byte[] ret = new byte[2 + 12288];
			ret[0] = 2;
			ret[1] = (byte) skin.getCustomSkinModelId().getId();
			skin.getCustomSkinPixels_eagler(ret, 2);
			return ret;
		}
	}

	public static byte[] encodeCapeData(IEaglerPlayerCape cape) {
		if(cape.isCapePreset()) {
			int preset = cape.getPresetCapeId();
			return new byte[] { (byte) 1, (byte)(preset >>> 24), (byte)(preset >>> 16), (byte)(preset >>> 8), (byte)(preset & 0xFF) };
		}else {
			byte[] ret = new byte[1 + 1173];
			ret[0] = 2;
			cape.getCustomCapePixels_eagler(ret, 1);
			return ret;
		}
	}

	public static byte[] encodeTexturesData(IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
		int skinLen;
		int capeLen;
		if(!skin.isSuccess()) {
			skinLen = 1;
		}else if(skin.isSkinPreset()) {
			skinLen = 1 + 4;
		}else {
			skinLen = 2 + 12288;
		}
		if(!cape.isSuccess()) {
			capeLen = 1;
		}else if(cape.isCapePreset()) {
			capeLen = 1 + 4;
		}else {
			capeLen = 1 + 1173;
		}
		byte[] ret = new byte[skinLen + capeLen];
		if(skinLen == 1) {
			ret[0] = (byte) -1;
		}else if(skinLen == 1 + 4) {
			int preset = skin.getPresetSkinId();
			ret[0] = (byte) 1;
			ret[1] = (byte)(preset >>> 24);
			ret[2] = (byte)(preset >>> 16);
			ret[3] = (byte)(preset >>> 8);
			ret[4] = (byte)(preset & 0xFF);
		}else if(skinLen == 2 + 12288) {
			ret[0] = 2;
			ret[1] = (byte) skin.getCustomSkinModelId().getId();
			skin.getCustomSkinPixels_eagler(ret, 2);
		}
		if(capeLen == 1) {
			ret[skinLen] = (byte) -1;
		}else if(capeLen == 1 + 4) {
			int preset = cape.getPresetCapeId();
			ret[skinLen] = (byte) 1;
			ret[skinLen + 1] = (byte)(preset >>> 24);
			ret[skinLen + 2] = (byte)(preset >>> 16);
			ret[skinLen + 3] = (byte)(preset >>> 8);
			ret[skinLen + 4] = (byte)(preset & 0xFF);
		}else if(capeLen == 1 + 1173) {
			ret[skinLen] = 2;
			cape.getCustomCapePixels_eagler(ret, skinLen + 1);
		}
		return ret;
	}

	public static IEaglerPlayerSkin decodeSkinData(byte[] data, boolean legacy) {
		int len = data.length;
		if(!legacy && len == 1) {
			if(data[0] == (byte) -1) {
				return MissingSkin.MISSING_SKIN;
			}
		}else if(len == 1 + 4) {
			if(data[0] == (byte) 1) {
				return InternUtils.getPresetSkin(((data[1] & 0xFF) << 24) | ((data[2] & 0xFF) << 16) | ((data[3] & 0xFF) << 8) | (data[4] & 0xFF));
			}
		}else {
			if(legacy) {
				if(len == 2 + 16384 && data[0] == (byte) 2) {
					byte[] tmp = new byte[16384];
					System.arraycopy(data, 2, tmp, 0, 16384);
					return SkinImageLoaderImpl.loadSkinImageData64x64(tmp, EnumSkinModel.getById(data[1] & 0xFF));
				}
			}else {
				if(len == 2 + 12288 && data[0] == (byte) 2) {
					byte[] tmp = new byte[12288];
					System.arraycopy(data, 2, tmp, 0, 12288);
					return SkinImageLoaderImpl.loadSkinImageData64x64Eagler(tmp, EnumSkinModel.getById(data[1] & 0xFF));
				}
			}
		}
		return null;
	}

	public static IEaglerPlayerCape decodeCapeData(byte[] data, boolean legacy) {
		int len = data.length;
		if(!legacy && len == 1) {
			if(data[0] == (byte) -1) {
				return MissingCape.MISSING_CAPE;
			}
		}else if(len == 1 + 4) {
			if(data[0] == (byte) 1) {
				return InternUtils.getPresetCape(((data[1] & 0xFF) << 24) | ((data[2] & 0xFF) << 16) | ((data[3] & 0xFF) << 8) | (data[4] & 0xFF));
			}
		}else {
			if(len == 1 + 1173 && data[0] == (byte) 2) {
				byte[] tmp = new byte[1173];
				System.arraycopy(data, 1, tmp, 0, 1173);
				return SkinImageLoaderImpl.loadCapeImageData23x17Eagler(tmp);
			}
		}
		return null;
	}

	public static IEaglerPlayerSkin decodeTexturesSkinData(byte[] data) {
		int len = data.length;
		if(len == 0) {
			return null;
		}
		byte type = data[0];
		if(type == (byte) -1) {
			return MissingSkin.MISSING_SKIN;
		}else if(type == (byte) 1) {
			if (len > 1 + 4) {
				return InternUtils.getPresetSkin(((data[1] & 0xFF) << 24) | ((data[2] & 0xFF) << 16)
						| ((data[3] & 0xFF) << 8) | (data[4] & 0xFF));
			}
		}else if(type == (byte) 2) {
			if(len > 2 + 12288) {
				byte[] tmp = new byte[12288];
				System.arraycopy(data, 2, tmp, 0, 12288);
				return SkinImageLoaderImpl.loadSkinImageData64x64Eagler(tmp, EnumSkinModel.getById(data[1] & 0xFF));
			}
		}
		return null;
	}

	public static IEaglerPlayerCape decodeTexturesCapeData(byte[] data, IEaglerPlayerSkin decodedSkin) {
		int baseOffset;
		if(!decodedSkin.isSuccess()) {
			baseOffset = 1;
		}else if(decodedSkin.isSkinPreset()) {
			baseOffset = 1 + 4;
		}else {
			baseOffset = 2 + 12288;
		}
		int len = data.length - baseOffset;
		if(len == 1) {
			if(data[baseOffset] == (byte) -1) {
				return MissingCape.MISSING_CAPE;
			}
		}else if(len == 1 + 4) {
			if(data[baseOffset] == (byte) 1) {
				return InternUtils.getPresetCape(((data[baseOffset + 1] & 0xFF) << 24) | ((data[baseOffset + 2] & 0xFF) << 16)
						| ((data[baseOffset + 3] & 0xFF) << 8) | (data[baseOffset + 4] & 0xFF));
			}
		}else {
			if(len == 1 + 1173 && data[baseOffset] == (byte) 2) {
				byte[] tmp = new byte[1173];
				System.arraycopy(data, baseOffset + 1, tmp, 0, 1173);
				return SkinImageLoaderImpl.loadCapeImageData23x17Eagler(tmp);
			}
		}
		return null;
	}

}
