package net.lax1dude.eaglercraft.backend.server.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IServerIconLoader {

	byte[] loadServerIcon(int[] pixelsIn, int width, int height);

	byte[] loadServerIcon(BufferedImage image);

	byte[] loadServerIcon(InputStream stream) throws IOException;

	byte[] loadServerIcon(File file) throws IOException;

}
