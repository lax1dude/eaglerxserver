package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesResult;

public class GameProfileUtil {

	public static TexturesResult extractSkinAndCape(String texturesProperty) {
		try {
			String skinURL = null;
			String skinModel = null;
			String capeURL = null;
			String jsonStr = new String(Base64.getDecoder().decode(texturesProperty), StandardCharsets.UTF_8);
			JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject().getAsJsonObject("textures");
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
			return TexturesResult.create(skinURL, "slim".equals(skinModel) ? EnumSkinModel.ALEX : EnumSkinModel.STEVE, capeURL);
		}catch(Exception ex) {
			return null;
		}
	}

}
