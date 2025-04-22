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

package net.lax1dude.eaglercraft.backend.rpc.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;

public interface IPacketImageLoader {

	@Nonnull
	IPacketImageData loadPacketImageData(@Nonnull int[] pixelsARGB8, int width, int height);

	@Nonnull
	default IPacketImageData loadPacketImageData(@Nonnull BufferedImage bufferedImage) {
		return loadPacketImageData(bufferedImage, 255, 255);
	}

	@Nonnull
	IPacketImageData loadPacketImageData(@Nonnull BufferedImage bufferedImage, int maxWidth, int maxHeight);

	@Nonnull
	default IPacketImageData loadPacketImageData(@Nonnull @WillNotClose InputStream inputStream) throws IOException {
		return loadPacketImageData(inputStream, 255, 255);
	}

	@Nonnull
	IPacketImageData loadPacketImageData(@Nonnull @WillNotClose InputStream inputStream, int maxWidth, int maxHeight) throws IOException;

	@Nonnull
	default IPacketImageData loadPacketImageData(@Nonnull File imageFile) throws IOException {
		return loadPacketImageData(imageFile, 255, 255);
	}

	@Nonnull
	IPacketImageData loadPacketImageData(@Nonnull File imageFile, int maxWidth, int maxHeight) throws IOException;

}
