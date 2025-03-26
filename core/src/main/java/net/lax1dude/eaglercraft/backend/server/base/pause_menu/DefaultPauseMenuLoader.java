package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.server.api.webview.ITemplateLoader;
import net.lax1dude.eaglercraft.backend.server.api.webview.InvalidMacroException;
import net.lax1dude.eaglercraft.backend.server.base.PacketImageLoader;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataPauseMenu;

class DefaultPauseMenuLoader {

	static ICustomPauseMenu loadDefaultPauseMenu(File dataDir, ConfigDataPauseMenu pauseMenuConf,
			PauseMenuService<?> service) throws IOException, InvalidMacroException {
		IPauseMenuBuilder builder = service.createPauseMenuBuilder();
		if(pauseMenuConf.isEnableServerInfoButton()) {
			if(pauseMenuConf.isServerInfoButtonOpenNewTab()) {
				builder.setServerInfoButtonModeURL(pauseMenuConf.getServerInfoButtonText(),
						pauseMenuConf.getServerInfoButtonEmbedURL());
			}else {
				Set<EnumWebViewPerms> perms = EnumSet.noneOf(EnumWebViewPerms.class);
				if(pauseMenuConf.isServerInfoButtonEmbedEnableJavascript()) {
					perms.add(EnumWebViewPerms.JAVASCRIPT);
				}
				if(pauseMenuConf.isServerInfoButtonEmbedEnableMessageAPI()) {
					perms.add(EnumWebViewPerms.MESSAGE_API);
				}
				if(pauseMenuConf.isServerInfoButtonEmbedEnableStrictCSP()) {
					perms.add(EnumWebViewPerms.STRICT_CSP);
				}
				if(pauseMenuConf.isServerInfoButtonModeEmbedFile()) {
					ITemplateLoader loader = service.getWebViewService().createTemplateLoader(dataDir,
							pauseMenuConf.isServerInfoButtonEmbedEnableTemplateMacros());
					builder.setServerInfoButtonModeWebViewBlob(pauseMenuConf.getServerInfoButtonText(),
							pauseMenuConf.getServerInfoButtonEmbedScreenTitle(), perms,
							service.getWebViewService().createWebViewBlob(loader.loadWebViewTemplate(
									pauseMenuConf.getServerInfoButtonEmbedFile(), StandardCharsets.UTF_8)));
				} else {
					builder.setServerInfoButtonModeWebViewURL(pauseMenuConf.getServerInfoButtonText(),
							pauseMenuConf.getServerInfoButtonEmbedScreenTitle(), perms,
							pauseMenuConf.getServerInfoButtonEmbedURL());
				}
			}
		}
		if(pauseMenuConf.isDiscordButtonEnable()) {
			builder.setDiscordInviteButtonModeURL(pauseMenuConf.getDiscordButtonText(),
					pauseMenuConf.getDiscordButtonURL());
		}
		for(Map.Entry<String, String> etr : pauseMenuConf.getCustomImages().entrySet()) {
			builder.setMenuIcon(etr.getKey(), PacketImageLoader.loadPacketImageData(
					new File(dataDir, etr.getValue()), 255, 255));
		}
		return builder.buildPauseMenu();
	}

}
