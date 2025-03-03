package net.lax1dude.eaglercraft.backend.server.bungee;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.DataFormatException;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformZlib;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.md_5.bungee.jni.zlib.BungeeZlib;

class BungeeNative {

	public interface IBungeeNativeZlibFactory {

		IPlatformZlib create(boolean compression, boolean decompression, int compressionLevel);

	}

	public static IBungeeNativeZlibFactory bindFactory() {
		try {
			Object compressFactory = Class.forName("net.md_5.bungee.compress.CompressFactory").getField("zlib").get(null);
			Field loadedField = compressFactory.getClass().getDeclaredField("loaded");
			loadedField.setAccessible(true);
			if(!(Boolean) loadedField.get(compressFactory)) {
				try {
					if(!(Boolean) compressFactory.getClass().getMethod("load").invoke(compressFactory)) {
						return null;
					}
				} catch (InvocationTargetException e) {
					return null;
				}
			}
			Method newInstance = compressFactory.getClass().getMethod("newInstance");
			return (compression, decompression, compressionLevel) -> {
				BungeeZlib compressionInstance = null;
				BungeeZlib decompressionInstance = null;
				try {
					if(compression) {
						compressionInstance = (BungeeZlib) newInstance.invoke(compressFactory);
						compressionInstance.init(true, compressionLevel);
					}
					if(decompression) {
						decompressionInstance = (BungeeZlib) newInstance.invoke(compressFactory);
						decompressionInstance.init(false, 0);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					try {
						if(compressionInstance != null) {
							compressionInstance.free();
						}
					}finally {
						if(decompressionInstance != null) {
							decompressionInstance.free();
						}
					}
					throw Util.propagateReflectThrowable(e);
				}
				return new BungeeZlibWrapper(compressionInstance, decompressionInstance);
			};
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException
				| NoSuchFieldException | SecurityException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static class BungeeZlibWrapper implements IPlatformZlib {

		private final BungeeZlib compressionInstance;
		private final BungeeZlib decompressionInstance;

		public BungeeZlibWrapper(BungeeZlib compressionInstance, BungeeZlib decompressionInstance) {
			this.compressionInstance = compressionInstance;
			this.decompressionInstance = decompressionInstance;
		}

		@Override
		public boolean inflateEnabled() {
			return decompressionInstance != null;
		}

		@Override
		public void inflate(ByteBuf input, ByteBuf output) throws DataFormatException {
			decompressionInstance.process(input, output);
		}

		@Override
		public boolean deflateEnabled() {
			return compressionInstance != null;
		}

		@Override
		public void deflate(ByteBuf input, ByteBuf output) throws DataFormatException {
			compressionInstance.process(input, output);
		}

		@Override
		public void release() {
			try {
				if(compressionInstance != null) {
					compressionInstance.free();
				}
			}finally {
				if(decompressionInstance != null) {
					decompressionInstance.free();
				}
			}
		}

	}

}
