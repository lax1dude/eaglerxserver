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

package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterSkinEvent;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public final class EaglercraftRegisterSkinEvent extends AsyncEvent<IEaglercraftRegisterSkinEvent<ProxiedPlayer>>
		implements IEaglercraftRegisterSkinEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerLoginConnection loginConnection;
	private final IRegisterSkinDelegate delegate;

	public EaglercraftRegisterSkinEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerLoginConnection loginConnection, @Nonnull IRegisterSkinDelegate delegate,
			@Nonnull Callback<IEaglercraftRegisterSkinEvent<ProxiedPlayer>> cb) {
		super(cb);
		this.api = api;
		this.loginConnection = loginConnection;
		this.delegate = delegate;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IEaglerLoginConnection getLoginConnection() {
		return loginConnection;
	}

	@Nonnull
	@Override
	public IEaglerPlayerSkin getEaglerSkin() {
		return delegate.getEaglerSkin();
	}

	@Nonnull
	@Override
	public IEaglerPlayerCape getEaglerCape() {
		return delegate.getEaglerCape();
	}

	@Override
	public void forceFromVanillaTexturesProperty(@Nonnull String value) {
		if(value == null) {
			throw new NullPointerException("value");
		}
		delegate.forceFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceFromVanillaLoginProfile() {
		delegate.forceFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinFromURL(@Nonnull String url, @Nonnull EnumSkinModel skinModel) {
		if(url == null) {
			throw new NullPointerException("url");
		}
		if(skinModel == null) {
			throw new NullPointerException("skinModel");
		}
		delegate.forceSkinFromURL(url, skinModel);
	}

	@Override
	public void forceSkinFromVanillaTexturesProperty(@Nonnull String value) {
		delegate.forceSkinFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceSkinFromVanillaLoginProfile() {
		delegate.forceSkinFromVanillaLoginProfile();
	}

	@Override
	public void forceCapeFromURL(@Nonnull String url) {
		if(url == null) {
			throw new NullPointerException("url");
		}
		delegate.forceCapeFromURL(url);
	}

	@Override
	public void forceCapeFromVanillaTexturesProperty(@Nonnull String value) {
		if(value == null) {
			throw new NullPointerException("value");
		}
		delegate.forceCapeFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceCapeFromVanillaLoginProfile() {
		delegate.forceCapeFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinEagler(@Nonnull IEaglerPlayerSkin skin) {
		if(skin == null) {
			throw new NullPointerException("skin");
		}
		delegate.forceSkinEagler(skin);
	}

	@Override
	public void forceCapeEagler(@Nonnull IEaglerPlayerCape cape) {
		if(cape == null) {
			throw new NullPointerException("cape");
		}
		delegate.forceCapeEagler(cape);
	}

	public interface IRegisterSkinDelegate {

		@Nonnull
		IEaglerPlayerSkin getEaglerSkin();

		@Nonnull
		IEaglerPlayerCape getEaglerCape();

		void forceFromVanillaTexturesProperty(@Nonnull String value);

		void forceFromVanillaLoginProfile();

		void forceSkinFromURL(@Nonnull String url, @Nonnull EnumSkinModel skinModel);

		void forceSkinFromVanillaTexturesProperty(@Nonnull String value);

		void forceSkinFromVanillaLoginProfile();

		void forceCapeFromURL(@Nonnull String url);

		void forceCapeFromVanillaTexturesProperty(@Nonnull String value);

		void forceCapeFromVanillaLoginProfile();

		void forceSkinEagler(@Nonnull IEaglerPlayerSkin skin);

		void forceCapeEagler(@Nonnull IEaglerPlayerCape skin);

	}

}
