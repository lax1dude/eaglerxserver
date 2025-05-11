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

package net.lax1dude.eaglercraft.backend.server.bungee.event;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IRegisterSkinDelegate;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftRegisterSkinEvent;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

public class BungeeRegisterSkinDelegate implements EaglercraftRegisterSkinEvent.IRegisterSkinDelegate {

	private final IRegisterSkinDelegate delegate;

	BungeeRegisterSkinDelegate(IRegisterSkinDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public IEaglerPlayerSkin getEaglerSkin() {
		return delegate.getEaglerSkin();
	}

	@Override
	public IEaglerPlayerCape getEaglerCape() {
		return delegate.getEaglerCape();
	}

	@Override
	public void forceFromVanillaTexturesProperty(String value) {
		delegate.forceFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceFromVanillaLoginProfile() {
		delegate.forceFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinFromURL(String url, EnumSkinModel skinModel) {
		delegate.forceSkinFromURL(url, skinModel);
	}

	@Override
	public void forceSkinFromVanillaTexturesProperty(String value) {
		delegate.forceSkinFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceSkinFromVanillaLoginProfile() {
		delegate.forceSkinFromVanillaLoginProfile();
	}

	@Override
	public void forceCapeFromURL(String url) {
		delegate.forceCapeFromURL(url);
	}

	@Override
	public void forceCapeFromVanillaTexturesProperty(String value) {
		delegate.forceCapeFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceCapeFromVanillaLoginProfile() {
		delegate.forceCapeFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinEagler(IEaglerPlayerSkin skin) {
		delegate.forceSkinEagler(skin);
	}

	@Override
	public void forceCapeEagler(IEaglerPlayerCape skin) {
		delegate.forceCapeEagler(skin);
	}

}
