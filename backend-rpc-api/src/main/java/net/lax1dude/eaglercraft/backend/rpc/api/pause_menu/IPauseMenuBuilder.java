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

package net.lax1dude.eaglercraft.backend.rpc.api.pause_menu;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;

public interface IPauseMenuBuilder {

	@Nonnull
	IPauseMenuBuilder copyFrom(@Nonnull IPauseMenuBuilder pauseMenu);

	@Nonnull
	IPauseMenuBuilder copyFrom(@Nonnull ICustomPauseMenu pauseMenu);

	@Nonnull
	EnumServerInfoButton getServerInfoButtonMode();

	@Nonnull
	IPauseMenuBuilder setServerInfoButtonModeNone();

	@Nonnull
	IPauseMenuBuilder setServerInfoButtonModeURL(@Nonnull String text, @Nonnull String url);

	@Nonnull
	IPauseMenuBuilder setServerInfoButtonModeWebViewURL(@Nonnull String text, @Nonnull String title,
			@Nullable Set<EnumWebViewPerms> permissions, @Nonnull String url);

	@Nonnull
	default IPauseMenuBuilder setServerInfoButtonModeWebViewURL(@Nonnull String text, @Nonnull String title,
			@Nonnull String url) {
		return setServerInfoButtonModeWebViewURL(text, title, null, url);
	}

	@Nonnull
	IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(@Nonnull String text, @Nonnull String title,
			@Nullable Set<EnumWebViewPerms> permissions, @Nonnull SHA1Sum blobHash);

	@Nonnull
	default IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(@Nonnull String text, @Nonnull String title,
			@Nonnull SHA1Sum blobHash) {
		return setServerInfoButtonModeWebViewBlob(text, title, null, blobHash);
	}

	@Nonnull
	IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(@Nonnull String text, @Nonnull String title,
			@Nullable Set<EnumWebViewPerms> permissions, @Nonnull String blobAlias);

	@Nonnull
	default IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(@Nonnull String text, @Nonnull String title,
			@Nonnull String blobAlias) {
		return setServerInfoButtonModeWebViewBlob(text, title, null, blobAlias);
	}

	@Nonnull
	IPauseMenuBuilder setServerInfoButtonModeInheritDefault();

	@Nullable
	String getServerInfoButtonText();

	@Nullable
	String getServerInfoButtonURL();

	@Nullable
	String getServerInfoButtonWebViewTitle();

	@Nonnull
	Set<EnumWebViewPerms> getServerInfoButtonWebViewPerms();

	@Nullable
	SHA1Sum getServerInfoButtonBlobHash();

	@Nullable
	String getServerInfoButtonBlobAlias();

	@Nonnull
	EnumDiscordInviteButton getDiscordInviteButtonMode();

	@Nonnull
	IPauseMenuBuilder setDiscordInviteButtonModeNone();

	@Nonnull
	IPauseMenuBuilder setDiscordInviteButtonModeURL(@Nonnull String text, @Nonnull String url);

	@Nonnull
	IPauseMenuBuilder setDiscordInviteButtonModeInheritDefault();

	@Nullable
	String getDiscordInviteButtonText();

	@Nullable
	String getDiscordInviteButtonURL();

	boolean isMenuIconInheritDefault(@Nonnull EnumPauseMenuIcon icon);

	boolean isMenuIconInheritDefault(@Nonnull String icon);

	@Nonnull
	IPacketImageData getMenuIcon(@Nonnull EnumPauseMenuIcon icon);

	@Nonnull
	IPacketImageData getMenuIcon(@Nonnull String icon);

	@Nonnull
	IPauseMenuBuilder setMenuIcon(@Nonnull EnumPauseMenuIcon icon, @Nullable IPacketImageData imageData);

	@Nonnull
	IPauseMenuBuilder setMenuIcon(@Nonnull String icon, @Nullable IPacketImageData imageData);

	@Nonnull
	IPauseMenuBuilder setMenuIconInheritDefault(@Nonnull EnumPauseMenuIcon icon);

	@Nonnull
	IPauseMenuBuilder setMenuIconInheritDefault(@Nonnull String icon);

	@Nonnull
	IPauseMenuBuilder clearMenuIcons();

	boolean isRemoteFeaturesSupported();

	@Nonnull
	ICustomPauseMenu buildPauseMenu();

}
