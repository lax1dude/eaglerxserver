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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import net.lax1dude.eaglercraft.backend.skin_cache.SkinCacheTable.SkinCacheTableThreadEnv;
import net.lax1dude.eaglercraft.backend.util.ILoggerAdapter;
import net.lax1dude.eaglercraft.backend.util.SteadyTime;

public class SkinCacheDatastore implements ISkinCacheDatastore {

	public static final int SKIN_LENGTH = 12288;
	public static final int CAPE_LENGTH = 1173;

	protected final ILoggerAdapter logger;
	protected final SkinCacheDatastoreThreadEnv[] threads;
	protected final Semaphore semaphore = new Semaphore(0);
	protected final ConcurrentLinkedQueue<SkinCacheDatastoreRunnable> databaseQueue = new ConcurrentLinkedQueue<>();
	protected final Connection conn;
	protected volatile boolean disposed;
	protected final CountDownLatch disposeLatch;

	protected long lastCleanup = 0l;
	protected int keepObjectsDays;
	protected int maxObjects;
	protected final boolean sqliteCompatible;

	protected final SkinCacheTable skin;
	protected final SkinCacheTable cape;

	private class SkinCacheDatastoreThreadEnv {

		protected final Thread thread;
		protected final SkinCacheTableThreadEnv skinEnv;
		protected final SkinCacheTableThreadEnv capeEnv;
		protected final byte[] compressionTmp;
		protected final Deflater deflater;
		protected final Inflater inflater;
		protected final MessageDigest sha1Digest;

		protected SkinCacheDatastoreThreadEnv(int i, int compressionLevel, Connection conn) throws SQLException {
			skinEnv = skin.createThreadEnv(conn);
			capeEnv = cape.createThreadEnv(conn);
			compressionTmp = new byte[65535];
			deflater = compressionLevel > 0 ? new Deflater(compressionLevel) : null;
			inflater = compressionLevel > 0 ? new Inflater() : null;
			try {
				sha1Digest = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException ex) {
				throw new RuntimeException("This JRE does not support SHA-1!", ex);
			}
			thread = new Thread(() -> {
				while (!disposed) {
					try {
						semaphore.acquire();
						SkinCacheDatastoreRunnable runnable = databaseQueue.poll();
						if (runnable != null) {
							runnable.run(this);
						}
					} catch (Throwable ex) {
						logger.error("Caught exception in worker thread #" + (i + 1), ex);
					}
				}
				dispose();
				disposeLatch.countDown();
			}, "SkinCacheDatastore Thread #" + (i + 1));
			thread.setDaemon(true);
			thread.start();
		}

		public void dispose() {
			skinEnv.dispose();
			capeEnv.dispose();
			if (deflater != null) {
				deflater.end();
			}
			if (inflater != null) {
				inflater.end();
			}
		}

	}

	private interface SkinCacheDatastoreRunnable {
		void run(SkinCacheDatastoreThreadEnv env);
	}

	public SkinCacheDatastore(Connection conn, int threadCount, int keepObjectsDays, int maxObjects,
			int compressionLevel, boolean sqliteCompatible, ILoggerAdapter logger) throws SQLException {
		this.conn = conn;
		this.keepObjectsDays = keepObjectsDays;
		this.maxObjects = maxObjects;
		this.sqliteCompatible = sqliteCompatible;
		this.logger = logger;
		skin = new SkinCacheTable("eagler_skins", conn, sqliteCompatible, logger);
		cape = new SkinCacheTable("eagler_capes", conn, sqliteCompatible, logger);
		disposed = false;
		disposeLatch = new CountDownLatch(threadCount);
		threads = new SkinCacheDatastoreThreadEnv[threadCount];
		for (int i = 0; i < threadCount; ++i) {
			threads[i] = new SkinCacheDatastoreThreadEnv(i, compressionLevel, conn);
		}
	}

	private void execute(SkinCacheDatastoreRunnable runnable) {
		databaseQueue.add(runnable);
		semaphore.release();
	}

	@Override
	public void loadSkin(String skinURL, Consumer<byte[]> callback) {
		execute((env) -> {
			byte[] result;
			try {
				result = skin.loadSkin(env.skinEnv, skinURL);
			} catch (SQLException ex) {
				logger.error("Could not load skin \"" + skinURL + "\" from database!");
				callback.accept(null);
				return;
			}
			if (result != null) {
				byte[] res;
				try {
					res = decompressSkin(env, result, SKIN_LENGTH);
				} catch (DataFormatException ex) {
					logger.warn("Skin \"" + skinURL + "\" could not be decompressed!");
					callback.accept(null);
					return;
				}
				callback.accept(res);
			} else {
				callback.accept(null);
			}
		});
	}

	@Override
	public void loadCape(String capeURL, Consumer<byte[]> callback) {
		execute((env) -> {
			byte[] result;
			try {
				result = cape.loadSkin(env.capeEnv, capeURL);
			} catch (SQLException ex) {
				logger.error("Could not load cape \"" + capeURL + "\" from database!");
				callback.accept(null);
				return;
			}
			if (result != null) {
				byte[] res;
				try {
					res = decompressSkin(env, result, CAPE_LENGTH);
				} catch (DataFormatException ex) {
					logger.warn("Cape \"" + capeURL + "\" could not be decompressed!");
					callback.accept(null);
					return;
				}
				callback.accept(res);
			} else {
				callback.accept(null);
			}
		});
	}

	private byte[] decompressSkin(SkinCacheDatastoreThreadEnv env, byte[] input, int len) throws DataFormatException {
		if (env.inflater == null && input.length == len) {
			return input;
		}
		byte[] ret = new byte[len];
		env.inflater.reset();
		env.inflater.setInput(input, 0, input.length);
		if (env.inflater.inflate(ret, 0, len) != len) {
			throw new DataFormatException();
		}
		return ret;
	}

	@Override
	public void storeSkin(String skinURL, byte[] data) {
		if (data.length != SKIN_LENGTH) {
			throw new IllegalArgumentException("Skin length is not " + SKIN_LENGTH + " bytes!");
		}
		execute((env) -> {
			try {
				skin.storeSkin(env.skinEnv, skinURL, sha1Digest(env, data), compressSkin(env, data));
			} catch (IllegalStateException | SQLException e) {
				logger.error("Skin \"" + skinURL + "\" could not be stored in the database!", e);
			}
		});
	}

	@Override
	public void storeCape(String capeURL, byte[] data) {
		if (data.length != CAPE_LENGTH) {
			throw new IllegalArgumentException("Cape length is not " + CAPE_LENGTH + " bytes!");
		}
		execute((env) -> {
			try {
				cape.storeSkin(env.capeEnv, capeURL, sha1Digest(env, data), compressSkin(env, data));
			} catch (IllegalStateException | SQLException e) {
				logger.error("Cape \"" + capeURL + "\" could not be stored in the database!", e);
			}
		});
	}

	private byte[] compressSkin(SkinCacheDatastoreThreadEnv env, byte[] data) {
		if (env.deflater == null) {
			return data;
		}
		env.deflater.reset();
		env.deflater.setInput(data, 0, data.length);
		env.deflater.finish();
		int i = env.deflater.deflate(env.compressionTmp, 0, env.compressionTmp.length);
		if (i <= 0) {
			throw new IllegalStateException();
		}
		return Arrays.copyOf(env.compressionTmp, i);
	}

	private byte[] sha1Digest(SkinCacheDatastoreThreadEnv env, byte[] data) {
		env.sha1Digest.update(data);
		return env.sha1Digest.digest();
	}

	@Override
	public void tick() {
		long millisSteady = SteadyTime.millis();
		if (millisSteady - lastCleanup > (600l * 1000l)) {
			lastCleanup = millisSteady;
			try {
				runCleanup();
			} catch (SQLException ex) {
				logger.error("Could not clean up skin cache!", ex);
			}
		}
	}

	private synchronized void runCleanup() throws SQLException {
		long millis = System.currentTimeMillis();
		long expiry = millis - keepObjectsDays * 86400000l;
		skin.runCleanup(maxObjects, expiry);
		cape.runCleanup(maxObjects, expiry);
	}

	@Override
	public void dispose() {
		disposed = true;
		databaseQueue.clear();
		semaphore.release(threads.length);
		try {
			disposeLatch.await();
		} catch (InterruptedException e) {
		}
		skin.dispose();
		cape.dispose();
	}

	static void disposeStmt(PreparedStatement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
			}
		}
	}

	@Override
	public synchronized int getTotalStoredSkins() {
		return skin.countSkins();
	}

	@Override
	public synchronized int getTotalStoredCapes() {
		return cape.countSkins();
	}

}