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

package net.lax1dude.eaglercraft.backend.server.config.docsutil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;
import net.lax1dude.eaglercraft.backend.server.config.IRandomSupplier;

class DocsSection implements IEaglerConfSection {

	final Map<String, Object> entries = new HashMap<>();
	private boolean initialized;
	String comment;

	DocsSection() {
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public boolean initialized() {
		return initialized;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public IEaglerConfSection getIfSection(String name) {
		Object obj = entries.get(name);
		if (obj instanceof DocsSection s) {
			return s;
		}
		return null;
	}

	@Override
	public IEaglerConfSection getSection(String name) {
		if (entries.get(name) instanceof DocsSection s) {
			return s;
		} else {
			DocsSection sec = new DocsSection();
			entries.put(name, sec);
			initialized = true;
			return sec;
		}
	}

	@Override
	public IEaglerConfList getIfList(String name) {
		Object obj = entries.get(name);
		if (obj instanceof DocsList l) {
			return l;
		}
		return null;
	}

	@Override
	public IEaglerConfList getList(String name) {
		if (entries.get(name) instanceof DocsList l) {
			return l;
		} else {
			DocsList lst = new DocsList();
			entries.put(name, lst);
			initialized = true;
			return lst;
		}
	}

	@Override
	public List<String> getKeys() {
		return ImmutableList.copyOf(entries.keySet());
	}

	@Override
	public boolean isBoolean(String name) {
		if (entries.get(name) instanceof DocsValue v) {
			return v.type == DocsValue.Type.BOOL;
		}
		return false;
	}

	@Override
	public boolean getBoolean(String name) {
		if (entries.get(name) instanceof DocsValue v && v.type == DocsValue.Type.BOOL) {
			return Boolean.parseBoolean(v.value);
		}
		return false;
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue, String comment) {
		if (entries.get(name) instanceof DocsValue v && v.type == DocsValue.Type.BOOL) {
			return Boolean.parseBoolean(v.value);
		} else {
			DocsValue val = new DocsValue(DocsValue.Type.BOOL, Boolean.toString(defaultValue), comment, false);
			entries.put(name, val);
			initialized = true;
			return defaultValue;
		}
	}

	@Override
	public boolean getBoolean(String name, Supplier<Boolean> defaultValue, String comment) {
		if (entries.get(name) instanceof DocsValue v && v.type == DocsValue.Type.BOOL) {
			return Boolean.parseBoolean(v.value);
		} else {
			boolean def = defaultValue.get();
			DocsValue val = new DocsValue(DocsValue.Type.BOOL, Boolean.toString(def), comment,
					defaultValue instanceof IRandomSupplier);
			entries.put(name, val);
			initialized = true;
			return def;
		}
	}

	@Override
	public boolean isInteger(String name) {
		if (entries.get(name) instanceof DocsValue v) {
			return v.type == DocsValue.Type.INT;
		}
		return false;
	}

	@Override
	public int getInteger(String name, int defaultValue, String comment) {
		if (entries.get(name) instanceof DocsValue v && v.type == DocsValue.Type.INT) {
			return Integer.parseInt(v.value);
		} else {
			DocsValue val = new DocsValue(DocsValue.Type.INT, Integer.toString(defaultValue), comment, false);
			entries.put(name, val);
			initialized = true;
			return defaultValue;
		}
	}

	@Override
	public int getInteger(String name, Supplier<Integer> defaultValue, String comment) {
		if (entries.get(name) instanceof DocsValue v && v.type == DocsValue.Type.INT) {
			return Integer.parseInt(v.value);
		} else {
			int def = defaultValue.get();
			DocsValue val = new DocsValue(DocsValue.Type.INT, Integer.toString(def), comment,
					defaultValue instanceof IRandomSupplier);
			entries.put(name, val);
			initialized = true;
			return def;
		}
	}

	@Override
	public boolean isString(String name) {
		if (entries.get(name) instanceof DocsValue v) {
			return v.type == DocsValue.Type.STR;
		}
		return false;
	}

	@Override
	public String getIfString(String name) {
		if (entries.get(name) instanceof DocsValue v && v.type == DocsValue.Type.STR) {
			return v.value;
		}
		return null;
	}

	@Override
	public String getString(String name, String defaultValue, String comment) {
		if (entries.get(name) instanceof DocsValue v && v.type == DocsValue.Type.STR) {
			return v.value;
		} else {
			DocsValue val = new DocsValue(DocsValue.Type.STR, defaultValue, comment, false);
			entries.put(name, val);
			initialized = true;
			return defaultValue;
		}
	}

	@Override
	public String getString(String name, Supplier<String> defaultValue, String comment) {
		if (entries.get(name) instanceof DocsValue v && v.type == DocsValue.Type.STR) {
			return v.value;
		} else {
			String def = defaultValue.get();
			DocsValue val = new DocsValue(DocsValue.Type.STR, def, comment,
					defaultValue instanceof IRandomSupplier);
			entries.put(name, val);
			initialized = true;
			return def;
		}
	}

}
