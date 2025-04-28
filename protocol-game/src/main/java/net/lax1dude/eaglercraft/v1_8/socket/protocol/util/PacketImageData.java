/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.socket.protocol.util;

import java.io.IOException;
import java.util.Arrays;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;

public final class PacketImageData {

	public final int width;
	public final int height;
	public final int[] rgba;

	public PacketImageData(int width, int height, int[] rgba) {
		this.width = width;
		this.height = height;
		this.rgba = rgba;
	}

	public int getByteLengthRGB16() {
		return 2 + (rgba.length << 1);
	}

	public static PacketImageData readRGB16(GamePacketInputBuffer buffer) throws IOException {
		int w = buffer.readUnsignedByte();
		int h = buffer.readUnsignedByte();
		int pixelCount = w * h;
		int[] pixels = new int[pixelCount];
		for (int j = 0, p, pR, pG, pB; j < pixelCount; ++j) {
			p = buffer.readUnsignedShort();
			pR = (p >>> 11) & 0x1F;
			pG = (p >>> 5) & 0x3F;
			pB = p & 0x1F;
			if (pR + pG + pB > 0) {
				pB = (pB - 1) * 255 / 30;
				pixels[j] = 0xFF000000 | (pR << 19) | (pG << 10) | pB;
			} else {
				pixels[j] = 0;
			}
		}
		return new PacketImageData(w, h, pixels);
	}

	public static void writeRGB16(GamePacketOutputBuffer buffer, PacketImageData imageData) throws IOException {
		if (imageData.width < 1 || imageData.width > 255 || imageData.height < 1 || imageData.height > 255) {
			throw new IOException("Invalid image dimensions in packet, must be between 1x1 and 255x255, got "
					+ imageData.width + "x" + imageData.height);
		}
		buffer.writeByte(imageData.width);
		buffer.writeByte(imageData.height);
		int pixelCount = imageData.width * imageData.height;
		for (int j = 0, p, pR, pG, pB; j < pixelCount; ++j) {
			p = imageData.rgba[j];
			if ((p >>> 24) > 0x7F) {
				pR = (p >>> 19) & 0x1F;
				pG = (p >>> 10) & 0x3F;
				pB = ((p & 0xFF) * 30 / 255) + 1;
				buffer.writeShort((pR << 11) | (pG << 5) | pB);
			} else {
				buffer.writeShort(0);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(rgba);
		result = prime * result + width;
		result = prime * result + height;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PacketImageData))
			return false;
		PacketImageData other = (PacketImageData) obj;
		return width == other.width && height == other.height && Arrays.equals(rgba, other.rgba);
	}

}
