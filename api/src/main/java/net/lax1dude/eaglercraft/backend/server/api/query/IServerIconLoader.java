package net.lax1dude.eaglercraft.backend.server.api.query;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IServerIconLoader {

	int[] loadServerIcon(int[] pixelsIn, int width, int height);

	int[] loadServerIcon(BufferedImage image);

	int[] loadServerIcon(InputStream stream) throws IOException;

	int[] loadServerIcon(File file) throws IOException;

}
