/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomCapePlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomSkinPlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.PresetCapePlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.PresetSkinPlayer;

public class SkinHandshake {

	public static final int PACKET_MY_SKIN_PRESET = 0x01;
	public static final int PACKET_MY_SKIN_CUSTOM = 0x02;
	public static final int PACKET_MY_CAPE_PRESET = 0x01;
	public static final int PACKET_MY_CAPE_CUSTOM = 0x02;

	public static IEaglerPlayerSkin loadSkinDataV1(UUID uuid, byte[] data) {
		if (data != null && data.length > 0) {
			switch((int)data[0] & 0xFF) {
			case PACKET_MY_SKIN_PRESET:
				if(data.length != 5) {
					break;
				}
				return new PresetSkinPlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
						((data[1] & 0x7F) << 24) | ((data[2] & 0xFF) << 16) | ((data[3] & 0xFF) << 8) | (data[4] & 0xFF));
			case PACKET_MY_SKIN_CUSTOM:
				if(data.length != 2 + 16384) {
					break;
				}
				byte[] pixels = new byte[16384];
				System.arraycopy(data, 2, pixels, 0, pixels.length);
				return CustomSkinPlayer.createV3(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
						Math.min((int) data[1] & 0x7F, 0x7E) | 0x80, pixels);
			default:
				break;
			}
		}
		return new PresetSkinPlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
				(uuid.hashCode() & 1) != 0 ? 1 : 0);
	}

	public static IEaglerPlayerSkin loadSkinDataV2(UUID uuid, byte[] data) {
		if (data != null && data.length > 0) {
			switch((int)data[0] & 0xFF) {
			case PACKET_MY_SKIN_PRESET:
				if(data.length != 5) {
					break;
				}
				return new PresetSkinPlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
						((data[1] & 0x7F) << 24) | ((data[2] & 0xFF) << 16) | ((data[3] & 0xFF) << 8) | (data[4] & 0xFF));
			case PACKET_MY_SKIN_CUSTOM:
				if(data.length != 2 + 12288) {
					break;
				}
				byte[] pixels = new byte[12288];
				System.arraycopy(data, 2, pixels, 0, pixels.length);
				return CustomSkinPlayer.createV4(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
						Math.min((int) data[1] & 0x7F, 0x7E) | 0x80, pixels);
			default:
				break;
			}
		}
		return new PresetSkinPlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
				(uuid.hashCode() & 1) != 0 ? 1 : 0);
	}

	public static IEaglerPlayerCape loadCapeDataV1(UUID uuid, byte[] data) {
		if (data != null && data.length > 0) {
			switch((int)data[0] & 0xFF) {
			case PACKET_MY_CAPE_PRESET:
				if(data.length != 5) {
					break;
				}
				return new PresetCapePlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
						((data[1] & 0x7F) << 24) | ((data[2] & 0xFF) << 16) | ((data[3] & 0xFF) << 8) | (data[4] & 0xFF));
			case PACKET_MY_CAPE_CUSTOM:
				if(data.length != 1 + 1173) {
					break;
				}
				byte[] pixels = new byte[1173];
				System.arraycopy(data, 1, pixels, 0, pixels.length);
				return new CustomCapePlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), pixels);
			default:
				break;
			}
		}
		return new PresetCapePlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), 0);
	}

}
