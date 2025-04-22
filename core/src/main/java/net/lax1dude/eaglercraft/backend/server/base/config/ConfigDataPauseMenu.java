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

package net.lax1dude.eaglercraft.backend.server.base.config;

import java.util.Map;

public class ConfigDataPauseMenu {

	private final boolean enableCustomPauseMenu;
	private final boolean enableServerInfoButton;
	private final String serverInfoButtonText;
	private final boolean serverInfoButtonOpenNewTab;
	private final String serverInfoButtonEmbedURL;
	private final boolean serverInfoButtonModeEmbedFile;
	private final String serverInfoButtonEmbedFile;
	private final String serverInfoButtonEmbedScreenTitle;
	private final int serverInfoButtonEmbedSendChunkRate;
	private final int serverInfoButtonEmbedSendChunkSize;
	private final boolean serverInfoButtonEmbedEnableTemplateMacros;
	private final Map<String, String> serverInfoButtonEmbedTemplateGlobals;
	private final boolean serverInfoButtonEmbedAllowTemplateEvalMacro;
	private final boolean serverInfoButtonEmbedEnableJavascript;
	private final boolean serverInfoButtonEmbedEnableMessageAPI;
	private final boolean serverInfoButtonEmbedEnableStrictCSP;
	private final boolean discordButtonEnable;
	private final String discordButtonText;
	private final String discordButtonURL;
	private final Map<String, String> customImages;

	public ConfigDataPauseMenu(boolean enableCustomPauseMenu, boolean enableServerInfoButton,
			String serverInfoButtonText, boolean serverInfoButtonOpenNewTab, String serverInfoButtonEmbedURL,
			boolean serverInfoButtonModeEmbedFile, String serverInfoButtonEmbedFile,
			String serverInfoButtonEmbedScreenTitle, int serverInfoButtonEmbedSendChunkRate,
			int serverInfoButtonEmbedSendChunkSize, boolean serverInfoButtonEmbedEnableTemplateMacros,
			Map<String, String> serverInfoButtonEmbedTemplateGlobals,
			boolean serverInfoButtonEmbedAllowTemplateEvalMacro, boolean serverInfoButtonEmbedEnableJavascript,
			boolean serverInfoButtonEmbedEnableMessageAPI, boolean serverInfoButtonEmbedEnableStrictCSP,
			boolean discordButtonEnable, String discordButtonText, String discordButtonURL,
			Map<String, String> customImages) {
		this.enableCustomPauseMenu = enableCustomPauseMenu;
		this.enableServerInfoButton = enableServerInfoButton;
		this.serverInfoButtonText = serverInfoButtonText;
		this.serverInfoButtonOpenNewTab = serverInfoButtonOpenNewTab;
		this.serverInfoButtonEmbedURL = serverInfoButtonEmbedURL;
		this.serverInfoButtonModeEmbedFile = serverInfoButtonModeEmbedFile;
		this.serverInfoButtonEmbedFile = serverInfoButtonEmbedFile;
		this.serverInfoButtonEmbedScreenTitle = serverInfoButtonEmbedScreenTitle;
		this.serverInfoButtonEmbedSendChunkRate = serverInfoButtonEmbedSendChunkRate;
		this.serverInfoButtonEmbedSendChunkSize = serverInfoButtonEmbedSendChunkSize;
		this.serverInfoButtonEmbedEnableTemplateMacros = serverInfoButtonEmbedEnableTemplateMacros;
		this.serverInfoButtonEmbedTemplateGlobals = serverInfoButtonEmbedTemplateGlobals;
		this.serverInfoButtonEmbedAllowTemplateEvalMacro = serverInfoButtonEmbedAllowTemplateEvalMacro;
		this.serverInfoButtonEmbedEnableJavascript = serverInfoButtonEmbedEnableJavascript;
		this.serverInfoButtonEmbedEnableMessageAPI = serverInfoButtonEmbedEnableMessageAPI;
		this.serverInfoButtonEmbedEnableStrictCSP = serverInfoButtonEmbedEnableStrictCSP;
		this.discordButtonEnable = discordButtonEnable;
		this.discordButtonText = discordButtonText;
		this.discordButtonURL = discordButtonURL;
		this.customImages = customImages;
	}

	public boolean isEnableCustomPauseMenu() {
		return enableCustomPauseMenu;
	}

	public boolean isEnableServerInfoButton() {
		return enableServerInfoButton;
	}

	public String getServerInfoButtonText() {
		return serverInfoButtonText;
	}

	public boolean isServerInfoButtonOpenNewTab() {
		return serverInfoButtonOpenNewTab;
	}

	public String getServerInfoButtonEmbedURL() {
		return serverInfoButtonEmbedURL;
	}

	public boolean isServerInfoButtonModeEmbedFile() {
		return serverInfoButtonModeEmbedFile;
	}

	public String getServerInfoButtonEmbedFile() {
		return serverInfoButtonEmbedFile;
	}

	public String getServerInfoButtonEmbedScreenTitle() {
		return serverInfoButtonEmbedScreenTitle;
	}

	public int getServerInfoButtonEmbedSendChunkRate() {
		return serverInfoButtonEmbedSendChunkRate;
	}

	public int getServerInfoButtonEmbedSendChunkSize() {
		return serverInfoButtonEmbedSendChunkSize;
	}

	public boolean isServerInfoButtonEmbedEnableTemplateMacros() {
		return serverInfoButtonEmbedEnableTemplateMacros;
	}

	public Map<String, String> getServerInfoButtonEmbedTemplateGlobals() {
		return serverInfoButtonEmbedTemplateGlobals;
	}

	public boolean isServerInfoButtonEmbedAllowTemplateEvalMacro() {
		return serverInfoButtonEmbedAllowTemplateEvalMacro;
	}

	public boolean isServerInfoButtonEmbedEnableJavascript() {
		return serverInfoButtonEmbedEnableJavascript;
	}

	public boolean isServerInfoButtonEmbedEnableMessageAPI() {
		return serverInfoButtonEmbedEnableMessageAPI;
	}

	public boolean isServerInfoButtonEmbedEnableStrictCSP() {
		return serverInfoButtonEmbedEnableStrictCSP;
	}

	public boolean isDiscordButtonEnable() {
		return discordButtonEnable;
	}

	public String getDiscordButtonText() {
		return discordButtonText;
	}

	public String getDiscordButtonURL() {
		return discordButtonURL;
	}

	public Map<String, String> getCustomImages() {
		return customImages;
	}

}
