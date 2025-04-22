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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.config.ConfigDataSettings.ConfigDataBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.config.ConfigDataSettings.ConfigDataBackendVoice;

public class BackendRPCConfigLoader {

	@SuppressWarnings("unchecked")
	public static ConfigDataRoot loadConfig(File dir) throws IOException {
		if(!dir.isDirectory() && !dir.mkdirs()) {
			throw new IOException("Failed to create config directory: " + dir.getAbsolutePath());
		}
		Yaml yaml = new Yaml();
		Map<String, Object> map = loadConfigFile(yaml, dir, "settings");
		boolean forceModernized = (boolean)map.getOrDefault("force_modernized_channel_names", Boolean.FALSE);
		Map<String, Object> mapRPC = (Map<String, Object>) map.get("backend_rpc");
		int baseRequestTimeout = ((Number)mapRPC.getOrDefault("base_request_timeout_sec", 10)).intValue();
		double timeoutResolutionSec = ((Number)mapRPC.getOrDefault("timeout_resolution_sec", 0.25)).doubleValue();
		Map<String, Object> mapVoice = (Map<String, Object>) map.get("backend_voice");
		boolean backendVoice = (boolean)mapVoice.getOrDefault("enable_backend_voice_service", Boolean.FALSE);
		map = loadConfigFile(yaml, dir, "ice_servers");
		boolean replaceICE = (boolean)map.getOrDefault("replace_ice_server_list", Boolean.FALSE);
		ImmutableList.Builder<ICEServerEntry> builder = ImmutableList.builder();
		List<Object> noPasswdList = (List<Object>) map.get("ice_servers_no_passwd");
		for(Object obj : noPasswdList) {
			builder.add(ICEServerEntry.create((String)obj));
		}
		List<Map<String, Object>> passwdList = (List<Map<String, Object>>) map.get("ice_servers_passwd");
		for(Map<String, Object> obj : passwdList) {
			builder.add(ICEServerEntry.create((String) obj.get("url"), (String) obj.get("username"),
					(String) obj.get("password")));
		}
		List<ICEServerEntry> lst = builder.build();
		return new ConfigDataRoot(new ConfigDataSettings(forceModernized,
				new ConfigDataBackendRPC(baseRequestTimeout, timeoutResolutionSec),
				new ConfigDataBackendVoice(backendVoice)), new ConfigDataICEServers(replaceICE, lst));
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> loadConfigFile(Yaml yaml, File folder, String file) throws IOException {
		File f = new File(folder, file + ".yml");
		if(!f.isFile()) {
			try (InputStream is = BackendRPCConfigLoader.class.getResourceAsStream("default_" + file + ".yml");
					OutputStream os = new FileOutputStream(f)) {
				is.transferTo(os);
			}
		}
		try(Reader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
			return yaml.loadAs(reader, LinkedHashMap.class);
		}catch(YAMLException ex) {
			throw new IOException("YAML config file has a syntax error: " + f.getAbsolutePath(), ex);
		}
	}

}
