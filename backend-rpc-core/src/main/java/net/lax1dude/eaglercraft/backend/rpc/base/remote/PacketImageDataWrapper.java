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

package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData;

public final class PacketImageDataWrapper implements IPacketImageData {

	public static IPacketImageData wrap(PacketImageData image) {
		return new PacketImageDataWrapper(image);
	}

	public static PacketImageData unwrap(IPacketImageData image) {
		return ((PacketImageDataWrapper) image).image;
	}

	private final PacketImageData image;

	private PacketImageDataWrapper(PacketImageData image) {
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

	@Override
	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof PacketImageDataWrapper o2) && image.equals(o2.image));
	}

	@Override
	public int hashCode() {
		return image.hashCode();
	}

}
