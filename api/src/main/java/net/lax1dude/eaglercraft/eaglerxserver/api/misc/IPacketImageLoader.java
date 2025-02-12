package net.lax1dude.eaglercraft.eaglerxserver.api.misc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface IPacketImageLoader {

	PacketImageData loadPacketImageData(int width, int height, int[] pixelsARGB8);

	PacketImageData loadPacketImageData(BufferedImage bufferedImage);

	PacketImageData loadPacketImageData(InputStream inputStream) throws IOException;

	PacketImageData loadPacketImageData(File imageFile) throws IOException;

}
