/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.socket.protocol.util;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.*;

public class SkinPacketVersionCache {

	public GameMessagePacket skinPacketV3;
	public GameMessagePacket skinPacketV4;

	public SkinPacketVersionCache(GameMessagePacket skinPacketV3, GameMessagePacket skinPacketV4) {
		this.skinPacketV3 = skinPacketV3;
		this.skinPacketV4 = skinPacketV4;
	}

	public static SkinPacketVersionCache createPreset(long uuidMost, long uuidLeast) {
		long hilo = uuidMost ^ uuidLeast;
		GameMessagePacket pkt = new SPacketOtherSkinPresetEAG(uuidMost, uuidLeast,
				((((int) (hilo >> 32)) ^ (int) hilo) & 1) != 0 ? 1 : 0);
		return new SkinPacketVersionCache(pkt, pkt);
	}

	public static SkinPacketVersionCache createPreset(long uuidMost, long uuidLeast, int presetID) {
		GameMessagePacket pkt = new SPacketOtherSkinPresetEAG(uuidMost, uuidLeast, presetID);
		return new SkinPacketVersionCache(pkt, pkt);
	}

	public static SkinPacketVersionCache createCustomV3(long uuidMost, long uuidLeast, int modelID, byte[] texture) {
		return new SkinPacketVersionCache(new SPacketOtherSkinCustomV3EAG(uuidMost, uuidLeast, modelID, texture), null);
	}

	public static SkinPacketVersionCache createCustomV4(long uuidMost, long uuidLeast, int modelID, byte[] texture) {
		return new SkinPacketVersionCache(null, new SPacketOtherSkinCustomV4EAG(uuidMost, uuidLeast, modelID, texture));
	}

	public GameMessagePacket get(GamePluginMessageProtocol vers) {
		switch (vers) {
		case V3:
			return getV3();
		case V4:
			return getV4();
		default:
			return null;
		}
	}

	public GameMessagePacket get(GamePluginMessageProtocol vers, long rewriteUUIDMost, long rewriteUUIDLeast) {
		switch (vers) {
		case V3:
			return getV3(rewriteUUIDMost, rewriteUUIDLeast);
		case V4:
			return getV4(rewriteUUIDMost, rewriteUUIDLeast);
		default:
			return null;
		}
	}

	public GameMessagePacket get(GamePluginMessageProtocol vers, long rewriteUUIDMost, long rewriteUUIDLeast,
			int rewriteModel) {
		switch (vers) {
		case V3:
			return getV3(rewriteUUIDMost, rewriteUUIDLeast, rewriteModel);
		case V4:
			return getV4(rewriteUUIDMost, rewriteUUIDLeast, rewriteModel);
		default:
			return null;
		}
	}

	public GameMessagePacket getV3() {
		if (skinPacketV3 != null) {
			return skinPacketV3;
		} else {
			if (skinPacketV4 == null) {
				return null;
			}
			return skinPacketV3 = convertToV3(skinPacketV4);
		}
	}

	public GameMessagePacket getV4() {
		if (skinPacketV4 != null) {
			return skinPacketV4;
		} else {
			if (skinPacketV3 == null) {
				return null;
			}
			return skinPacketV4 = convertToV4(skinPacketV3);
		}
	}

	public GameMessagePacket getV3(long rewriteUUIDMost, long rewriteUUIDLeast) {
		if (skinPacketV3 != null) {
			return rewriteUUID(skinPacketV3, rewriteUUIDMost, rewriteUUIDLeast);
		} else {
			if (skinPacketV4 == null) {
				return null;
			}
			return skinPacketV3 = convertToV3RewriteUUID(skinPacketV4, rewriteUUIDMost, rewriteUUIDLeast);
		}
	}

	public GameMessagePacket getV4(long rewriteUUIDMost, long rewriteUUIDLeast) {
		if (skinPacketV4 != null) {
			return rewriteUUID(skinPacketV4, rewriteUUIDMost, rewriteUUIDLeast);
		} else {
			if (skinPacketV3 == null) {
				return null;
			}
			return skinPacketV4 = convertToV4RewriteUUID(skinPacketV3, rewriteUUIDMost, rewriteUUIDLeast);
		}
	}

	public GameMessagePacket getV3(long rewriteUUIDMost, long rewriteUUIDLeast, int rewriteModel) {
		if (skinPacketV3 != null) {
			return rewriteUUIDModel(skinPacketV3, rewriteUUIDMost, rewriteUUIDLeast, rewriteModel);
		} else {
			if (skinPacketV4 == null) {
				return null;
			}
			return skinPacketV3 = convertToV3RewriteUUIDModel(skinPacketV4, rewriteUUIDMost, rewriteUUIDLeast,
					rewriteModel);
		}
	}

	public GameMessagePacket getV4(long rewriteUUIDMost, long rewriteUUIDLeast, int rewriteModel) {
		if (skinPacketV4 != null) {
			return rewriteUUIDModel(skinPacketV4, rewriteUUIDMost, rewriteUUIDLeast, rewriteModel);
		} else {
			if (skinPacketV3 == null) {
				return null;
			}
			return skinPacketV4 = convertToV4RewriteUUIDModel(skinPacketV3, rewriteUUIDMost, rewriteUUIDLeast,
					rewriteModel);
		}
	}

	public GameMessagePacket getForceClientV4() {
		if (skinPacketV4 != null) {
			return convertToForceV4(skinPacketV4);
		} else {
			if (skinPacketV3 == null) {
				return null;
			}
			return convertToForceV4(skinPacketV4 = convertToV4(skinPacketV3));
		}
	}

	public byte[] getV3HandshakeData() {
		GameMessagePacket packetV3 = getV3();
		if (packetV3 instanceof SPacketOtherSkinCustomV3EAG) {
			SPacketOtherSkinCustomV3EAG pkt = (SPacketOtherSkinCustomV3EAG) packetV3;
			byte[] tex = pkt.customSkin;
			byte[] ret = new byte[2 + tex.length];
			ret[0] = (byte) 2;
			ret[1] = (byte) pkt.modelID;
			System.arraycopy(tex, 0, ret, 2, tex.length);
			return ret;
		} else {
			SPacketOtherSkinPresetEAG pkt = (SPacketOtherSkinPresetEAG) packetV3;
			int p = pkt.presetSkin;
			byte[] ret = new byte[5];
			ret[0] = (byte) 1;
			ret[1] = (byte) (p >>> 24);
			ret[2] = (byte) (p >>> 16);
			ret[3] = (byte) (p >>> 8);
			ret[4] = (byte) p;
			return ret;
		}
	}

	public boolean isPreset() {
		if (skinPacketV4 != null) {
			return skinPacketV4 instanceof SPacketOtherSkinPresetEAG;
		} else if (skinPacketV3 != null) {
			return skinPacketV3 instanceof SPacketOtherSkinPresetEAG;
		}
		return false;
	}

	public int getPresetId() {
		if (skinPacketV4 != null) {
			if (skinPacketV4 instanceof SPacketOtherSkinPresetEAG) {
				return ((SPacketOtherSkinPresetEAG) skinPacketV4).presetSkin;
			}
		} else if (skinPacketV3 != null) {
			if (skinPacketV3 instanceof SPacketOtherSkinPresetEAG) {
				return ((SPacketOtherSkinPresetEAG) skinPacketV3).presetSkin;
			}
		}
		return -1;
	}

	public int getModelId() {
		if (skinPacketV4 != null) {
			if (skinPacketV4 instanceof SPacketOtherSkinCustomV4EAG) {
				return ((SPacketOtherSkinCustomV4EAG) skinPacketV4).modelID;
			}
		} else if (skinPacketV3 != null) {
			if (skinPacketV3 instanceof SPacketOtherSkinCustomV3EAG) {
				return ((SPacketOtherSkinCustomV3EAG) skinPacketV3).modelID;
			}
		}
		return -1;
	}

	public static GameMessagePacket rewriteUUID(GameMessagePacket pkt, long uuidMost, long uuidLeast) {
		if (pkt instanceof SPacketOtherSkinPresetEAG) {
			return new SPacketOtherSkinPresetEAG(uuidMost, uuidLeast, ((SPacketOtherSkinPresetEAG) pkt).presetSkin);
		} else if (pkt instanceof SPacketOtherSkinCustomV4EAG) {
			SPacketOtherSkinCustomV4EAG pkt2 = (SPacketOtherSkinCustomV4EAG) pkt;
			return new SPacketOtherSkinCustomV4EAG(uuidMost, uuidLeast, pkt2.modelID, pkt2.customSkin);
		} else if (pkt instanceof SPacketOtherSkinCustomV3EAG) {
			SPacketOtherSkinCustomV3EAG pkt2 = (SPacketOtherSkinCustomV3EAG) pkt;
			return new SPacketOtherSkinCustomV3EAG(uuidMost, uuidLeast, pkt2.modelID, pkt2.customSkin);
		} else {
			return pkt;
		}
	}

	public static GameMessagePacket rewriteUUIDModel(GameMessagePacket pkt, long uuidMost, long uuidLeast,
			int modelID) {
		if (pkt instanceof SPacketOtherSkinPresetEAG) {
			return new SPacketOtherSkinPresetEAG(uuidMost, uuidLeast, ((SPacketOtherSkinPresetEAG) pkt).presetSkin);
		} else if (pkt instanceof SPacketOtherSkinCustomV4EAG) {
			SPacketOtherSkinCustomV4EAG pkt2 = (SPacketOtherSkinCustomV4EAG) pkt;
			return new SPacketOtherSkinCustomV4EAG(uuidMost, uuidLeast, modelID, pkt2.customSkin);
		} else if (pkt instanceof SPacketOtherSkinCustomV3EAG) {
			SPacketOtherSkinCustomV3EAG pkt2 = (SPacketOtherSkinCustomV3EAG) pkt;
			return new SPacketOtherSkinCustomV3EAG(uuidMost, uuidLeast, modelID, pkt2.customSkin);
		} else {
			return pkt;
		}
	}

	public static byte[] convertToV3Raw(byte[] v4data) {
		byte[] v3data = new byte[16384];
		for (int i = 0, j, k; i < 4096; ++i) {
			j = i * 3;
			k = i << 2;
			v3data[k + 1] = v4data[j];
			v3data[k + 2] = v4data[j + 1];
			v3data[k + 3] = (byte) ((v4data[j + 2] & 0x7F) << 1);
			v3data[k] = (v4data[j + 2] & 0x80) != 0 ? (byte) 0xFF : (byte) 0;
		}
		return v3data;
	}

	public static byte[] convertToV4Raw(byte[] v3data) {
		byte[] v4data = new byte[12288];
		for (int i = 0, j, k; i < 4096; ++i) {
			j = i << 2;
			k = i * 3;
			v4data[k] = v3data[j + 1];
			v4data[k + 1] = v3data[j + 2];
			v4data[k + 2] = (byte) (((v3data[j + 3] & 0xFF) >>> 1) | (v3data[j] & 0x80));
		}
		return v4data;
	}

	public static GameMessagePacket convertToV3(GameMessagePacket v4pkt) {
		if (v4pkt instanceof SPacketOtherSkinCustomV4EAG) {
			SPacketOtherSkinCustomV4EAG pkt = (SPacketOtherSkinCustomV4EAG) v4pkt;
			return new SPacketOtherSkinCustomV3EAG(pkt.uuidMost, pkt.uuidLeast, pkt.modelID,
					convertToV3Raw(pkt.customSkin));
		} else {
			return v4pkt;
		}
	}

	public static GameMessagePacket convertToV4(GameMessagePacket v3pkt) {
		if (v3pkt instanceof SPacketOtherSkinCustomV3EAG) {
			SPacketOtherSkinCustomV3EAG pkt = (SPacketOtherSkinCustomV3EAG) v3pkt;
			return new SPacketOtherSkinCustomV4EAG(pkt.uuidMost, pkt.uuidLeast, pkt.modelID,
					convertToV4Raw(pkt.customSkin));
		} else {
			return v3pkt;
		}
	}

	public static GameMessagePacket convertToV3RewriteUUID(GameMessagePacket v4pkt, long uuidMost, long uuidLeast) {
		if (v4pkt instanceof SPacketOtherSkinCustomV4EAG) {
			SPacketOtherSkinCustomV4EAG pkt = (SPacketOtherSkinCustomV4EAG) v4pkt;
			return new SPacketOtherSkinCustomV3EAG(uuidMost, uuidLeast, pkt.modelID, convertToV3Raw(pkt.customSkin));
		} else {
			return v4pkt;
		}
	}

	public static GameMessagePacket convertToV4RewriteUUID(GameMessagePacket v3pkt, long uuidMost, long uuidLeast) {
		if (v3pkt instanceof SPacketOtherSkinCustomV3EAG) {
			SPacketOtherSkinCustomV3EAG pkt = (SPacketOtherSkinCustomV3EAG) v3pkt;
			return new SPacketOtherSkinCustomV4EAG(uuidMost, uuidLeast, pkt.modelID, convertToV4Raw(pkt.customSkin));
		} else {
			return v3pkt;
		}
	}

	public static GameMessagePacket convertToV3RewriteUUIDModel(GameMessagePacket v4pkt, long uuidMost, long uuidLeast,
			int modelID) {
		if (v4pkt instanceof SPacketOtherSkinCustomV4EAG) {
			SPacketOtherSkinCustomV4EAG pkt = (SPacketOtherSkinCustomV4EAG) v4pkt;
			return new SPacketOtherSkinCustomV3EAG(uuidMost, uuidLeast, modelID, convertToV3Raw(pkt.customSkin));
		} else {
			return v4pkt;
		}
	}

	public static GameMessagePacket convertToV4RewriteUUIDModel(GameMessagePacket v3pkt, long uuidMost, long uuidLeast,
			int modelID) {
		if (v3pkt instanceof SPacketOtherSkinCustomV3EAG) {
			SPacketOtherSkinCustomV3EAG pkt = (SPacketOtherSkinCustomV3EAG) v3pkt;
			return new SPacketOtherSkinCustomV4EAG(uuidMost, uuidLeast, modelID, convertToV4Raw(pkt.customSkin));
		} else {
			return v3pkt;
		}
	}

	public static SkinPacketVersionCache rewriteUUID(SkinPacketVersionCache pkt, long uuidMost, long uuidLeast) {
		GameMessagePacket rv3 = null;
		GameMessagePacket rv4 = null;
		if (pkt.skinPacketV3 != null) {
			if (pkt.skinPacketV3 instanceof SPacketOtherSkinCustomV3EAG) {
				SPacketOtherSkinCustomV3EAG pkt2 = (SPacketOtherSkinCustomV3EAG) pkt.skinPacketV3;
				rv3 = new SPacketOtherSkinCustomV3EAG(uuidMost, uuidLeast, pkt2.modelID, pkt2.customSkin);
			} else {
				rv3 = pkt.skinPacketV3;
			}
		}
		if (pkt.skinPacketV4 != null) {
			if (pkt.skinPacketV4 instanceof SPacketOtherSkinCustomV4EAG) {
				SPacketOtherSkinCustomV4EAG pkt2 = (SPacketOtherSkinCustomV4EAG) pkt.skinPacketV4;
				rv4 = new SPacketOtherSkinCustomV4EAG(uuidMost, uuidLeast, pkt2.modelID, pkt2.customSkin);
			} else {
				rv4 = pkt.skinPacketV4;
			}
		}
		return new SkinPacketVersionCache(rv3, rv4);
	}

	public static SkinPacketVersionCache rewriteUUIDModel(SkinPacketVersionCache pkt, long uuidMost, long uuidLeast,
			int model) {
		GameMessagePacket rv3 = null;
		GameMessagePacket rv4 = null;
		if (pkt.skinPacketV3 != null) {
			if (pkt.skinPacketV3 instanceof SPacketOtherSkinCustomV3EAG) {
				SPacketOtherSkinCustomV3EAG pkt2 = (SPacketOtherSkinCustomV3EAG) pkt.skinPacketV3;
				rv3 = new SPacketOtherSkinCustomV3EAG(uuidMost, uuidLeast, model, pkt2.customSkin);
			} else {
				rv3 = pkt.skinPacketV3;
			}
		}
		if (pkt.skinPacketV4 != null) {
			if (pkt.skinPacketV4 instanceof SPacketOtherSkinCustomV4EAG) {
				SPacketOtherSkinCustomV4EAG pkt2 = (SPacketOtherSkinCustomV4EAG) pkt.skinPacketV4;
				rv4 = new SPacketOtherSkinCustomV4EAG(uuidMost, uuidLeast, model, pkt2.customSkin);
			} else {
				rv4 = pkt.skinPacketV4;
			}
		}
		return new SkinPacketVersionCache(rv3, rv4);
	}

	public static GameMessagePacket convertToForceV4(GameMessagePacket v4pkt) {
		if (v4pkt instanceof SPacketOtherSkinCustomV4EAG) {
			SPacketOtherSkinCustomV4EAG pkt = (SPacketOtherSkinCustomV4EAG) v4pkt;
			return new SPacketForceClientSkinCustomV4EAG(pkt.modelID, pkt.customSkin);
		} else if (v4pkt instanceof SPacketOtherSkinPresetEAG) {
			return new SPacketForceClientSkinPresetV4EAG(((SPacketOtherSkinPresetEAG) v4pkt).presetSkin);
		} else {
			return v4pkt;
		}
	}

}
