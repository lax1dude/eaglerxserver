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

package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	@Nonnull
	public String getIconName() {
		return icon;
	}

	private static final Map<String, EnumPauseMenuIcon> NAMES_MAP;

	@Nullable
	public static EnumPauseMenuIcon getByName(@Nonnull String name) {
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
