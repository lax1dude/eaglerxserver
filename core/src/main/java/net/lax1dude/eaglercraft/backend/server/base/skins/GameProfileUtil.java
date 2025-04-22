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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesResult;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class GameProfileUtil {

	public static TexturesResult extractSkinAndCape(String texturesProperty) {
		try {
			String skinURL = null;
			String skinModel = null;
			String capeURL = null;
			String jsonStr = new String(Base64.getDecoder().decode(texturesProperty), StandardCharsets.UTF_8);
			JsonObject json = EaglerXServer.GSON_PRETTY.fromJson(jsonStr, JsonObject.class).getAsJsonObject("textures");
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
