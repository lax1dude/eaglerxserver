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

package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesProperty;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class SimpleProfileCache {

	public static void loadProfile(EaglerXServer<?> resolver, File cacheFile, String usernameOrUUID, long maxAge,
			Consumer<TexturesProperty> result) {
		JsonObject cache = loadFile(cacheFile);
		if (cache != null) {
			TexturesProperty res = null;
			try {
				if (usernameOrUUID.equals(cache.get("name").getAsString())
						&& System.currentTimeMillis() - cache.get("timestamp").getAsLong() < maxAge) {
					res = TexturesProperty.create(cache.get("value").getAsString(),
							cache.get("signature").getAsString());
				}
			} catch (Exception ex) {
			}
			if (res != null) {
				result.accept(res);
				return;
			}
		}
		UUID uuid;
		try {
			uuid = UUID.fromString(usernameOrUUID);
		} catch (IllegalArgumentException ex) {
			resolver.getProfileResolver().resolveVanillaTexturesFromUsername(usernameOrUUID, (res) -> {
				if (res != null) {
					complete(resolver.logger(), cacheFile, usernameOrUUID, res, result);
				} else {
					resolver.logger().error("Could not load vanilla skin from username \"" + usernameOrUUID + "\"");
					result.accept(res);
				}
			});
			return;
		}
		resolver.getProfileResolver().resolveVanillaTexturesFromUUID(uuid, (res) -> {
			if (res != null) {
				complete(resolver.logger(), cacheFile, usernameOrUUID, res, result);
			} else {
				resolver.logger().error("Could not load vanilla skin from UUID " + uuid);
				result.accept(res);
			}
		});
	}

	private static void complete(IPlatformLogger logger, File file, String name, TexturesProperty res,
			Consumer<TexturesProperty> result) {
		JsonObject obj = new JsonObject();
		obj.addProperty("timestamp", System.currentTimeMillis());
		obj.addProperty("name", name);
		obj.addProperty("value", res.getValue());
		obj.addProperty("signature", res.getSignature());
		storeFile(logger, file, obj);
		result.accept(res);
	}

	private static JsonObject loadFile(File cacheFile) {
		try (Reader reader = new InputStreamReader(new FileInputStream(cacheFile), StandardCharsets.UTF_8)) {
			return EaglerXServer.GSON_PRETTY.fromJson(reader, JsonObject.class);
		} catch (IOException | JsonParseException ex) {
			return null;
		}
	}

	private static void storeFile(IPlatformLogger logger, File cacheFile, JsonObject object) {
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(cacheFile), StandardCharsets.UTF_8)) {
			EaglerXServer.GSON_PRETTY.toJson(object, writer);
		} catch (IOException ex) {
			logger.error("Could not save profile cache to file: " + cacheFile.getAbsolutePath(), ex);
		}
	}

}
