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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

public class CustomSkinModelRw extends BaseCustomSkin implements IModelRewritable {

	private final BaseCustomSkin data;
	private final int modelId;

	CustomSkinModelRw(BaseCustomSkin data, int modelId) {
		this.data = data;
		this.modelId = modelId;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public boolean isSkinPreset() {
		return false;
	}

	@Override
	public int getPresetSkinId() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
	}

	@Override
	public boolean isSkinCustom() {
		return true;
	}

	@Override
	public void getCustomSkinPixels_ABGR8_64x64(byte[] array, int offset) {
		data.getCustomSkinPixels_ABGR8_64x64(array, offset);
	}

	@Override
	public void getCustomSkinPixels_eagler(byte[] array, int offset) {
		data.getCustomSkinPixels_eagler(array, offset);
	}

	@Override
	public EnumSkinModel getCustomSkinModelId() {
		return EnumSkinModel.getById(modelId);
	}

	@Override
	public int getCustomSkinRawModelId() {
		return modelId;
	}

	@Override
	protected int modelId() {
		return modelId;
	}

	@Override
	protected byte[] textureDataV3() {
		return data.textureDataV3();
	}

	@Override
	protected byte[] textureDataV4() {
		return data.textureDataV4();
	}

	@Override
	public IEaglerPlayerSkin rewriteModelInternal(int modelId) {
		if(modelId != this.modelId) {
			if(modelId == data.modelId()) {
				return data;
			}else {
				return new CustomSkinModelRw(data, modelId);
			}
		}else {
			return this;
		}
	}

}
