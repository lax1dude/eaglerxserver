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

package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientSkinCustomV4EAG;

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
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		return data.getSkinPacket(rewriteUUIDMost, rewriteUUIDLeast, EnumSkinModel.getById(modelId), protocol);
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, EnumSkinModel rewriteModelId,
			GamePluginMessageProtocol protocol) {
		return data.getSkinPacket(rewriteUUIDMost, rewriteUUIDLeast, rewriteModelId, protocol);
	}

	@Override
	public GameMessagePacket getForceSkinPacketV4() {
		return new SPacketForceClientSkinCustomV4EAG(modelId, data.textureDataV4());
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
		return data.getCustomSkinModelId();
	}

	@Override
	protected int modelId() {
		return data.modelId();
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
