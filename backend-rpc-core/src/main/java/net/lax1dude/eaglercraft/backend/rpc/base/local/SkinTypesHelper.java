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

package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;

class SkinTypesHelper implements ISkinImageLoader {

	static IEaglerPlayerSkin wrap(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin skin) {
		return new PlayerSkinLocal(skin);
	}

	static IEaglerPlayerCape wrap(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape) {
		return new PlayerCapeLocal(cape);
	}

	static EnumPresetSkins wrap(net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins skin) {
		return EnumPresetSkins.getByIdOrDefault(skin.getId());
	}

	static EnumPresetCapes wrap(net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes cape) {
		return EnumPresetCapes.getByIdOrDefault(cape.getId());
	}

	static EnumSkinModel wrap(net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel model) {
		return EnumSkinModel.getById(model.getId());
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin unwrap(IEaglerPlayerSkin skin) {
		return ((PlayerSkinLocal) skin).skin;
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape unwrap(IEaglerPlayerCape cape) {
		return ((PlayerCapeLocal) cape).cape;
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins unwrap(EnumPresetSkins skin) {
		return net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins.getByIdOrDefault(skin.getId());
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes unwrap(EnumPresetCapes cape) {
		return net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes.getByIdOrDefault(cape.getId());
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel unwrap(EnumSkinModel model) {
		return net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel.getById(model.getId());
	}

	static EnumEnableFNAW wrap(net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW en) {
		return switch (en) {
		default -> EnumEnableFNAW.DISABLED;
		case ENABLED -> EnumEnableFNAW.ENABLED;
		case FORCED -> EnumEnableFNAW.FORCED;
		};
	}

	static net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW unwrap(EnumEnableFNAW en) {
		return switch (en) {
		default -> net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW.DISABLED;
		case ENABLED -> net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW.ENABLED;
		case FORCED -> net.lax1dude.eaglercraft.backend.server.api.skins.EnumEnableFNAW.FORCED;
		};
	}

	static class PlayerSkinLocal implements IEaglerPlayerSkin {

		final net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin skin;

		PlayerSkinLocal(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin skin) {
			this.skin = skin;
		}

		@Override
		public boolean isSuccess() {
			return skin.isSuccess();
		}

		@Override
		public boolean isSkinPreset() {
			return skin.isSkinPreset();
		}

		@Override
		public int getPresetSkinId() {
			return skin.getPresetSkinId();
		}

		@Override
		public EnumPresetSkins getPresetSkin() {
			return EnumPresetSkins.getByIdOrDefault(skin.getPresetSkinId());
		}

		@Override
		public boolean isSkinCustom() {
			return skin.isSkinCustom();
		}

		@Override
		public void getCustomSkinPixels_ABGR8_64x64(byte[] array, int offset) {
			skin.getCustomSkinPixels_ABGR8_64x64(array, offset);
		}

		@Override
		public void getCustomSkinPixels_eagler(byte[] array, int offset) {
			skin.getCustomSkinPixels_eagler(array, offset);
		}

		@Override
		public EnumSkinModel getCustomSkinModelId() {
			return EnumSkinModel.getById(skin.getCustomSkinModelId().getId());
		}

		@Override
		public int getCustomSkinRawModelId() {
			return skin.getCustomSkinRawModelId();
		}

		@Override
		public int hashCode() {
			return skin.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj || ((obj instanceof PlayerSkinLocal other) && skin.equals(other.skin));
		}

	}

	static class PlayerCapeLocal implements IEaglerPlayerCape {

		final net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape;

		PlayerCapeLocal(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape) {
			this.cape = cape;
		}

		@Override
		public boolean isSuccess() {
			return cape.isSuccess();
		}

		@Override
		public boolean isCapeEnabled() {
			return cape.isCapeEnabled();
		}

		@Override
		public boolean isCapePreset() {
			return cape.isCapePreset();
		}

		@Override
		public int getPresetCapeId() {
			return cape.getPresetCapeId();
		}

		@Override
		public EnumPresetCapes getPresetCape() {
			return EnumPresetCapes.getByIdOrDefault(cape.getPresetCapeId());
		}

		@Override
		public boolean isCapeCustom() {
			return cape.isCapeCustom();
		}

		@Override
		public void getCustomCapePixels_ABGR8_32x32(byte[] array, int offset) {
			cape.getCustomCapePixels_ABGR8_32x32(array, offset);
		}

		@Override
		public void getCustomCapePixels_eagler(byte[] array, int offset) {
			cape.getCustomCapePixels_eagler(array, offset);
		}

		@Override
		public int hashCode() {
			return cape.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj || ((obj instanceof PlayerCapeLocal other) && cape.equals(other.cape));
		}

	}

	final net.lax1dude.eaglercraft.backend.server.api.skins.ISkinImageLoader loader;

	public SkinTypesHelper(net.lax1dude.eaglercraft.backend.server.api.skins.ISkinImageLoader loader) {
		this.loader = loader;
	}

	@Override
	public IEaglerPlayerSkin loadPresetSkin(int presetSkin) {
		return wrap(loader.loadPresetSkin(presetSkin));
	}

	@Override
	public IEaglerPlayerSkin loadPresetSkin(EnumPresetSkins presetSkin) {
		return wrap(loader.loadPresetSkin(presetSkin.getId()));
	}

	@Override
	public IEaglerPlayerSkin loadPresetSkin(UUID playerUUID) {
		return wrap(loader.loadPresetSkin(playerUUID));
	}

	@Override
	public IEaglerPlayerCape loadPresetNoCape() {
		return wrap(loader.loadPresetNoCape());
	}

	@Override
	public IEaglerPlayerCape loadPresetCape(int presetCape) {
		return wrap(loader.loadPresetCape(presetCape));
	}

	@Override
	public IEaglerPlayerCape loadPresetCape(EnumPresetCapes presetCape) {
		return wrap(loader.loadPresetCape(presetCape.getId()));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x64(int[] pixelsARGB8, EnumSkinModel modelId) {
		return wrap(loader.loadSkinImageData_ARGB8I_64x64(pixelsARGB8, unwrap(modelId)));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x64(int[] pixelsARGB8, int modelIdRaw) {
		return wrap(loader.loadSkinImageData_ARGB8I_64x64(pixelsARGB8, modelIdRaw));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData_ABGR8_64x64(byte[] pixelsRGBA8, EnumSkinModel modelId) {
		return wrap(loader.loadSkinImageData_ABGR8_64x64(pixelsRGBA8, unwrap(modelId)));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData_ABGR8_64x64(byte[] pixelsRGBA8, int modelIdRaw) {
		return wrap(loader.loadSkinImageData_ABGR8_64x64(pixelsRGBA8, modelIdRaw));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData_eagler(byte[] pixelsEagler, EnumSkinModel modelId) {
		return wrap(loader.loadSkinImageData_eagler(pixelsEagler, unwrap(modelId)));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData_eagler(byte[] pixelsEagler, int modelIdRaw) {
		return wrap(loader.loadSkinImageData_eagler(pixelsEagler, modelIdRaw));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x32(int[] pixelsARGB8, EnumSkinModel modelId) {
		return wrap(loader.loadSkinImageData_ARGB8I_64x32(pixelsARGB8, unwrap(modelId)));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x32(int[] pixelsARGB8, int modelIdRaw) {
		return wrap(loader.loadSkinImageData_ARGB8I_64x32(pixelsARGB8, modelIdRaw));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(BufferedImage image, EnumSkinModel modelId) {
		return wrap(loader.loadSkinImageData(image, unwrap(modelId)));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(BufferedImage image, int modelIdRaw) {
		return wrap(loader.loadSkinImageData(image, modelIdRaw));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(InputStream inputStream, EnumSkinModel modelId) throws IOException {
		return wrap(loader.loadSkinImageData(inputStream, unwrap(modelId)));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(InputStream inputStream, int modelIdRaw) throws IOException {
		return wrap(loader.loadSkinImageData(inputStream, modelIdRaw));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(File imageFile, EnumSkinModel modelId) throws IOException {
		return wrap(loader.loadSkinImageData(imageFile, unwrap(modelId)));
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(File imageFile, int modelIdRaw) throws IOException {
		return wrap(loader.loadSkinImageData(imageFile, modelIdRaw));
	}

	@Override
	public IEaglerPlayerSkin rewriteCustomSkinModelId(IEaglerPlayerSkin skin, EnumSkinModel modelId) {
		return wrap(loader.rewriteCustomSkinModelId(unwrap(skin), unwrap(modelId)));
	}

	@Override
	public IEaglerPlayerSkin rewriteCustomSkinModelId(IEaglerPlayerSkin skin, int modelIdRaw) {
		return wrap(loader.rewriteCustomSkinModelId(unwrap(skin), modelIdRaw));
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData_ARGB8I_64x32(int[] pixelsARGB8) {
		return wrap(loader.loadCapeImageData_ARGB8I_64x32(pixelsARGB8));
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData_ARGB8I_32x32(int[] pixelsARGB8) {
		return wrap(loader.loadCapeImageData_ARGB8I_32x32(pixelsARGB8));
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData_ABGR8_32x32(byte[] pixelsRGBA8) {
		return wrap(loader.loadCapeImageData_ABGR8_32x32(pixelsRGBA8));
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData_eagler(byte[] pixelsEagler) {
		return wrap(loader.loadCapeImageData_eagler(pixelsEagler));
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(BufferedImage image) {
		return wrap(loader.loadCapeImageData(image));
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(InputStream inputStream) throws IOException {
		return wrap(loader.loadCapeImageData(inputStream));
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(File imageFile) throws IOException {
		return wrap(loader.loadCapeImageData(imageFile));
	}

}
