package net.lax1dude.eaglercraft.backend.server.base.webview;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlobBuilder;
import net.lax1dude.eaglercraft.backend.server.util.ImmutableBuilders;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketServerInfoDataChunkV4EAG;

public abstract class WebViewBlobBuilder<T extends Closeable> implements IWebViewBlobBuilder<T> {

	protected final int chunkSize;
	protected T streamInstance;
	protected IWebViewBlob result;

	protected WebViewBlobBuilder(int chunkSize) {
		this.chunkSize = Math.max(chunkSize, 16);
	}

	protected abstract T wrap(OutputStream os);

	private OutputStream init() {
		try {
			return new OutputStream() {
				protected int totalUncompressedSize = 0;
				protected MessageDigest digest = Util.sha1();
				private final OutputStream delegate = new GZIPOutputStream(new OutputStream() {
					protected byte[] currentChunk = new byte[chunkSize];
					protected int currentChunkSize = 4;
					protected List<byte[]> chunkList = new ArrayList<>();
					protected int totalSize = 0;
					private void nextChunk() {
						if(currentChunkSize == currentChunk.length) {
							chunkList.add(currentChunk);
							currentChunk = new byte[chunkSize];
							totalSize += currentChunkSize;
							currentChunkSize = 0;
						}
					}
					@Override
					public void write(int b) throws IOException {
						if(currentChunk == null) {
							throw new IOException("Stream is closed");
						}
						nextChunk();
						currentChunk[currentChunkSize++] = (byte) b;
					}
					@Override
					public void write(byte[] b, int off, int len) throws IOException {
						if(currentChunk == null) {
							throw new IOException("Stream is closed");
						}
						for(; len > 0;) {
							nextChunk();
							int nextLen = Math.min(chunkSize - currentChunkSize, len);
							System.arraycopy(b, off, currentChunk, currentChunkSize, nextLen);
							currentChunkSize += nextLen;
							off += nextLen;
							len -= nextLen;
						}
					}
					@Override
					public void close() throws IOException {
						if(currentChunk == null) {
							return;
						}
						if(currentChunkSize > 0) {
							if(currentChunkSize == currentChunk.length) {
								chunkList.add(currentChunk);
							}else {
								chunkList.add(Arrays.copyOf(currentChunk, currentChunkSize));
							}
							totalSize += currentChunkSize;
						}
						currentChunk = null;
						byte[] csum = digest.digest();
						digest = null;
						byte[] b = chunkList.get(0);
						int l = totalSize;
						int j = totalUncompressedSize;
						b[0] = (byte)(j >>> 24);
						b[1] = (byte)(j >>> 16);
						b[2] = (byte)(j >>> 8);
						b[3] = (byte)(j & 0xFF);
						ImmutableList.Builder<SPacketServerInfoDataChunkV4EAG> builder = ImmutableBuilders
								.listBuilderWithExpected(chunkList.size());
						int k = 0;
						for(byte[] bb : chunkList) {
							builder.add(new SPacketServerInfoDataChunkV4EAG(false, k++, csum, l, bb));
						}
						List<SPacketServerInfoDataChunkV4EAG> chunks = builder.build();
						chunks.get(chunks.size() - 1).lastChunk = true;
						result = new WebViewBlob(SHA1Sum.create(csum), chunks);
					}
				});
				@Override
				public void write(int b) throws IOException {
					delegate.write(b);
					digest.update((byte)b);
					++totalUncompressedSize;
				}
				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					delegate.write(b, off, len);
					digest.update(b, off, len);
					totalUncompressedSize += len;
				}
				@Override
				public void flush() throws IOException {
					delegate.flush();
				}
				@Override
				public void close() throws IOException {
					delegate.close();
				}
			};
		}catch(IOException ex) {
			throw new RuntimeException("Unexpected IOException thrown");
		}
	}

	@Override
	public T stream() {
		if(streamInstance == null) {
			streamInstance = wrap(init());
		}
		return streamInstance;
	}

	@Override
	public IWebViewBlob build() {
		if(result == null) {
			if(streamInstance != null) {
				throw new IllegalStateException("Builder stream is still open");
			}else {
				return WebViewService.zeroByteBlob;
			}
		}
		return result;
	}

	@Override
	public void close() throws IOException {
		if(streamInstance != null) {
			streamInstance.close();
		}
	}

}
