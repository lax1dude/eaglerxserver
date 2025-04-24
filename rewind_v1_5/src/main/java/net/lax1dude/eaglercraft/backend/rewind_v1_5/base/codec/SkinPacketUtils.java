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

package net.lax1dude.eaglercraft.backend.rewind_v1_5.base.codec;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import io.netty.buffer.ByteBuf;

public class SkinPacketUtils {

	private static final int SKIN_TYPE_CUSTOM_LEGACY = 0;
	private static final int SKIN_TYPE_CUSTOM_MODERN_STEVE = 1;
	private static final int SKIN_TYPE_PRESET = 4;
	private static final int SKIN_TYPE_CUSTOM_MODERN_ALEX = 5;

	private static final int CAPE_TYPE_CUSTOM = 0;
	private static final int CAPE_TYPE_PRESET = 2;

	public static byte[] rewriteLegacyHandshakeSkinToV1(ByteBuf data) {
		try {
			int id = data.readUnsignedByte();
			switch(id) {
			case SKIN_TYPE_CUSTOM_LEGACY:
				return rewriteLegacyHandshakeLegacySkinToV1(data);
			case SKIN_TYPE_CUSTOM_MODERN_STEVE:
				return rewriteLegacyHandshakeModernSkinToV1(data, false);
			case SKIN_TYPE_PRESET:
				return rewriteLegacyHandshakePresetSkinToV1(data);
			case SKIN_TYPE_CUSTOM_MODERN_ALEX:
				return rewriteLegacyHandshakeModernSkinToV1(data, true);
			default:
				return null;
			}
		}catch(IndexOutOfBoundsException ex) {
			return null;
		}
	}

	private static final int[] map15to18PresetSkin = new int[] {
			EnumPresetSkins.DEFAULT_STEVE.getId(), //skins/01.default_steve.png
			EnumPresetSkins.DEFAULT_ALEX.getId(), //skins/02.default_alex.png
			EnumPresetSkins.TENNIS_STEVE.getId(), //skins/03.tennis_steve.png
			EnumPresetSkins.TENNIS_ALEX.getId(), //skins/04.tennis_alex.png
			EnumPresetSkins.TUXEDO_STEVE.getId(), //skins/05.tuxedo_steve.png
			EnumPresetSkins.TUXEDO_ALEX.getId(), //skins/06.tuxedo_alex.png
			EnumPresetSkins.ATHLETE_STEVE.getId(), //skins/07.athlete_steve.png
			EnumPresetSkins.ATHLETE_ALEX.getId(), //skins/08.athlete_alex.png
			EnumPresetSkins.CYCLIST_STEVE.getId(), //skins/09.cyclist_steve.png
			EnumPresetSkins.CYCLIST_ALEX.getId(), //skins/10.cyclist_alex.png
			EnumPresetSkins.BOXER_STEVE.getId(), //skins/11.boxer_steve.png
			EnumPresetSkins.BOXER_ALEX.getId(), //skins/12.boxer_alex.png
			EnumPresetSkins.PRISONER_STEVE.getId(), //skins/13.prisoner_steve.png
			EnumPresetSkins.PRISONER_ALEX.getId(), //skins/14.prisoner_alex.png
			EnumPresetSkins.SCOTTISH_STEVE.getId(), //skins/15.scottish_steve.png
			EnumPresetSkins.SCOTTISH_ALEX.getId(), //skins/16.scottish_alex.png
			EnumPresetSkins.DEVELOPER_STEVE.getId(), //skins/17.dev_steve.png
			EnumPresetSkins.DEVELOPER_ALEX.getId(), //skins/18.dev_alex.png
			EnumPresetSkins.HEROBRINE.getId(), //skins/19.herobrine.png
			EnumPresetSkins.DEFAULT_STEVE.getId(), //mob/enderman.png
			EnumPresetSkins.DEFAULT_STEVE.getId(), //mob/skeleton.png
			EnumPresetSkins.DEFAULT_STEVE.getId(), //mob/fire.png
			EnumPresetSkins.DEFAULT_STEVE.getId(), //skins/20.barney.png
			EnumPresetSkins.DEFAULT_STEVE.getId(), //skins/21.slime.png
			EnumPresetSkins.DEFAULT_STEVE.getId(), //skins/22.noob.png
			EnumPresetSkins.DEFAULT_STEVE.getId(), //skins/23.trump.png
			EnumPresetSkins.NOTCH.getId(), //skins/24.notch.png
			EnumPresetSkins.CREEPER.getId(), //skins/25.creeper.png
			EnumPresetSkins.ZOMBIE.getId(), //skins/26.zombie.png
			EnumPresetSkins.PIG.getId(), //skins/27.pig.png
			EnumPresetSkins.DEFAULT_STEVE.getId(), //skins/28.squid.png
			EnumPresetSkins.MOOSHROOM.getId(), //skins/29.mooshroom.png
			EnumPresetSkins.DEFAULT_STEVE.getId(), //mob/villager/villager.png
			EnumPresetSkins.LONG_ARMS.getId(), //LONG_ARMS
			EnumPresetSkins.WEIRD_CLIMBER_DUDE.getId(), //WEIRD_CLIMBER_DUDE
			EnumPresetSkins.LAXATIVE_DUDE.getId(), //LAXATIVE_DUDE
			EnumPresetSkins.BABY_CHARLES.getId(), //BABY_CHARLES
			EnumPresetSkins.BABY_WINSTON.getId() //BABY_WINSTON
	};

	private static final int[] map18to15PresetSkin = invertMap(map15to18PresetSkin);

	private static int[] invertMap(int[] arr) {
		int max = 0;
		for(int i = 0; i < arr.length; ++i) {
			int j = arr[i];
			if(j > max) {
				max = j;
			}
		}
		int[] ret = new int[max + 1];
		for(int i = 0; i < arr.length; ++i) {
			int j = arr[i];
			if(j != 0) {
				ret[j] = i;
			}
		}
		return ret;
	}

	private static int map(int val, int[] table) {
		return val >= 0 && val < table.length ? table[val] : 0;
	}

	private static byte[] rewriteLegacyHandshakePresetSkinToV1(ByteBuf data) {
		int presetSkin = map(data.readUnsignedByte(), map15to18PresetSkin);
		return new byte[] { 0x01, (byte) (presetSkin >>> 24), (byte) (presetSkin >>> 16), (byte) (presetSkin >>> 8),
				(byte) presetSkin };
	}

	private static byte[] rewriteLegacyHandshakeLegacySkinToV1(ByteBuf data) {
		byte[] ret = new byte[2 + 16384];
		ret[0] = 0x02;
		ret[1] = 0x00;
		SkinHandshakeConverter.convertSkin64x32To64x64(data, data.readerIndex(), ret, 2);
		data.skipBytes(8192);
		return ret;
	}

	private static byte[] rewriteLegacyHandshakeModernSkinToV1(ByteBuf data, boolean b) {
		byte[] ret = new byte[2 + 16384];
		ret[0] = 0x02;
		ret[1] = b ? (byte) 0x01 : (byte) 0x00;
		SkinHandshakeConverter.convertSkinPixels(data, data.readerIndex(), ret, 2, 4096);
		return ret;
	}

	public static byte[] rewriteLegacyHandshakeCapeToV1(ByteBuf data) {
		try {
			int id = data.readUnsignedByte();
			data.skipBytes(1); // Skip skin layers, TODO: remap this to 1.8 entity metadata?
			switch(id) {
			case CAPE_TYPE_CUSTOM:
				return rewriteLegacyHandshakeCustomCapeToV1(data);
			case CAPE_TYPE_PRESET:
				return rewriteLegacyHandshakePresetCapeToV1(data);
			default:
				return null;
			}
		}catch(IndexOutOfBoundsException ex) {
			return null;
		}
	}

	private static byte[] rewriteLegacyHandshakePresetCapeToV1(ByteBuf data) {
		return new byte[] { 0x01, 0x00, 0x00, 0x00, data.readByte() };
	}

	private static byte[] rewriteLegacyHandshakeCustomCapeToV1(ByteBuf data) {
		byte[] ret = new byte[1 + 1173];
		ret[0] = 0x02;
		SkinHandshakeConverter.convertCape32x32RGBAto23x17RGB(data, data.readerIndex(), ret, 1);
		data.skipBytes(4096);
		return ret;
	}

	public static int rewritePresetSkinIdToLegacy(int id) {
		if(id >= 0 && id < map18to15PresetSkin.length) {
			return map18to15PresetSkin[id];
		}else {
			return id;
		}
	}

	public static void rewriteCustomSkinToLegacy(byte[] data, ByteBuf dest) {
		if(BufferUtils.LITTLE_ENDIAN_SUPPORT) {
			for(int i = 0, j; i < 4096; ++i) {
				j = i * 3;
				dest.writeIntLE((data[j] & 0xFF) | ((data[j + 1] & 0xFF) << 8) | ((data[j + 2] & 0x7F) << 17)
						| ((data[j + 2] & 0x80) != 0 ? 0xFF000000 : 0));
			}
		}else {
			for(int i = 0, j; i < 4096; ++i) {
				j = i * 3;
				dest.writeInt(((data[j] & 0xFF) << 24) | ((data[j + 1] & 0xFF) << 16) | ((data[j + 2] & 0x7F) << 9)
						| ((data[j + 2] & 0x80) != 0 ? 0x000000FF : 0));
			}
		}
	}

	public static void rewriteCustomCapeToLegacy(byte[] data, ByteBuf dest) {
		int idx = dest.writerIndex();
		dest.writeZero(4096);
		int i, j;
		if(BufferUtils.LITTLE_ENDIAN_SUPPORT) {
			for(int y = 0; y < 17; ++y) {
				for(int x = 0; x < 22; ++x) {
					i = idx + ((y * 32 + x) << 2);
					j = ((y * 23 + x) * 3);
					dest.setIntLE(i, 0xFF000000 | ((data[j + 2] & 0xFF) << 16) | ((data[j + 1] & 0xFF) << 8) | (data[j] & 0xFF));
				}
			}
			for(int y = 0; y < 11; ++y) {
				i = idx + (((y + 11) * 32 + 22) << 2);
				j = (((y + 6) * 23 + 22) * 3);
				dest.setIntLE(i, 0xFF000000 | ((data[j + 2] & 0xFF) << 16) | ((data[j + 1] & 0xFF) << 8) | (data[j] & 0xFF));
			}
		}else {
			for(int y = 0; y < 17; ++y) {
				for(int x = 0; x < 22; ++x) {
					i = idx + ((y * 32 + x) << 2);
					j = ((y * 23 + x) * 3);
					dest.setInt(i, 0x000000FF | ((data[j + 2] & 0xFF) << 8) | ((data[j + 1] & 0xFF) << 16) | ((data[j] & 0xFF) << 24));
				}
			}
			for(int y = 0; y < 11; ++y) {
				i = idx + (((y + 11) * 32 + 22) << 2);
				j = (((y + 6) * 23 + 22) * 3);
				dest.setInt(i, 0x000000FF | ((data[j + 2] & 0xFF) << 8) | ((data[j + 1] & 0xFF) << 16) | ((data[j] & 0xFF) << 24));
			}
		}
	}

}
