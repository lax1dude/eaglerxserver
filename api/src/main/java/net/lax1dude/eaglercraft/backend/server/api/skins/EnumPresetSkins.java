package net.lax1dude.eaglercraft.backend.server.api.skins;

public enum EnumPresetSkins {
	DEFAULT_STEVE(0, "Default Steve"),
	DEFAULT_ALEX(1, "Default Alex"),
	TENNIS_STEVE(2, "Tennis Steve"),
	TENNIS_ALEX(3, "Tennis Alex"),
	TUXEDO_STEVE(4, "Tuxedo Steve"),
	TUXEDO_ALEX(5, "Tuxedo Alex"),
	ATHLETE_STEVE(6, "Athlete Steve"),
	ATHLETE_ALEX(7, "Athlete Alex"),
	CYCLIST_STEVE(8, "Cyclist Steve"),
	CYCLIST_ALEX(9, "Cyclist Alex"),
	BOXER_STEVE(10, "Boxer Steve"),
	BOXER_ALEX(11, "Boxer Alex"),
	PRISONER_STEVE(12, "Prisoner Steve"),
	PRISONER_ALEX(13, "Prisoner Alex"),
	SCOTTISH_STEVE(14, "Scottish Steve"),
	SCOTTISH_ALEX(15, "Scottish Alex"),
	DEVELOPER_STEVE(16, "Developer Steve"),
	DEVELOPER_ALEX(17, "Developer Alex"),
	HEROBRINE(18, "Herobrine"),
	NOTCH(19, "Notch"),
	CREEPER(20, "Creeper"),
	ZOMBIE(21, "Zombie"),
	PIG(22, "Pig"),
	MOOSHROOM(23, "Mooshroom"),
	LONG_ARMS(24, "Long Arms"),
	WEIRD_CLIMBER_DUDE(25, "Weird Climber Dude"),
	LAXATIVE_DUDE(26, "Laxative Dude"),
	BABY_CHARLES(27, "Baby Charles"),
	BABY_WINSTON(28, "Baby Winston");

	private final int presetId;
	private final String presentName;

	private EnumPresetSkins(int id, String name) {
		this.presetId = id;
		this.presentName = name;
	}

	public int getId() {
		return presetId;
	}

	public String getName() {
		return presentName;
	}

	private static final EnumPresetSkins[] VALUES;

	public static EnumPresetSkins getById(int id) {
		return id >= 0 && id < VALUES.length ? VALUES[id] : null;
	}

	static {
		EnumPresetSkins[] skins = values();
		EnumPresetSkins[] arr = new EnumPresetSkins[32];
		for(int i = 0; i < skins.length; ++i) {
			arr[skins[i].presetId] = skins[i];
		}
		VALUES = arr;
	}

}
