package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinImageLoader;

class SkinImageLoaderCacheOn extends SkinImageLoaderCacheOff {

	static final ISkinImageLoader INSTANCE = new SkinImageLoaderCacheOn();

	private static final Cache<File, IEaglerPlayerSkin> cachedSkinFiles = CacheBuilder.newBuilder().weakValues().build();
	private static final Cache<File, IEaglerPlayerCape> cachedCapeFiles = CacheBuilder.newBuilder().weakValues().build();

	@Override
	public IEaglerPlayerSkin loadSkinImageData(File imageFile, EnumSkinModel modelId) throws IOException {
		try {
			return SkinImageLoaderImpl.rewriteCustomSkinModelId(cachedSkinFiles.get(imageFile, () -> {
				return SkinImageLoaderImpl.loadSkinImageData(imageFile, modelId);
			}), modelId);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if(cause instanceof IOException) {
				throw (IOException) cause;
			}else if(cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}else {
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
			if(cause instanceof IOException) {
				throw (IOException) cause;
			}else if(cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}else {
				throw new RuntimeException("Uncaught exception in lambda", cause);
			}
		}
	}

}
