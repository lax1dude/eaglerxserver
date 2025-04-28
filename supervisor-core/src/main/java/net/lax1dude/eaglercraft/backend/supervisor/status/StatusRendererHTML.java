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

package net.lax1dude.eaglercraft.backend.supervisor.status;

import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.CharStreams;

import net.lax1dude.eaglercraft.backend.supervisor.EaglerXSupervisorServer;
import net.lax1dude.eaglercraft.backend.supervisor.server.SupervisorClientInstance;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.PlayerCapeData;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.PlayerSkinData;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.SupervisorPlayerInstance;

public class StatusRendererHTML {

	private final EaglerXSupervisorServer server;
	private final LoadingCache<String, String> templateFileCache;

	public StatusRendererHTML(EaglerXSupervisorServer server) {
		this.server = server;
		this.templateFileCache = CacheBuilder.newBuilder().build(new CacheLoader<>() {
			@Override
			public String load(String key) throws Exception {
				try (InputStreamReader reader = new InputStreamReader(
						StatusRendererHTML.class.getResourceAsStream(key))) {
					return CharStreams.toString(reader);
				}
			}
		});
	}

	private String cacheLoad(String name) {
		try {
			return templateFileCache.get(name);
		} catch (ExecutionException e) {
			throw new RuntimeException("Could not load required resource!", e);
		}
	}

	private String render(String title, String body, int refreshRate) {
		String base = cacheLoad("base.html");
		if (refreshRate > 0) {
			base = base.replace("${auto_refresh}", "<meta http-equiv=\"refresh\" content=\"" + refreshRate + "\">");
		} else {
			base = base.replace("${auto_refresh}", "");
		}
		base = base.replace("${title}", title + " - " + server.getServerBrand());
		base = base.replace("${server_string}", server.getServerString());
		base = base.replace("${body}", body);
		return base;
	}

	public String renderIndex() {
		String body = cacheLoad("overview.html");
		Runtime rt = Runtime.getRuntime();
		body = body.replace("${memory_used}", Long.toString((rt.totalMemory() - rt.freeMemory()) / (1024l * 1024l)));
		body = body.replace("${memory_max}", Long.toString(rt.maxMemory() / (1024l * 1024l)));
		body = body.replace("${skin_cache_status}",
				server.getSkinCache() == null ? "Eagler Players Only" : "Enabled (Allow Downloads)");
		body = body.replace("${total_players}", Integer.toString(server.getPlayerCount()));
		body = body.replace("${max_players}", Integer.toString(server.getMaxPlayers()));
		SkinCacheStatus[] skinCacheStatus = server.getSkinCacheStatus();
		body = body.replace("${eagler_players_preset_skins}", Integer.toString(skinCacheStatus[0].eaglerPlayerPreset));
		body = body.replace("${eagler_players_preset_capes}", Integer.toString(skinCacheStatus[1].eaglerPlayerPreset));
		body = body.replace("${eagler_players_custom_skins}", Integer.toString(skinCacheStatus[0].eaglerPlayerCustom));
		body = body.replace("${eagler_players_custom_capes}", Integer.toString(skinCacheStatus[1].eaglerPlayerCustom));
		if (skinCacheStatus[0].downloadEnabled) {
			body = body.replace("${downloaded_memory_skins}", Integer.toString(skinCacheStatus[0].downloadedInMemory));
			body = body.replace("${downloaded_database_skins}",
					Integer.toString(skinCacheStatus[0].downloadedInDatabase));
		} else {
			body = body.replace("${downloaded_memory_skins}", "N/A");
			body = body.replace("${downloaded_database_skins}", "N/A");
		}
		if (skinCacheStatus[1].downloadEnabled) {
			body = body.replace("${downloaded_memory_capes}", Integer.toString(skinCacheStatus[1].downloadedInMemory));
			body = body.replace("${downloaded_database_capes}",
					Integer.toString(skinCacheStatus[1].downloadedInDatabase));
		} else {
			body = body.replace("${downloaded_memory_capes}", "N/A");
			body = body.replace("${downloaded_database_capes}", "N/A");
		}
		List<SupervisorClientInstance> lst = server.getClientList();
		body = body.replace("${proxies_total}", Integer.toString(lst.size()));
		body = body.replace("${proxies_table}", renderProxyTable(lst));
		return render("Overview", body, 10);
	}

	private String renderProxyTable(List<SupervisorClientInstance> lst) {
		StringBuilder ret = new StringBuilder();
		boolean b = true;
		for (SupervisorClientInstance proxy : lst) {
			ret.append("<tr");
			if (b = !b) {
				ret.append(" class=\"tr_alt\"");
			}
			ret.append("><td>");
			ret.append(proxy.getNodeId());
			ret.append("</td><td>");
			ret.append(proxy.getHandler().getChannel().remoteAddress());
			ret.append("</td><td>");
			ret.append(proxy.getHandler().getConnectionProtocol().name());
			ret.append("</td><td>");
			ret.append(proxy.getProxyPing());
			ret.append("ms</td><td>");
			ret.append(proxy.getProxyType().name());
			ret.append("</td><td>");
			ret.append(htmlEntities(proxy.getProxyVersion()));
			ret.append("</td><td>");
			ret.append(proxy.getPluginType().name());
			ret.append("</td><td>");
			ret.append(htmlEntities(proxy.getPluginBrand()));
			ret.append("</td><td>");
			ret.append(htmlEntities(proxy.getPluginVersion()));
			ret.append("</td><td>");
			ret.append(HttpStatusRequestHandler.gmt.format(new Date(proxy.getProxySystemTime())));
			ret.append("</td><td>");
			ret.append(proxy.getPlayerCount());
			ret.append("</td><td>");
			ret.append(proxy.getPlayerMax());
			ret.append("</td></tr>");
		}
		return ret.toString();
	}

	public String renderProxies() {
		String body = cacheLoad("proxies.html");
		List<SupervisorClientInstance> lst = server.getClientList();
		body = body.replace("${proxies_total}", Integer.toString(lst.size()));
		body = body.replace("${proxies_table}", renderProxyTable(lst));
		return render("Proxies", body, 10);
	}

	public String renderPlayers() {
		String body = cacheLoad("players.html");
		List<SupervisorPlayerInstance> lst = server.getPlayerList();
		body = body.replace("${players_total}", Integer.toString(lst.size()));
		StringBuilder ret = new StringBuilder();
		boolean b = true;
		for (SupervisorPlayerInstance player : lst) {
			ret.append("<tr");
			if (b = !b) {
				ret.append(" class=\"tr_alt\"");
			}
			ret.append("><td>");
			ret.append(player.getOwner().getNodeId());
			ret.append("</td><td>");
			ret.append(htmlEntities(player.getUsername()));
			ret.append("</td><td>");
			ret.append(player.getPlayerUUID());
			ret.append("</td><td>");
			int eagProto = player.getEaglerProtocol();
			if (eagProto == 0) {
				ret.append("MC: ");
				ret.append(player.getGameProtocol());
			} else {
				ret.append("MC: ");
				ret.append(player.getGameProtocol());
				ret.append(", EAG: ");
				ret.append(eagProto);
			}
			ret.append("</td><td>");
			ret.append(ClientBrandUUIDHelper.toString(player.getBrandUUID()));
			ret.append("</td><td>");
			PlayerSkinData sdata = player.getSkinDataIfLoaded();
			if (sdata == null) {
				ret.append("Pending");
			} else if (sdata == PlayerSkinData.ERROR) {
				ret.append("Error");
			} else if (sdata instanceof PlayerSkinData.Preset) {
				ret.append("Preset (");
				ret.append(((PlayerSkinData.Preset) sdata).presetId);
				ret.append(")");
			} else {
				ret.append("Custom");
			}
			ret.append("</td><td>");
			PlayerCapeData cdata = player.getCapeDataIfLoaded();
			if (cdata == null) {
				ret.append("Pending");
			} else if (cdata == PlayerCapeData.ERROR) {
				ret.append("Error");
			} else if (cdata instanceof PlayerCapeData.Preset) {
				int id = ((PlayerCapeData.Preset) cdata).presetId;
				if (id != 0) {
					ret.append("Preset (");
					ret.append(id);
					ret.append(")");
				} else {
					ret.append("None");
				}
			} else {
				ret.append("Custom");
			}
			ret.append("</td></tr>");
		}
		body = body.replace("${players_table}", ret.toString());
		return render("Players", body, 0);
	}

	public String render404() {
		return render("404 Not Found", cacheLoad("404.html"), 0);
	}

	private String htmlEntities(String username) {
		return username.replace("<", "&lt;").replace(">", "&gt;");
	}

}