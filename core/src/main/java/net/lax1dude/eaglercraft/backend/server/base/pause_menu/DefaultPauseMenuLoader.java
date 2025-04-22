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

package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms;
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
