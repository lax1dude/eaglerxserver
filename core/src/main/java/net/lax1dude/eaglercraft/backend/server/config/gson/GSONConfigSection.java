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

import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

public class GSONConfigSection implements IEaglerConfSection {

	private final GSONConfigBase owner;
	final JsonObject json;
	private final boolean exists;
	private boolean initialized;

	protected GSONConfigSection(GSONConfigBase owner, JsonObject json, boolean exists) {
		this.owner = owner;
		this.json = json;
		this.exists = this.initialized = exists;
	}

	@Override
	public boolean exists() {
		return exists;
	}

	@Override
	public boolean initialized() {
		return initialized;
	}

	@Override
	public void setComment(String comment) {
	}

	@Override
	public IEaglerConfSection getIfSection(String name) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonObject()) {
			return new GSONConfigSection(owner, el.getAsJsonObject(), true);
		}else {
			return null;
		}
	}

	@Override
	public IEaglerConfSection getSection(String name) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonObject()) {
			return new GSONConfigSection(owner, el.getAsJsonObject(), true);
		}else {
			JsonObject obj = new JsonObject();
			json.add(name, obj);
			owner.modified = true;
			initialized = true;
			return new GSONConfigSection(owner, obj, false);
		}
	}

	@Override
	public IEaglerConfList getIfList(String name) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonArray()) {
			return new GSONConfigList(owner, el.getAsJsonArray(), true);
		}else {
			return null;
		}
	}

	@Override
	public IEaglerConfList getList(String name) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonArray()) {
			return new GSONConfigList(owner, el.getAsJsonArray(), true);
		}else {
			JsonArray obj = new JsonArray();
			json.add(name, obj);
			owner.modified = true;
			initialized = true;
			return new GSONConfigList(owner, obj, false);
		}
	}

	@Override
	public List<String> getKeys() {
		return ImmutableList.copyOf(json.keySet());
	}

	@Override
	public boolean isBoolean(String name) {
		JsonElement el = json.get(name);
		return el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isBoolean();
	}

	@Override
	public boolean getBoolean(String name) {
		JsonElement el = json.get(name);
		return el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isBoolean()
				&& el.getAsBoolean();
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue, String comment) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isBoolean()) {
			return el.getAsBoolean();
		}else {
			json.addProperty(name, defaultValue);
			owner.modified = true;
			initialized = true;
			return defaultValue;
		}
	}

	@Override
	public boolean getBoolean(String name, Supplier<Boolean> defaultValue, String comment) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isBoolean()) {
			return el.getAsBoolean();
		}else {
			boolean b = defaultValue.get();
			json.addProperty(name, b);
			owner.modified = true;
			initialized = true;
			return b;
		}
	}

	@Override
	public boolean isInteger(String name) {
		JsonElement el = json.get(name);
		return el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber();
	}

	@Override
	public int getInteger(String name, int defaultValue, String comment) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber()) {
			return el.getAsJsonPrimitive().getAsInt();
		}else {
			json.addProperty(name, defaultValue);
			owner.modified = true;
			initialized = true;
			return defaultValue;
		}
	}

	@Override
	public int getInteger(String name, Supplier<Integer> defaultValue, String comment) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber()) {
			return el.getAsJsonPrimitive().getAsInt();
		}else {
			Integer i = defaultValue.get();
			json.addProperty(name, i);
			owner.modified = true;
			initialized = true;
			return i;
		}
	}

	@Override
	public boolean isString(String name) {
		JsonElement el = json.get(name);
		return el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isString();
	}

	@Override
	public String getIfString(String name) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
			return el.getAsString();
		}else {
			return null;
		}
	}

	@Override
	public String getString(String name, String defaultValue, String comment) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
			return el.getAsString();
		}else {
			json.addProperty(name, defaultValue);
			owner.modified = true;
			initialized = true;
			return defaultValue;
		}
	}

	@Override
	public String getString(String name, Supplier<String> defaultValue, String comment) {
		JsonElement el = json.get(name);
		if(el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
			return el.getAsString();
		}else {
			String d = defaultValue.get();
			json.addProperty(name, d);
			owner.modified = true;
			initialized = true;
			return d;
		}
	}

}
