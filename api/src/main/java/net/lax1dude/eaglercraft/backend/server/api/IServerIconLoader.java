package net.lax1dude.eaglercraft.backend.server.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

public interface IServerIconLoader {

	@Nonnull
	byte[] loadServerIcon(@Nonnull int[] pixelsIn, int width, int height);

	@Nonnull
	byte[] loadServerIcon(@Nonnull BufferedImage image);

	@Nonnull
	byte[] loadServerIcon(@Nonnull InputStream stream) throws IOException;

	@Nonnull
	byte[] loadServerIcon(@Nonnull File file) throws IOException;

}
