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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;

class SkinImageLoaderCacheOn extends SkinImageLoaderCacheOff {

	static final ISkinImageLoader INSTANCE = new SkinImageLoaderCacheOn();

	private static final Cache<File, IEaglerPlayerSkin> cachedSkinFiles = CacheBuilder.newBuilder().weakValues().build();
	private static final Cache<File, IEaglerPlayerCape> cachedCapeFiles = CacheBuilder.newBuilder().weakValues().build();

	@Override
	public IEaglerPlayerSkin loadSkinImageData(File imageFile, EnumSkinModel modelId) throws IOException {
		return loadSkinImageData(imageFile, modelId.getId());
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(File imageFile, int modelId) throws IOException {
		if (modelId < 0 || modelId >= 0xFF) {
			throw new IllegalArgumentException("Invalid model id: " + modelId);
		}
		try {
			return SkinImageLoaderImpl.rewriteCustomSkinModelId(cachedSkinFiles.get(imageFile, () -> {
				return SkinImageLoaderImpl.loadSkinImageData(imageFile, (modelId & 0x7F) == 1 ? 1 : 0);
			}), modelId);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof IOException c2) {
				throw c2;
			} else if (cause instanceof RuntimeException c3) {
				throw c3;
			} else {
				throw new RuntimeException("Uncaught exception in lambda", cause);
			}
		}
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(File imageFile) throws IOException {
		try {
			return cachedCapeFiles.get(imageFile, () -> {
				return SkinImageLoaderImpl.loadCapeImageData(imageFile);
			});
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof IOException c2) {
				throw c2;
			} else if (cause instanceof RuntimeException c3) {
				throw c3;
			} else {
				throw new RuntimeException("Uncaught exception in lambda", cause);
			}
		}
	}

}
