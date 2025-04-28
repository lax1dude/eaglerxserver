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

package net.lax1dude.eaglercraft.backend.eaglermotd.base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.eaglermotd.base.util.GsonUtil;

public class QueryCache {

	private static class CachedFile {
		protected final String name;
		protected final File file;
		protected long lastReload;
		protected long lastRescan;

		protected CachedFile(String name, File file) {
			this.name = name;
			this.file = file;
			this.lastReload = this.lastRescan = 0l;
		}

		protected boolean needsReload() {
			long l = System.currentTimeMillis();
			if (l - lastRescan > 5000l) {
				lastRescan = l;
				return file.exists() && lastReload < file.lastModified();
			} else {
				return false;
			}
		}
	}

	private static class CachedFileBinary extends CachedFile {
		protected byte[] bytes = null;

		protected CachedFileBinary(String name, File file) {
			super(name, file);
		}

		protected byte[] getOrReload() {
			synchronized (this) {
				if (needsReload()) {
					if (file.exists()) {
						try (FileInputStream fis = new FileInputStream(file)) {
							ByteArrayOutputStream read = new ByteArrayOutputStream(fis.available());
							byte[] d = new byte[8192];
							int i;
							while ((i = fis.read(d)) != -1) {
								read.write(d, 0, i);
							}
							lastRescan = lastReload = System.currentTimeMillis();
							bytes = read.toByteArray();
						} catch (Throwable t) {
							bytes = null;
							System.err.println("[EaglerMOTD] Failed to load binary: " + name);
							t.printStackTrace();
						}
					}
				}
				return bytes;
			}
		}
	}

	private static class CachedFileString extends CachedFile {
		protected String chars = null;

		protected CachedFileString(String name, File file) {
			super(name, file);

		}

		protected String getOrReload() {
			synchronized (this) {
				if (needsReload()) {
					if (file.exists()) {
						try (FileInputStream fis = new FileInputStream(file)) {
							ByteArrayOutputStream read = new ByteArrayOutputStream(fis.available());
							byte[] d = new byte[8192];
							int i;
							while ((i = fis.read(d)) != -1) {
								read.write(d, 0, i);
							}
							lastRescan = lastReload = System.currentTimeMillis();
							chars = new String(read.toByteArray(), StandardCharsets.UTF_8);
						} catch (Exception t) {
							chars = null;
							System.err.println("[EaglerMOTD] Failed to load text: " + name);
							t.printStackTrace();
						}
					}
				}
				return chars;
			}
		}
	}

	private static class CachedFileJSON extends CachedFile {
		protected JsonObject json = null;

		protected CachedFileJSON(String name, File file) {
			super(name, file);

		}

		protected JsonObject getOrReload() {
			synchronized (this) {
				if (needsReload()) {
					if (file.exists()) {
						try {
							json = GsonUtil.loadJSONFile(file);
						} catch (Exception t) {
							json = null;
							System.err.println("[EaglerMOTD] Failed to load json: " + name);
							t.printStackTrace();
						}
					}
				}
				return json;
			}
		}
	}

	private static final HashMap<String, CachedFileBinary> cachedBinary = new HashMap<>();
	private static final HashMap<String, CachedFileString> cachedString = new HashMap<>();
	private static final HashMap<String, CachedFileJSON> cachedJSON = new HashMap<>();

	public static void flush() {
		cachedBinary.clear();
		cachedString.clear();
		cachedJSON.clear();
	}

	public static byte[] getBinaryFile(String s) {
		CachedFileBinary fb;
		synchronized (cachedBinary) {
			fb = cachedBinary.get(s);
			if (fb == null) {
				fb = new CachedFileBinary(s, new File(s));
				cachedBinary.put(s, fb);
			}
		}
		return fb.getOrReload();
	}

	public static String getStringFile(String s) {
		CachedFileString fb;
		synchronized (cachedString) {
			fb = cachedString.get(s);
			if (fb == null) {
				fb = new CachedFileString(s, new File(s));
				cachedString.put(s, fb);
			}
		}
		return fb.getOrReload();
	}

	public static JsonObject getJSONFile(String s) {
		CachedFileJSON fb;
		synchronized (cachedJSON) {
			fb = cachedJSON.get(s);
			if (fb == null) {
				fb = new CachedFileJSON(s, new File(s));
				cachedJSON.put(s, fb);
			}
		}
		return fb.getOrReload();
	}

}
