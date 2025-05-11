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

package net.lax1dude.eaglercraft.backend.server.config;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.lax1dude.eaglercraft.backend.server.config.gson.GSONConfigLoader;
import net.lax1dude.eaglercraft.backend.server.config.nightconfig.NightConfigLoader;
import net.lax1dude.eaglercraft.backend.server.config.snakeyaml.YAMLConfigLoader;

public enum EnumConfigFormat {

	YAML(ImmutableSet.of("yml", "yaml"), "yml", (f) -> {
		return YAMLConfigLoader.getConfigFile(f);
	}, "org.yaml.snakeyaml.Yaml"),

	TOML(ImmutableSet.of("toml"), "toml", (f) -> {
		return NightConfigLoader.getConfigFile(f);
	}, "com.electronwill.nightconfig.toml.TomlParser"),

	JSON(ImmutableSet.of("json"), "json", (f) -> {
		return GSONConfigLoader.getConfigFile(f);
	}, "com.google.gson.Gson");

	private interface IConfigProvider {
		IEaglerConfig load(File file) throws IOException;
	}

	private final Set<String> exts;
	private final String defaultExt;
	private final IConfigProvider provider;
	private final String depends;
	private int supported;

	private EnumConfigFormat(Set<String> exts, String defaultExt, IConfigProvider provider, String depends) {
		this.exts = exts;
		this.defaultExt = defaultExt;
		this.provider = provider;
		this.depends = depends;
	}

	public Set<String> getExts() {
		return exts;
	}

	public String getDefaultExt() {
		return defaultExt;
	}

	public IEaglerConfig getConfigFile(File file) throws IOException {
		return provider.load(file);
	}

	public boolean isSupported() {
		if (supported == 0) {
			supported = classAvailable(depends) ? 2 : 1;
		}
		return supported == 2;
	}

	public static Set<EnumConfigFormat> getSupported() {
		Set<EnumConfigFormat> ret = EnumSet.noneOf(EnumConfigFormat.class);
		for (EnumConfigFormat fmt : values()) {
			if (fmt.isSupported()) {
				ret.add(fmt);
			}
		}
		return ret;
	}

	private static boolean classAvailable(String name) {
		try {
			Class.forName(name);
			return true;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}

}
