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

package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

class ResponseCache {

	static final byte[] ERROR = new byte[0];

	class ResponseLoader {

		private static final VarHandle RESULT_HANDLE;

		static {
			try {
				MethodHandles.Lookup l = MethodHandles.lookup();
				RESULT_HANDLE = l.findVarHandle(ResponseLoader.class, "data", byte[].class);
			} catch (ReflectiveOperationException e) {
				throw new ExceptionInInitializerError(e);
			}
		}

		private final ResponseCacheKey key;
		private volatile byte[] data;
		private List<Consumer<byte[]>> waitingCallbacks;

		protected ResponseLoader(ResponseCacheKey key) {
			this.key = key;
		}

		byte[] tryGetResponse() {
			return (byte[]) RESULT_HANDLE.getAcquire(this);
		}

		void loadResponse(Consumer<byte[]> consumer) {
			byte[] data = (byte[]) RESULT_HANDLE.getAcquire(this);
			if (data == null) {
				eagler: {
					synchronized (this) {
						data = (byte[]) RESULT_HANDLE.getAcquire(this);
						if (data != null) {
							break eagler;
						}
						if (waitingCallbacks == null) {
							waitingCallbacks = new ArrayList<>(4);
							waitingCallbacks.add(consumer);
						} else {
							waitingCallbacks.add(consumer);
							return;
						}
					}
					ResponseCache.this.loadFileAsync(key.getFile(), (data0) -> {
						if (data0 == null) {
							data0 = ERROR;
						}
						List<Consumer<byte[]>> cb;
						synchronized (this) {
							if ((byte[]) RESULT_HANDLE.getAcquire(this) != null) {
								return;
							}
							RESULT_HANDLE.setRelease(this, data0);
							cb = waitingCallbacks;
							waitingCallbacks = null;
						}
						if (cb != null) {
							for (int i = 0, l = cb.size(); i < l; ++i) {
								cb.get(i).accept(data0);
							}
						}
					});
					return;
				}
			}
			consumer.accept(data);
		}

	}

	private static final int MAX_BUFFER_SIZE = 4 * 1024 * 1024;

	private class ResponseLoaderContext {

		protected final Thread thread;
		protected byte[] loaderBuffer;

		protected ResponseLoaderContext(int i) {
			loaderBuffer = new byte[1024 * 1024];
			thread = new Thread(() -> {
				while (!disposed) {
					try {
						semaphore.acquire();
						ResponseLoaderRunnable runnable = queue.poll();
						if (runnable != null) {
							runnable.run(this);
						}
					} catch (Throwable ex) {
						logger.error("Caught exception in worker thread #" + (i + 1), ex);
					}
				}
				disposeLatch.countDown();
			}, "EaglerWeb IO Thread #" + (i + 1));
			thread.setDaemon(true);
			thread.start();
		}

		protected byte[] loadFileAsByte(File file) {
			byte[] buf = loaderBuffer;
			int len = buf.length;
			int i, j = 0;
			try (InputStream is = new FileInputStream(file)) {
				while ((i = is.read(buf, j, len - j)) != -1) {
					j += i;
					if (j >= len) {
						if (len >= (Integer.MAX_VALUE >> 1)) {
							throw new IOException("File is too large: " + file.getAbsolutePath());
						}
						int newLen = (len << 1);
						byte[] newBuf = new byte[newLen];
						System.arraycopy(buf, 0, newBuf, 0, len);
						buf = newBuf;
						len = newLen;
						if (newLen <= MAX_BUFFER_SIZE) {
							loaderBuffer = buf;
						}
					}
				}
			} catch (IOException ex) {
				logger.error("Could not load file: " + file.getAbsolutePath(), ex);
				return null;
			}
			return Arrays.copyOf(buf, j);
		}

	}

	private interface ResponseLoaderRunnable {
		void run(ResponseLoaderContext ctx);
	}

	protected final LoadingCache<ResponseCacheKey, ResponseLoader> cache;
	protected final IEaglerWebLogger logger;
	protected volatile boolean disposed = false;
	protected final ResponseLoaderContext[] threads;
	protected final Semaphore semaphore = new Semaphore(0);
	protected final ConcurrentLinkedQueue<ResponseLoaderRunnable> queue = new ConcurrentLinkedQueue<>();
	protected final CountDownLatch disposeLatch;

	ResponseCache(long expiresAfter, int maxCacheFiles, int threadCount, IEaglerWebLogger loggerIn) {
		logger = loggerIn;
		cache = CacheBuilder.newBuilder().concurrencyLevel(8).expireAfterWrite(expiresAfter, TimeUnit.MILLISECONDS)
				.initialCapacity(Math.min(256, maxCacheFiles)).maximumSize(maxCacheFiles)
				.build(new CacheLoader<ResponseCacheKey, ResponseLoader>() {
					@Override
					public ResponseLoader load(ResponseCacheKey key) throws Exception {
						return new ResponseLoader(key);
					}
				});
		disposeLatch = new CountDownLatch(threadCount);
		threads = new ResponseLoaderContext[threadCount];
	}

	ResponseLoader loadResponse(ResponseCacheKey key) {
		try {
			return cache.get(key);
		} catch (ExecutionException e) {
			Throwable t = e.getCause();
			if (t instanceof RuntimeException ee)
				throw ee;
			throw new RuntimeException(e);
		}
	}

	ResponseCache start() {
		for (int i = 0; i < threads.length; ++i) {
			threads[i] = new ResponseLoaderContext(i);
		}
		return this;
	}

	protected void loadFileAsync(File file, Consumer<byte[]> callback) {
		ResponseLoaderRunnable runnable = (ctx) -> {
			callback.accept(ctx.loadFileAsByte(file));
		};
		queue.add(runnable);
		semaphore.release();
	}

	void dispose() {
		disposed = true;
		queue.clear();
		semaphore.release(threads.length);
		try {
			disposeLatch.await();
		} catch (InterruptedException e) {
		}
	}

}
