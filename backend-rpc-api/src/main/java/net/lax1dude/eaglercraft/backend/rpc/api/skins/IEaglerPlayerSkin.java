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

package net.lax1dude.eaglercraft.backend.rpc.api.skins;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.IOptional;

public interface IEaglerPlayerSkin extends IOptional<IEaglerPlayerSkin> {

	boolean isSkinPreset();

	int getPresetSkinId();

	@Nonnull
	EnumPresetSkins getPresetSkin();

	boolean isSkinCustom();

	@Nonnull
	default byte[] getCustomSkinPixels_ABGR8_64x64() {
		byte[] array = new byte[16384];
		getCustomSkinPixels_ABGR8_64x64(array, 0);
		return array;
	}

	default void getCustomSkinPixels_ABGR8_64x64(@Nonnull byte[] array) {
		getCustomSkinPixels_ABGR8_64x64(array, 0);
	}

	void getCustomSkinPixels_ABGR8_64x64(@Nonnull byte[] array, int offset);

	@Nonnull
	default byte[] getCustomSkinPixels_eagler() {
		byte[] array = new byte[12288];
		getCustomSkinPixels_eagler(array, 0);
		return array;
	}

	default void getCustomSkinPixels_eagler(@Nonnull byte[] array) {
		getCustomSkinPixels_eagler(array, 0);
	}

	void getCustomSkinPixels_eagler(@Nonnull byte[] array, int offset);

	@Nonnull
	EnumSkinModel getCustomSkinModelId();

}
