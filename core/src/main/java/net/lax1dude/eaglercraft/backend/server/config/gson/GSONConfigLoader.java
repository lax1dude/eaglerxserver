package net.lax1dude.eaglercraft.backend.server.config.gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.jline.utils.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class GSONConfigLoader {

	private static final Gson GSON = (new GsonBuilder()).setLenient().setPrettyPrinting().serializeNulls().create();

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		JsonObject obj;
		try(Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			obj = GSON.fromJson(reader, JsonObject.class);
		}catch(JsonParseException ex) {
			throw new IOException("JSON config file has a syntax error: " + file.getAbsolutePath(), ex);
		}
		return getConfigFile(obj);
	}

	public static IEaglerConfig getConfigFile(JsonObject jsonObject) throws IOException {
		GSONConfigBase base = new GSONConfigBase();
		base.root = new GSONConfigSection(base, jsonObject, jsonObject.size() > 0);
		return base;
	}

	public static void writeConfigFile(JsonObject configIn, File file) throws IOException {
		try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			GSON.toJson(configIn, writer);
		}
	}

}
