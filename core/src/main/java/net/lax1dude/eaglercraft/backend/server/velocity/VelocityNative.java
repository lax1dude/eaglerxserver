package net.lax1dude.eaglercraft.backend.server.velocity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import java.util.zip.DataFormatException;

import com.velocitypowered.natives.compression.VelocityCompressor;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformZlib;
import net.lax1dude.eaglercraft.backend.server.util.Util;

class VelocityNative {

	public interface IVelocityNativeZlibFactory {

		IPlatformZlib create(int compressionLevel);

	}

	@SuppressWarnings("unchecked")
	public static IVelocityNativeZlibFactory bindFactory() {
		try {
			Object compressFactory = ((Supplier<Object>) Class.forName("com.velocitypowered.natives.util.Natives")
					.getField("compress").get(null)).get();
			Method create = compressFactory.getClass().getMethod("create", int.class);
			return (level) -> {
				try {
					return new VelocityZlibWrapper((VelocityCompressor) create.invoke(compressFactory, level));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw Util.propagateReflectThrowable(e);
				}
			};
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
				| ClassNotFoundException | NoSuchMethodException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static class VelocityZlibWrapper implements IPlatformZlib {

		private VelocityCompressor delegate;

		public VelocityZlibWrapper(VelocityCompressor delegate) {
			this.delegate = delegate;
		}

		@Override
		public boolean inflateEnabled() {
			return delegate != null;
		}

		@Override
		public void inflate(ByteBuf input, ByteBuf output) throws DataFormatException {
			delegate.inflate(input, output, output.writableBytes());
		}

		@Override
		public boolean deflateEnabled() {
			return delegate != null;
		}

		@Override
		public void deflate(ByteBuf input, ByteBuf output) throws DataFormatException {
			delegate.deflate(input, output);
		}

		@Override
		public void release() {
			if(delegate != null) {
				delegate.close();
				delegate = null;
			}
		}

	}

}
