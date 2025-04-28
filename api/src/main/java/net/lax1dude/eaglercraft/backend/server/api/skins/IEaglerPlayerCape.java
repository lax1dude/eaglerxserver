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

package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IOptional;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IEaglerPlayerCape extends IOptional<IEaglerPlayerCape> {

	@Nonnull
	GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			@Nonnull GamePluginMessageProtocol protocol);

	@Nonnull
	default GameMessagePacket getCapePacket(@Nonnull UUID rewriteUUID, @Nonnull GamePluginMessageProtocol protocol) {
		return getCapePacket(rewriteUUID.getMostSignificantBits(), rewriteUUID.getLeastSignificantBits(), protocol);
	}

	@Nonnull
	GameMessagePacket getCapePacket(int requestId, @Nonnull GamePluginMessageProtocol protocol);

	@Nonnull
	GameMessagePacket getForceCapePacketV4();

	boolean isCapeEnabled();

	boolean isCapePreset();

	int getPresetCapeId();

	@Nonnull
	EnumPresetCapes getPresetCape();

	boolean isCapeCustom();

	@Nonnull
	default byte[] getCustomCapePixels_ABGR8_32x32() {
		byte[] array = new byte[4096];
		getCustomCapePixels_ABGR8_32x32(array, 0);
		return array;
	}

	default void getCustomCapePixels_ABGR8_32x32(@Nonnull byte[] array) {
		getCustomCapePixels_ABGR8_32x32(array, 0);
	}

	void getCustomCapePixels_ABGR8_32x32(@Nonnull byte[] array, int offset);

	@Nonnull
	default byte[] getCustomCapePixels_eagler() {
		byte[] array = new byte[1173];
		getCustomCapePixels_eagler(array, 0);
		return array;
	}

	default void getCustomCapePixels_eagler(@Nonnull byte[] array) {
		getCustomCapePixels_eagler(array, 0);
	}

	void getCustomCapePixels_eagler(@Nonnull byte[] array, int offset);

}
