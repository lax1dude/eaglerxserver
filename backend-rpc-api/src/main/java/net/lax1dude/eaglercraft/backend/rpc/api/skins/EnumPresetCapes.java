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

package net.lax1dude.eaglercraft.backend.rpc.api.skins;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum EnumPresetCapes {
	NO_CAPE(0, "No Cape"),
	MINECON_2011(1, "Minecon 2011"),
	MINECON_2012(2, "Minecon 2012"),
	MINECON_2013(3, "Minecon 2013"),
	MINECON_2015(4, "Minecon 2015"),
	MINECON_2016(5, "Minecon 2016"),
	MICROSOFT_ACCOUNT(6, "Microsoft Account"),
	MAPMAKER(7, "Realms Mapmaker"),
	MOJANG_OLD(8, "Mojang Old"),
	MOJANG_NEW(9, "Mojang New"),
	JIRA_MOD(10, "Jira Moderator"),
	MOJANG_VERY_OLD(11, "Mojang Very Old"),
	SCROLLS(12, "Scrolls"),
	COBALT(13, "Cobalt"),
	TRANSLATOR(14, "Lang Translator"),
	MILLIONTH_ACCOUNT(15, "Millionth Player"),
	PRISMARINE(16, "Prismarine"),
	SNOWMAN(17, "Snowman"),
	SPADE(18, "Spade"),
	BIRTHDAY(19, "Birthday"),
	DB(20, "dB"),
	_15TH_ANNIVERSARY(21, "15th Anniversary"),
	VANILLA(22, "Vanilla"),
	TIKTOK(23, "TikTok"),
	PURPLE_HEART(24, "Purple Heart"),
	CHERRY_BLOSSOM(25, "Cherry Blossom");

	private final int presetId;
	private final String presentName;

	private EnumPresetCapes(int id, String name) {
		this.presetId = id;
		this.presentName = name;
	}

	public int getId() {
		return presetId;
	}

	@Nonnull
	public String getName() {
		return presentName;
	}

	private static final EnumPresetCapes[] VALUES;

	@Nullable
	public static EnumPresetCapes getById(int id) {
		return id >= 0 && id < VALUES.length ? VALUES[id] : null;
	}

	@Nonnull
	public static EnumPresetCapes getByIdOrDefault(int id) {
		EnumPresetCapes ret = getById(id);
		return ret != null ? ret : NO_CAPE;
	}

	static {
		EnumPresetCapes[] skins = values();
		EnumPresetCapes[] arr = new EnumPresetCapes[32];
		for (int i = 0; i < skins.length; ++i) {
			arr[skins[i].presetId] = skins[i];
		}
		VALUES = arr;
	}

}
