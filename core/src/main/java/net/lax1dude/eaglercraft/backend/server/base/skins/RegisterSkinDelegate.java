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

package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.function.BiConsumer;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IRegisterSkinDelegate;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesResult;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;

abstract class RegisterSkinDelegate implements IRegisterSkinDelegate {

	final IEaglerPlayerSkin skinOriginal;
	final IEaglerPlayerCape capeOriginal;
	IEaglerPlayerSkin skin;
	IEaglerPlayerCape cape;
	EnumSkinModel skinModel;
	String skinURL;
	String capeURL;

	RegisterSkinDelegate(IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
		this.skinOriginal = this.skin = skin;
		this.capeOriginal = this.cape = cape;
	}

	protected abstract String resolveTexturesProperty();

	@Override
	public IEaglerPlayerSkin getEaglerSkin() {
		return skinOriginal;
	}

	@Override
	public IEaglerPlayerCape getEaglerCape() {
		return capeOriginal;
	}

	@Override
	public void forceFromVanillaTexturesProperty(String value) {
		TexturesResult profile = GameProfileUtil.extractSkinAndCape(value);
		if(profile == null) {
			throw new IllegalArgumentException("Textures property data is invalid!");
		}
		if(profile.getSkinURL() != null) {
			skin = null;
			skinModel = profile.getSkinModel();
			skinURL = profile.getSkinURL();
		}
		if(profile.getCapeURL() != null) {
			cape = null;
			capeURL = profile.getCapeURL();
		}
	}

	@Override
	public void forceFromVanillaLoginProfile() {
		String prop = resolveTexturesProperty();
		if(prop != null) {
			try {
				forceFromVanillaTexturesProperty(prop);
			}catch(IllegalArgumentException ex) {
			}
		}
	}

	@Override
	public void forceSkinFromURL(String value, EnumSkinModel model) {
		if(value != null) {
			skin = null;
			skinModel = model;
			skinURL = value;
		}
	}

	@Override
	public void forceSkinFromVanillaTexturesProperty(String value) {
		TexturesResult profile = GameProfileUtil.extractSkinAndCape(value);
		if(profile == null) {
			throw new IllegalArgumentException("Textures property data is invalid!");
		}
		if(profile.getSkinURL() != null) {
			skin = null;
			skinModel = profile.getSkinModel();
			skinURL = profile.getSkinURL();
		}
	}

	@Override
	public void forceSkinFromVanillaLoginProfile() {
		String prop = resolveTexturesProperty();
		if(prop != null) {
			try {
				forceSkinFromVanillaTexturesProperty(prop);
			}catch(IllegalArgumentException ex) {
			}
		}
	}

	@Override
	public void forceCapeFromURL(String value) {
		if(value != null) {
			cape = null;
			capeURL = value;
		}
	}

	@Override
	public void forceCapeFromVanillaTexturesProperty(String value) {
		TexturesResult profile = GameProfileUtil.extractSkinAndCape(value);
		if(profile == null) {
			throw new IllegalArgumentException("Textures property data is invalid!");
		}
		if(profile.getCapeURL() != null) {
			cape = null;
			capeURL = profile.getCapeURL();
		}
	}

	@Override
	public void forceCapeFromVanillaLoginProfile() {
		String prop = resolveTexturesProperty();
		if(prop != null) {
			try {
				forceCapeFromVanillaTexturesProperty(prop);
			}catch(IllegalArgumentException ex) {
			}
		}
	}

	@Override
	public void forceSkinEagler(IEaglerPlayerSkin skin) {
		if(skin != null) {
			skinURL = null;
			this.skin = skin;
		}
	}

	@Override
	public void forceCapeEagler(IEaglerPlayerCape cape) {
		if(cape != null) {
			capeURL = null;
			this.cape = cape;
		}
	}

	void handleComplete(EaglerPlayerInstance<?> player, IEaglerPlayerSkin skinResult,
			IEaglerPlayerCape capeResult, BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> onComplete) {
		if(skinResult == null) {
			skinResult = skinOriginal;
		}else if(!skinResult.equals(skinOriginal)) {
			if(player.getEaglerProtocol().ver >= 4) {
				player.sendEaglerMessage(skinResult.getForceSkinPacketV4());
			}
		}
		if(capeResult == null) {
			capeResult = capeOriginal;
		}else if(!capeResult.equals(capeOriginal)) {
			if(player.getEaglerProtocol().ver >= 4) {
				player.sendEaglerMessage(capeResult.getForceCapePacketV4());
			}
		}
		onComplete.accept(skinResult, capeResult);
	}

}
