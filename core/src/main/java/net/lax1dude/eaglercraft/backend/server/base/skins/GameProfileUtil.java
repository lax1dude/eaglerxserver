package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameProfileUtil {

	public final String skinURL;
	public final String skinModel;

	public final String capeURL;

	private GameProfileUtil(String skinURL, String skinModel, String capeURL) {
		this.skinURL = skinURL;
		this.skinModel = skinModel;
		this.capeURL = capeURL;
	}

	public static GameProfileUtil extractSkinAndCape(String texturesProperty) {
		try {
			String skinURL = null;
			String skinModel = null;
			String capeURL = null;
			String jsonStr = new String(Base64.getDecoder().decode(texturesProperty), StandardCharsets.UTF_8);
			JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
			JsonObject skin = json.getAsJsonObject("SKIN");
			if(skin != null) {
				JsonElement url = skin.get("url");
				if(url != null) {
					skinURL = url.getAsString();
					if(skinURL != null) {
						JsonElement el = skin.get("metadata");
						if(el != null && el.isJsonObject()) {
							el = el.getAsJsonObject().get("model");
							if(el != null) {
								skinModel = el.getAsString();
							}
						}
					}
				}
			}
			JsonObject cape = json.getAsJsonObject("CAPE");
			if(cape != null) {
				JsonElement url = cape.get("url");
				if(url != null) {
					capeURL = url.getAsString();
				}
			}
			if(skinModel == null) {
				skinModel = "default";
			}
			return new GameProfileUtil(skinURL, skinModel, capeURL);
		}catch(Exception ex) {
			return null;
		}
	}

}
