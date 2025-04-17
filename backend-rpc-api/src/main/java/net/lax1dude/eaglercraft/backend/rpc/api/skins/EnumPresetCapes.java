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
		for(int i = 0; i < skins.length; ++i) {
			arr[skins[i].presetId] = skins[i];
		}
		VALUES = arr;
	}

}
