package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public enum EnumPauseMenuIcon {
	ICON_TITLE_L("icon_title_L"),
	ICON_TITLE_R("icon_title_R"),
	ICON_BACK_TO_GAME_L("icon_backToGame_L"),
	ICON_BACK_TO_GAME_R("icon_backToGame_R"),
	ICON_ACHIEVEMENTS_L("icon_achievements_L"),
	ICON_ACHIEVEMENTS_R("icon_achievements_R"),
	ICON_STATISTICS_L("icon_statistics_L"),
	ICON_STATISTICS_R("icon_statistics_R"),
	ICON_SERVER_INFO_L("icon_serverInfo_L"),
	ICON_SERVER_INFO_R("icon_serverInfo_R"),
	ICON_OPTIONS_L("icon_options_L"),
	ICON_OPTIONS_R("icon_options_R"),
	ICON_DISCORD_L("icon_discord_L"),
	ICON_DISCORD_R("icon_discord_R"),
	ICON_DISCONNECT_L("icon_disconnect_L"),
	ICON_DISCONNECT_R("icon_disconnect_R"),
	ICON_BACKGROUND_PAUSE("icon_background_pause"),
	ICON_BACKGROUND_ALL("icon_background_all"),
	ICON_WATERMARK_PAUSE("icon_watermark_pause"),
	ICON_WATERMARK_ALL("icon_watermark_all");

	private final String icon;

	private EnumPauseMenuIcon(String icon) {
		this.icon = icon;
	}

	public String getIconName() {
		return icon;
	}

	private static final Map<String, EnumPauseMenuIcon> NAMES_MAP;

	public static EnumPauseMenuIcon getByName(String name) {
		return NAMES_MAP.get(name);
	}

	static {
		ImmutableMap.Builder<String, EnumPauseMenuIcon> builder = ImmutableMap.builder();
		for(EnumPauseMenuIcon e : values()) {
			builder.put(e.icon, e);
		}
		NAMES_MAP = builder.build();
	}

}
