package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.server.api.skins.IProfileResolver;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesProperty;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesResult;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.util.CharSequenceReader;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.backend.skin_cache.HTTPClient;

public class ProfileResolver implements IProfileResolver {

	private final EaglerXServer<?> server;
	private final HTTPClient httpClient;

	public ProfileResolver(EaglerXServer<?> server, HTTPClient httpClient) {
		this.server = server;
		this.httpClient = httpClient;
	}

	@Override
	public void resolveVanillaUUIDFromUsername(String username, Consumer<UUID> callback) {
		httpClient.asyncRequest("GET", URI.create("https://api.mojang.com/users/profiles/minecraft/"
				+ URLEncoder.encode(username, StandardCharsets.UTF_8)), (response) -> {
			if(response == null) {
				callback.accept(null);
			}else if(response.exception != null) {
				server.logger().error("Exception loading vanilla profile UUID of \"" + username + "\"!", response.exception);
				callback.accept(null);
			}else {
				try {
					if(response.code != 200) {
						server.logger().error("Recieved code " + response.code + " while looking up UUID of \"" + username + "\"");
						callback.accept(null);
					}else if (response.data == null) {
						server.logger().error("Recieved null payload while looking up UUID of \"" + username + "\"");
						callback.accept(null);
					}else {
						UUID uuid;
						try {
							JsonObject json = JsonParser.parseReader(new CharSequenceReader(response.data.readCharSequence(
										response.data.readableBytes(), StandardCharsets.UTF_8))).getAsJsonObject();
							uuid = Util.createUUIDFromUndashed(json.get("id").getAsString());
						}catch(Exception t) {
							server.logger().error("Exception processing name to UUID lookup response for \"" + username + "\"!", t);
							callback.accept(null);
							return;
						}
						callback.accept(uuid);
					}
				}finally {
					ReferenceCountUtil.release(response.data);
				}
			}
		});
	}

	@Override
	public void resolveVanillaTexturesFromUUID(UUID uuid, Consumer<TexturesProperty> callback) {
		httpClient.asyncRequest("GET", URI.create("https://sessionserver.mojang.com/session/minecraft/profile/"
				+ Util.toUUIDStringUndashed(uuid) + "?unsigned=false"), (response) -> {
			if(response == null) {
				callback.accept(null);
			}else if(response.exception != null) {
				server.logger().error("Exception loading profile " + uuid + "!", response.exception);
				callback.accept(null);
			}else {
				try {
					if(response.code != 200) {
						server.logger().error("Recieved code " + response.code + " while looking up profile of " + uuid);
						callback.accept(null);
					}else if (response.data == null) {
						server.logger().error("Recieved null payload while looking up profile of " + uuid);
						callback.accept(null);
					}else {
						TexturesProperty result = null;
						try {
							JsonObject json = JsonParser.parseReader(new CharSequenceReader(response.data.readCharSequence(
									response.data.readableBytes(), StandardCharsets.UTF_8))).getAsJsonObject();
							JsonElement propsElement = json.get("properties");
							if(propsElement != null) {
								JsonArray properties = propsElement.getAsJsonArray();
								if(properties.size() > 0) {
									for(int i = 0, l = properties.size(); i < l; ++i) {
										JsonElement prop = properties.get(i);
										if(prop.isJsonObject()) {
											JsonObject propObj = prop.getAsJsonObject();
											if(propObj.get("name").getAsString().equals("textures")) {
												JsonElement value = propObj.get("value");
												JsonElement signature = propObj.get("signature");
												if(value != null && signature != null) {
													String v = value.getAsString();
													String s = signature.getAsString();
													if(v != null && s != null) {
														result = TexturesProperty.create(v, s);
														break;
													}
												}
											}
										}
									}
								}
							}
						}catch(Exception t) {
							server.logger().error("Exception processing name to UUID lookup response!", t);
							callback.accept(null);
							return;
						}
						callback.accept(result);
					}
				}finally {
					ReferenceCountUtil.release(response.data);
				}
			}
		});
	}

	@Override
	public TexturesResult decodeVanillaTextures(String propertyValue) {
		return GameProfileUtil.extractSkinAndCape(propertyValue);
	}

}
