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
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV3EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SkinPacketVersionCache;

public class CustomSkinGeneric extends BaseCustomSkin implements IModelRewritable {

	private final int modelId;
	private byte[] textureDataV3;
	private byte[] textureDataV4;

	private CustomSkinGeneric(int modelId, byte[] textureDataV3, byte[] textureDataV4) {
		this.modelId = modelId;
		this.textureDataV3 = textureDataV3;
		this.textureDataV4 = textureDataV4;
	}

	public static CustomSkinGeneric createV3(int modelId, byte[] textureDataV3) {
		return new CustomSkinGeneric(modelId, textureDataV3, null);
	}

	public static CustomSkinGeneric createV4(int modelId, byte[] textureDataV4) {
		return new CustomSkinGeneric(modelId, null, textureDataV4);
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		switch(protocol) {
		case V3:
			return new SPacketOtherSkinCustomV3EAG(rewriteUUIDMost, rewriteUUIDLeast, modelId, textureDataV3());
		case V4:
		default:
			return new SPacketOtherSkinCustomV4EAG(rewriteUUIDMost, rewriteUUIDLeast, modelId, textureDataV4());
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, EnumSkinModel rewriteModelId,
			GamePluginMessageProtocol protocol) {
		return getSkinPacket(rewriteUUIDMost, rewriteUUIDLeast, rewriteModelId.getId(), protocol);
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, int rewriteModelIdRaw,
			GamePluginMessageProtocol protocol) {
		switch(protocol) {
		case V3:
			return new SPacketOtherSkinCustomV3EAG(rewriteUUIDMost, rewriteUUIDLeast, rewriteModelIdRaw, textureDataV3());
		case V4:
		default:
			return new SPacketOtherSkinCustomV4EAG(rewriteUUIDMost, rewriteUUIDLeast, rewriteModelIdRaw, textureDataV4());
		}
	}

	@Override
	public GameMessagePacket getForceSkinPacketV4() {
		return new SPacketForceClientSkinCustomV4EAG(modelId, textureDataV4());
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
		System.arraycopy(textureDataV3(), 0, array, offset, 16384);
	}

	@Override
	public void getCustomSkinPixels_eagler(byte[] array, int offset) {
		System.arraycopy(textureDataV4(), 0, array, offset, 12288);
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
		if(textureDataV3 != null) {
			return textureDataV3;
		}else {
			return textureDataV3 = SkinPacketVersionCache.convertToV3Raw(textureDataV4);
		}
	}

	@Override
	protected byte[] textureDataV4() {
		if(textureDataV4 != null) {
			return textureDataV4;
		}else {
			return textureDataV4 = SkinPacketVersionCache.convertToV4Raw(textureDataV3);
		}
	}

	@Override
	public IEaglerPlayerSkin rewriteModelInternal(int modelId) {
		if(modelId != this.modelId) {
			return new CustomSkinModelRw(this, modelId);
		}else {
			return this;
		}
	}

}
