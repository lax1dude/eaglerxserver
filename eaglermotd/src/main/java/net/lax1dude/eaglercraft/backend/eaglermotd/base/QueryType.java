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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;

public class QueryType {

	public final String name;
	public final String type;

	public final String dataString;
	public final String dataJSONFile;
	public final JsonObject dataJSONObject;
	public final String dataTextFile;
	public final String dataBinaryFile;

	public QueryType(String name, JsonObject tag) {
		this.name = name;
		JsonElement el = tag.get("json");
		if (el == null || !el.isJsonObject()) {
			this.dataJSONObject = null;
			this.dataJSONFile = optString(tag.get("json"), null);
			if (this.dataJSONFile == null) {
				this.dataTextFile = optString(tag.get("txt"), null);
				if (this.dataTextFile == null) {
					this.dataString = optString(tag.get("string"), null);
				} else {
					this.dataString = null;
				}
			} else {
				this.dataTextFile = null;
				this.dataString = null;
			}
		} else {
			this.dataJSONObject = el.getAsJsonObject();
			this.dataJSONFile = null;
			this.dataTextFile = null;
			this.dataString = null;
		}
		this.dataBinaryFile = optString(tag.get("file"), null);
		String t = optString(tag.get("type"), null);
		if (t == null) {
			if (this.dataJSONObject != null || this.dataJSONFile != null) {
				t = "json";
			} else if (this.dataString != null || this.dataTextFile != null) {
				t = "text";
			} else {
				t = "binary";
			}
		}
		this.type = t;
	}

	public void doQuery(IQueryConnection query) {
		byte[] bin = null;
		if (dataBinaryFile != null) {
			bin = QueryCache.getBinaryFile(dataBinaryFile);
			if (bin == null) {
				query.sendResponse("error",
						"Error: could not load binary file '" + dataBinaryFile + "' for query '" + type + "'");
				return;
			}
		}
		boolean flag = false;
		if (dataJSONObject != null) {
			query.sendResponse(type, dataJSONObject);
			flag = true;
		} else if (dataJSONFile != null) {
			JsonObject obj = QueryCache.getJSONFile(dataJSONFile);
			if (obj == null) {
				query.sendResponse("error",
						"Error: could not load or parse JSON file '" + dataJSONFile + "' for query '" + type + "'");
				return;
			} else {
				query.sendResponse(type, obj);
				flag = true;
			}
		} else if (dataTextFile != null) {
			String txt = QueryCache.getStringFile(dataTextFile);
			if (txt == null) {
				query.sendResponse("error",
						"Error: could not load text file '" + dataTextFile + "' for query '" + type + "'");
				return;
			} else {
				query.sendResponse(type, txt);
				flag = true;
			}
		} else if (dataString != null) {
			query.sendResponse(type, dataString);
			flag = true;
		}
		if (!flag) {
			if (bin != null) {
				JsonObject json = new JsonObject();
				json.addProperty("binary", true);
				json.addProperty("file", dataBinaryFile);
				json.addProperty("size", bin.length);
				query.sendResponse(type, json);
			} else {
				query.sendResponse(type, "<No Content>");
			}
		}
		if (bin != null) {
			query.send(bin);
		}
	}

	private static String optString(JsonElement el, String def) {
		if (el == null || !el.isJsonPrimitive()) {
			return def;
		} else {
			JsonPrimitive pr = el.getAsJsonPrimitive();
			return pr.isString() ? pr.getAsString() : def;
		}
	}

}
