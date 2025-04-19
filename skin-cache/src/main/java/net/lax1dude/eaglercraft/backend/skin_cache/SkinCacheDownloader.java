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

package net.lax1dude.eaglercraft.backend.skin_cache;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import io.netty.buffer.ByteBufInputStream;

public class SkinCacheDownloader implements ISkinCacheDownloader {

	private final IHTTPClient httpClient;
	private final Set<String> validSkinHosts;

	public SkinCacheDownloader(IHTTPClient httpClient, Set<String> validSkinHosts) {
		this.httpClient = httpClient;
		this.validSkinHosts = validSkinHosts;
	}

	private URI validateSkinURL(String url) {
		try {
			URI uri = URI.create(url);
			String host = uri.getHost();
			if(host == null || !validSkinHosts.contains(host.toLowerCase())) {
				return null;
			}
			return uri;
		}catch(IllegalArgumentException t) {
			return null;
		}
	}

	private void downloadTexture(String skinURL, Consumer<BufferedImage> callback) {
		URI uri = validateSkinURL(skinURL);
		if(uri != null) {
			httpClient.asyncRequest("GET", uri, (res) -> {
				if(res.exception != null || res.data == null) {
					callback.accept(null);
				}else {
					BufferedImage img = null;
					try {
						if(res.code == 200) {
							img = ImageIO.read(new ByteBufInputStream(res.data));
						}
					}catch (IOException e) {
					}finally {
						res.data.release();
					}
					callback.accept(img);
				}
			});
		}else {
			callback.accept(null);
		}
	}

	@Override
	public void downloadSkin(String skinURL, Consumer<byte[]> callback) {
		downloadTexture(skinURL, (res) -> {
			if(res != null) {
				if(res.getWidth() == 64) {
					byte[] pixels;
					if(res.getHeight() == 32) {
						int[] pixelsIn = res.getRGB(0, 0, 64, 32, null, 0, 64);
						pixels = new byte[64 * 64 * 3];
						SkinConverter.convert64x32To64x64(pixelsIn, pixels);
					}else if(res.getHeight() == 64) {
						int[] pixelsIn = res.getRGB(0, 0, 64, 64, null, 0, 64);
						pixels = new byte[64 * 64 * 3];
						SkinConverter.convertToBytes(pixelsIn, pixels);
					}else {
						callback.accept(null);
						return;
					}
					callback.accept(pixels);
				}else {
					callback.accept(null);
				}
			}else {
				callback.accept(null);
			}
		});
	}

	@Override
	public void downloadCape(String skinURL, Consumer<byte[]> callback) {
		downloadTexture(skinURL, (res) -> {
			if(res != null) {
				if(res.getWidth() == 64 && res.getHeight() == 32) {
					int[] pixelsIn = res.getRGB(0, 0, 64, 32, null, 0, 64);
					byte[] pixels = new byte[1173];
					SkinConverter.convertCape64x32RGBAto23x17RGB(pixelsIn, pixels);
					callback.accept(pixels);
				}else {
					callback.accept(null);
				}
			}else {
				callback.accept(null);
			}
		});
	}

}