/*
 * Copyright (c) 2026 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.server.base.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Files;

import net.lax1dude.eaglercraft.backend.server.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServerVersion;
import net.lax1dude.eaglercraft.backend.server.config.docsutil.DocsDirectory;
import net.lax1dude.eaglercraft.backend.server.config.docsutil.DocsGenerator;

public class EaglerConfigDocs {

	public static void main(String[] args) throws IOException {
		DocsGenerator gen = new DocsGenerator(
			"EaglerXServer Config Reference",
			"Generated from the source code of EaglerXServer " + EaglerXServerVersion.VERSION
		);
		gen.setPrimaryFile("settings");
		addPlatform(gen, EnumAdapterPlatformType.BUKKIT, "Bukkit");
		addPlatform(gen, EnumAdapterPlatformType.BUNGEE, "BungeeCord");
		addPlatform(gen, EnumAdapterPlatformType.VELOCITY, "Velocity");
		File file = new File(args[0]);
		Files.createParentDirs(file);
		try (PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			gen.writeDocs(writer);
		}
	}

	private static void addPlatform(DocsGenerator gen, EnumAdapterPlatformType platform, String name) throws IOException {
		gen.addPlatform(name, dir -> EaglerConfigLoader.loadConfig(new DocDirAdapter(dir), platform, NullLogger.INSTANCE));
	}

	static class DocDirAdapter implements IConfigDirectory {

		private final DocsDirectory dir;

		DocDirAdapter(DocsDirectory dir) {
			this.dir = dir;
		}

		@Override
		public File getBaseDir() {
			return null;
		}

		@Override
		public <T> T loadConfig(String fileName, IConfigLoadFunction<T> func) throws IOException {
			return dir.addFile(fileName, func::call);
		}

	}

	static class NullLogger implements IPlatformLogger {

		static final NullLogger INSTANCE = new NullLogger();

		@Override
		public void info(String msg) {
		}

		@Override
		public void info(String msg, Throwable thrown) {
		}

		@Override
		public void warn(String msg) {
		}

		@Override
		public void warn(String msg, Throwable thrown) {
		}

		@Override
		public void error(String msg) {
		}

		@Override
		public void error(String msg, Throwable thrown) {
		}

		@Override
		public IPlatformSubLogger createSubLogger(String name) {
			throw new UnsupportedOperationException();
		}

	}

}
