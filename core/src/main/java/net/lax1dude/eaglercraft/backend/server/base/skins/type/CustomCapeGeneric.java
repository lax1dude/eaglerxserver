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

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.base.skins.SkinConverterExt;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientCapeCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapeCustomEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapeCustomV5EAG;

public class CustomCapeGeneric extends BaseCustomCape {

	private final byte[] textureData;

	public CustomCapeGeneric(byte[] textureData) {
		this.textureData = textureData;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		if(protocol.ver <= 4) {
			return new SPacketOtherCapeCustomEAG(rewriteUUIDMost, rewriteUUIDLeast, textureData);
		}else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getCapePacket(int requestId, GamePluginMessageProtocol protocol) {
		if(protocol.ver >= 5) {
			return new SPacketOtherCapeCustomV5EAG(requestId, textureData);
		}else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getForceCapePacketV4() {
		return new SPacketForceClientCapeCustomV4EAG(textureData);
	}

	@Override
	public boolean isCapeEnabled() {
		return true;
	}

	@Override
	public boolean isCapePreset() {
		return false;
	}

	@Override
	public int getPresetCapeId() {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a preset cape");
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a preset cape");
	}

	@Override
	public boolean isCapeCustom() {
		return true;
	}

	@Override
	public void getCustomCapePixels_ABGR8_32x32(byte[] array, int offset) {
		SkinConverterExt.convertCape23x17RGBto32x32ABGR(textureData, 0, array, offset);
	}

	@Override
	public void getCustomCapePixels_eagler(byte[] array, int offset) {
		System.arraycopy(textureData, 0, array, offset, 1173);
	}

	@Override
	protected byte[] textureData() {
		return textureData;
	}

}
