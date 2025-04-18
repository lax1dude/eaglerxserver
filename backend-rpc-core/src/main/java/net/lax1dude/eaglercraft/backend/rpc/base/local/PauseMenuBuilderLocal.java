package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.EnumDiscordInviteButton;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.EnumPauseMenuIcon;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.EnumServerInfoButton;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;

public class PauseMenuBuilderLocal implements IPauseMenuBuilder {

	private final net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuBuilder delegate;

	PauseMenuBuilderLocal(net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuBuilder delegate) {
		this.delegate = delegate;
	}

	@Override
	public IPauseMenuBuilder copyFrom(IPauseMenuBuilder pauseMenu) {
		delegate.copyFrom(((PauseMenuBuilderLocal) pauseMenu).delegate);
		return this;
	}

	@Override
	public IPauseMenuBuilder copyFrom(ICustomPauseMenu pauseMenu) {
		delegate.copyFrom(PauseMenuHelper.unwrap(pauseMenu));
		return this;
	}

	@Override
	public EnumServerInfoButton getServerInfoButtonMode() {
		return PauseMenuHelper.wrap(delegate.getServerInfoButtonMode());
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeNone() {
		delegate.setServerInfoButtonModeNone();
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeURL(String text, String url) {
		delegate.setServerInfoButtonModeURL(text, url);
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewURL(String text, String title,
			Set<EnumWebViewPerms> permissions, String url) {
		delegate.setServerInfoButtonModeWebViewURL(text, title, WebViewHelper.unwrap(permissions), url);
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title,
			Set<EnumWebViewPerms> permissions, SHA1Sum blobHash) {
		delegate.setServerInfoButtonModeWebViewBlob(text, title, WebViewHelper.unwrap(permissions),
				WebViewHelper.unwrap(blobHash));
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title,
			Set<EnumWebViewPerms> permissions, String blobAlias) {
		throw new UnsupportedOperationException("Remote features not supported by this builder");
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeInheritDefault() {
		throw new UnsupportedOperationException("Remote features not supported by this builder");
	}

	@Override
	public String getServerInfoButtonText() {
		return delegate.getServerInfoButtonText();
	}

	@Override
	public String getServerInfoButtonURL() {
		return delegate.getServerInfoButtonURL();
	}

	@Override
	public String getServerInfoButtonWebViewTitle() {
		return delegate.getServerInfoButtonWebViewTitle();
	}

	@Override
	public Set<EnumWebViewPerms> getServerInfoButtonWebViewPerms() {
		return WebViewHelper.wrap(delegate.getServerInfoButtonWebViewPerms());
	}

	@Override
	public SHA1Sum getServerInfoButtonBlobHash() {
		return WebViewHelper.wrap(delegate.getServerInfoButtonBlobHash());
	}

	@Override
	public String getServerInfoButtonBlobAlias() {
		return null;
	}

	@Override
	public EnumDiscordInviteButton getDiscordInviteButtonMode() {
		return PauseMenuHelper.wrap(delegate.getDiscordInviteButtonMode());
	}

	@Override
	public IPauseMenuBuilder setDiscordInviteButtonModeNone() {
		delegate.setDiscordInviteButtonModeNone();
		return this;
	}

	@Override
	public IPauseMenuBuilder setDiscordInviteButtonModeURL(String text, String url) {
		delegate.setDiscordInviteButtonModeURL(text, url);
		return this;
	}

	@Override
	public IPauseMenuBuilder setDiscordInviteButtonModeInheritDefault() {
		throw new UnsupportedOperationException("Remote features not supported by this builder");
	}

	@Override
	public String getDiscordInviteButtonText() {
		return delegate.getDiscordInviteButtonText();
	}

	@Override
	public String getDiscordInviteButtonURL() {
		return delegate.getDiscordInviteButtonURL();
	}

	@Override
	public boolean isMenuIconInheritDefault(EnumPauseMenuIcon icon) {
		return false;
	}

	@Override
	public boolean isMenuIconInheritDefault(String icon) {
		return false;
	}

	@Override
	public IPacketImageData getMenuIcon(EnumPauseMenuIcon icon) {
		return PacketImageDataHelper.wrap(delegate.getMenuIcon(icon.getIconName()));
	}

	@Override
	public IPacketImageData getMenuIcon(String icon) {
		return PacketImageDataHelper.wrap(delegate.getMenuIcon(icon));
	}

	@Override
	public IPauseMenuBuilder setMenuIcon(EnumPauseMenuIcon icon, IPacketImageData imageData) {
		delegate.setMenuIcon(icon.getIconName(), PacketImageDataHelper.unwrap(imageData));
		return this;
	}

	@Override
	public IPauseMenuBuilder setMenuIcon(String icon, IPacketImageData imageData) {
		delegate.setMenuIcon(icon, PacketImageDataHelper.unwrap(imageData));
		return this;
	}

	@Override
	public IPauseMenuBuilder setMenuIconInheritDefault(EnumPauseMenuIcon icon) {
		throw new UnsupportedOperationException("Remote features not supported by this builder");
	}

	@Override
	public IPauseMenuBuilder setMenuIconInheritDefault(String icon) {
		throw new UnsupportedOperationException("Remote features not supported by this builder");
	}

	@Override
	public IPauseMenuBuilder clearMenuIcons() {
		delegate.clearMenuIcons();
		return this;
	}

	@Override
	public boolean isRemoteFeaturesSupported() {
		return false;
	}

	@Override
	public ICustomPauseMenu buildPauseMenu() {
		return PauseMenuHelper.wrap(delegate.buildPauseMenu());
	}

}
