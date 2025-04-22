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

package net.lax1dude.eaglercraft.backend.server.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class PacketImageLoader {

	public static final IPacketImageLoader INSTANCE = new IPacketImageLoader() {
		@Override
		public PacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height) {
			return PacketImageLoader.loadPacketImageData(pixelsARGB8, width, height);
		}
		@Override
		public PacketImageData loadPacketImageData(BufferedImage bufferedImage, int maxWidth, int maxHeight) {
			return PacketImageLoader.loadPacketImageData(bufferedImage, maxWidth, maxHeight);
		}
		@Override
		public PacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight) throws IOException {
			return PacketImageLoader.loadPacketImageData(inputStream, maxWidth, maxHeight);
		}
		@Override
		public PacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException {
			return PacketImageLoader.loadPacketImageData(imageFile, maxWidth, maxHeight);
		}
	};

	public static PacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height) {
		checkSize(width, height);
		if(pixelsARGB8 == null) {
			throw new NullPointerException("pixelsARGB8");
		}
		return new PacketImageData(width, height, pixelsARGB8);
	}

	public static PacketImageData loadPacketImageData(BufferedImage img, int maxWidth, int maxHeight) {
		checkSize(maxWidth, maxHeight);
		int w = img.getWidth();
		int h = img.getHeight();
		if(w > maxWidth || h > maxHeight) {
			float aspectRatio = (float)w / (float)h;
			int nw, nh;
			if(aspectRatio >= 1.0f) {
				nw = (int)(maxWidth / aspectRatio);
				nh = maxHeight;
			}else {
				nw = maxWidth;
				nh = (int)(maxHeight * aspectRatio);
			}
			BufferedImage resized = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) resized.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setBackground(new Color(0, true));
			g.clearRect(0, 0, nw, nh);
			g.drawImage(img, 0, 0, nw, nh, 0, 0, w, h, null);
			g.dispose();
			img = resized;
		}
		int[] pixels = new int[w * h];
		img.getRGB(0, 0, w, h, pixels, 0, w);
		return new PacketImageData(w, h, pixels);
	}

	public static PacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight) throws IOException {
		checkSize(maxWidth, maxHeight);
		return loadPacketImageData(ImageIO.read(inputStream), maxWidth, maxHeight);
	}

	public static PacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException {
		checkSize(maxWidth, maxHeight);
		return loadPacketImageData(ImageIO.read(imageFile), maxWidth, maxHeight);
	}

	private static void checkSize(int w, int h) {
		if(w < 0 || h < 0) {
			throw new IllegalArgumentException("Size is negative");
		}
		if(w > 255) {
			throw new IllegalArgumentException("Width is greater than 255");
		}
		if(h > 255) {
			throw new IllegalArgumentException("Height is greater than 255");
		}
	}

}
