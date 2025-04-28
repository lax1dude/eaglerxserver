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

package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

class PacketImageDataHelper implements IPacketImageLoader {

	static IPacketImageData wrap(PacketImageData image) {
		if (image == null)
			return null;
		return new PacketImageDataLocal(image);
	}

	static PacketImageData unwrap(IPacketImageData image) {
		if (image == null)
			return null;
		return ((PacketImageDataLocal) image).image;
	}

	static class PacketImageDataLocal implements IPacketImageData {

		final PacketImageData image;

		PacketImageDataLocal(PacketImageData image) {
			this.image = image;
		}

		@Override
		public int getWidth() {
			return image.width;
		}

		@Override
		public int getHeight() {
			return image.height;
		}

		@Override
		public void getPixels(int[] dest, int offset) {
			System.arraycopy(image.rgba, 0, dest, offset, image.rgba.length);
		}

	}

	private final net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader loader;

	PacketImageDataHelper(net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader loader) {
		this.loader = loader;
	}

	@Override
	public IPacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height) {
		return wrap(loader.loadPacketImageData(pixelsARGB8, width, height));
	}

	@Override
	public IPacketImageData loadPacketImageData(BufferedImage bufferedImage, int maxWidth, int maxHeight) {
		return wrap(loader.loadPacketImageData(bufferedImage, maxWidth, maxHeight));
	}

	@Override
	public IPacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight)
			throws IOException {
		return wrap(loader.loadPacketImageData(inputStream, maxWidth, maxHeight));
	}

	@Override
	public IPacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException {
		return wrap(loader.loadPacketImageData(imageFile, maxWidth, maxHeight));
	}

}
