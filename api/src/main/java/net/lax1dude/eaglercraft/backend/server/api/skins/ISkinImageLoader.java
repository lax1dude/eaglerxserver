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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;

public interface ISkinImageLoader {

	@Nonnull
	IEaglerPlayerSkin loadPresetSkin(int presetSkin);

	@Nonnull
	IEaglerPlayerSkin loadPresetSkin(@Nonnull EnumPresetSkins presetSkin);

	@Nonnull
	IEaglerPlayerSkin loadPresetSkin(@Nonnull UUID playerUUID);

	@Nonnull
	IEaglerPlayerCape loadPresetNoCape();

	@Nonnull
	IEaglerPlayerCape loadPresetCape(int presetCape);

	@Nonnull
	IEaglerPlayerCape loadPresetCape(@Nonnull EnumPresetCapes presetCape);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x64(@Nonnull int[] pixelsARGB8I, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x64(@Nonnull int[] pixelsARGB8I, int modelIdRaw);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_ABGR8_64x64(@Nonnull byte[] pixelsABGR8, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_ABGR8_64x64(@Nonnull byte[] pixelsABGR8, int modelIdRaw);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_eagler(@Nonnull byte[] pixelsEagler, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_eagler(@Nonnull byte[] pixelsEagler, int modelIdRaw);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x32(@Nonnull int[] pixelsARGB8I, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x32(@Nonnull int[] pixelsARGB8I, int modelIdRaw);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData(@Nonnull BufferedImage image, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData(@Nonnull BufferedImage image, int modelIdRaw);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData(@Nonnull @WillNotClose InputStream inputStream, @Nonnull EnumSkinModel modelId)
			throws IOException;

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData(@Nonnull @WillNotClose InputStream inputStream, int modelIdRaw)
			throws IOException;

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData(@Nonnull File imageFile, @Nonnull EnumSkinModel modelId) throws IOException;

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData(@Nonnull File imageFile, int modelIdRaw) throws IOException;

	@Nonnull
	IEaglerPlayerSkin rewriteCustomSkinModelId(@Nonnull IEaglerPlayerSkin skin, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin rewriteCustomSkinModelId(@Nonnull IEaglerPlayerSkin skin, int modelIdRaw);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData_ARGB8I_64x32(@Nonnull int[] pixelsARGB8I);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData_ARGB8I_32x32(@Nonnull int[] pixelsARGB8I);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData_ABGR8_32x32(@Nonnull byte[] pixelsABGR8);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData_eagler(@Nonnull byte[] pixelsEagler);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData(@Nonnull BufferedImage image);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData(@Nonnull @WillNotClose InputStream inputStream) throws IOException;

	@Nonnull
	IEaglerPlayerCape loadCapeImageData(@Nonnull File imageFile) throws IOException;

}
