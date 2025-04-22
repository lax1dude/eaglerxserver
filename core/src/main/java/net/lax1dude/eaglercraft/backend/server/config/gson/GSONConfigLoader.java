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

package net.lax1dude.eaglercraft.backend.server.config.gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;
import net.lax1dude.eaglercraft.backend.server.util.GsonLenient;

public class GSONConfigLoader {

	private static final Gson GSON = GsonLenient.setLenient(new GsonBuilder()).setPrettyPrinting().serializeNulls().create();

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		JsonObject obj;
		try(Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			obj = GSON.fromJson(reader, JsonObject.class);
		}catch(FileNotFoundException ex) {
			obj = new JsonObject();
		}catch(JsonParseException ex) {
			throw new IOException("JSON config file has a syntax error: " + file.getAbsolutePath(), ex);
		}
		return getConfigFile(file, obj);
	}

	public static IEaglerConfig getConfigFile(File file, JsonObject jsonObject) throws IOException {
		GSONConfigBase base = new GSONConfigBase(file);
		base.root = new GSONConfigSection(base, jsonObject, jsonObject.size() > 0);
		return base;
	}

	public static void writeConfigFile(JsonObject configIn, File file) throws IOException {
		File p = file.getAbsoluteFile().getParentFile();
		if(p != null && !p.isDirectory() && !p.mkdirs()) {
			throw new IOException("Could not create directory: " + p.getAbsolutePath());
		}
		try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			GSON.toJson(configIn, writer);
		}
	}

}
