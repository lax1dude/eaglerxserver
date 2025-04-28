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

package net.lax1dude.eaglercraft.backend.supervisor.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lax1dude.eaglercraft.backend.supervisor.EaglerXSupervisorServer;
import net.lax1dude.eaglercraft.backend.supervisor.server.SupervisorClientInstance;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.PlayerCapeData;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.PlayerSkinData;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.SupervisorPlayerInstance;
import net.lax1dude.eaglercraft.backend.supervisor.status.ClientBrandUUIDHelper;
import net.lax1dude.eaglercraft.backend.supervisor.status.SkinCacheStatus;

public class EaglerXSupervisorConsole implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger("Console");

	private final EaglerXSupervisorServer svr;
	private final List<ConsoleCommand> commandsList;
	private final Map<String, ConsoleCommand> commands;

	public EaglerXSupervisorConsole(EaglerXSupervisorServer svr) {
		this.svr = svr;
		this.commandsList = new ArrayList<>();
		this.commands = new HashMap<>();
		registerCommand(new ConsoleCommand("help", "Displays the list of commands", "?") {
			@Override
			public void handleCommand(Logger logger, String command, String args) {
				logger.info("Commands List:");
				for (ConsoleCommand cmd : commandsList) {
					logger.info("- {}: {}", cmd.getName(), cmd.getDesc());
				}
			}
		});
		registerCommand(new ConsoleCommand("stop", "Initiates a supervisor shutdown", "end", "shutdown") {
			@Override
			public void handleCommand(Logger logger, String command, String args) {
				svr.shutdown();
			}
		});
		registerCommand(new ConsoleCommand("totals", "Lists the maximum and total number of players") {
			@Override
			public void handleCommand(Logger logger, String command, String args) {
				logger.info("Players: {} / {}", svr.getPlayerCount(), svr.getMaxPlayers());
			}
		});
		registerCommand(new ConsoleCommand("proxies", "Lists all proxies connected to the supervisor") {
			@Override
			public void handleCommand(Logger logger, String command, String args) {
				List<SupervisorClientInstance> lst = svr.getClientList();
				TableRenderer tbl = new TableRenderer();
				tbl.pushRow("ID", "Remote Address", "Protocol", "Ping", "Proxy Type", "Plugin Type", "Player Count");
				for (SupervisorClientInstance proxy : lst) {
					tbl.pushRow(proxy.getNodeId(), proxy.getHandler().getChannel().remoteAddress(),
							proxy.getHandler().getConnectionProtocol().name(), proxy.getProxyPing(),
							proxy.getProxyType().name(), proxy.getPluginType().name(),
							proxy.getPlayerCount() + " / " + proxy.getPlayerMax());
				}
				logger.info("{} {} total.", lst.size(), lst.size() == 1 ? "proxy" : "proxies");
				tbl.print((str) -> {
					logger.info("{}", str);
				});
			}
		});
		registerCommand(new ConsoleCommand("versions", "Lists version of the supervisor and proxies") {
			@Override
			public void handleCommand(Logger logger, String command, String args) {
				List<SupervisorClientInstance> lst = svr.getClientList();
				TableRenderer tbl = new TableRenderer();
				tbl.pushRow("ID", "Protocol", "Proxy Type", "Proxy Version", "Plugin Type", "Plugin Brand",
						"Plugin Version");
				for (SupervisorClientInstance proxy : lst) {
					tbl.pushRow(proxy.getNodeId(), proxy.getHandler().getConnectionProtocol().name(),
							proxy.getProxyType().name(), proxy.getProxyVersion(), proxy.getPluginType().name(),
							proxy.getPluginBrand(), proxy.getPluginVersion());
				}
				logger.info("Supervisor Brand: {}", svr.getServerBrand());
				logger.info("Supervisor Version: {}", svr.getServerVersion());
				tbl.print((str) -> {
					logger.info("{}", str);
				});
			}
		});
		registerCommand(new ConsoleCommand("players", "Lists all players being tracked by the supervisor") {
			@Override
			public void handleCommand(Logger logger, String command, String args) {
				List<SupervisorPlayerInstance> lst = svr.getPlayerList();
				TableRenderer tbl = new TableRenderer();
				tbl.pushRow("Proxy", "Player Name", "Player UUID", "Protocol", "Brand UUID", "Skin", "Cape");
				for (SupervisorPlayerInstance player : lst) {
					List<Object> row = new ArrayList<>();
					row.add(player.getOwner().getNodeId());
					row.add(player.getUsername());
					row.add(player.getPlayerUUID());
					int eagProto = player.getEaglerProtocol();
					if (eagProto == 0) {
						row.add("MC: " + player.getGameProtocol());
					} else {
						row.add("MC: " + player.getGameProtocol() + ", EAG: " + eagProto);
					}
					row.add(ClientBrandUUIDHelper.toString(player.getBrandUUID()));
					PlayerSkinData sdata = player.getSkinDataIfLoaded();
					if (sdata == null) {
						row.add("Pending");
					} else if (sdata == PlayerSkinData.ERROR) {
						row.add("Error");
					} else if (sdata instanceof PlayerSkinData.Preset) {
						row.add("Preset (" + ((PlayerSkinData.Preset) sdata).presetId + ")");
					} else {
						row.add("Custom");
					}
					PlayerCapeData cdata = player.getCapeDataIfLoaded();
					if (cdata == null) {
						row.add("Pending");
					} else if (cdata == PlayerCapeData.ERROR) {
						row.add("Error");
					} else if (cdata instanceof PlayerCapeData.Preset) {
						int id = ((PlayerCapeData.Preset) cdata).presetId;
						if (id != 0) {
							row.add("Preset (" + id + ")");
						} else {
							row.add("None");
						}
					} else {
						row.add("Custom");
					}
					tbl.pushRow(row);
				}
				logger.info("{} {} total.", lst.size(), lst.size() == 1 ? "player" : "players");
				tbl.print((str) -> {
					logger.info("{}", str);
				});
			}
		});
		registerCommand(new ConsoleCommand("skins", "Lists statistics about the skin cache") {
			@Override
			public void handleCommand(Logger logger, String command, String args) {
				SkinCacheStatus[] skinCacheStatus = svr.getSkinCacheStatus();
				TableRenderer tbl = new TableRenderer();
				tbl.pushRow("Statistic", "Skins", "Capes");
				tbl.pushRow("Eagler Players - Preset", skinCacheStatus[0].eaglerPlayerPreset, skinCacheStatus[1].eaglerPlayerPreset);
				tbl.pushRow("Eagler Players - Custom", skinCacheStatus[0].eaglerPlayerCustom, skinCacheStatus[1].eaglerPlayerCustom);
				tbl.pushRow("Downloaded - In Memory",
						skinCacheStatus[0].downloadEnabled ? Integer.toString(skinCacheStatus[0].downloadedInMemory) : "N/A",
						skinCacheStatus[1].downloadEnabled ? Integer.toString(skinCacheStatus[1].downloadedInMemory) : "N/A");
				tbl.pushRow("Downloaded - Database",
						skinCacheStatus[0].downloadEnabled ? Integer.toString(skinCacheStatus[0].downloadedInDatabase) : "N/A",
						skinCacheStatus[1].downloadEnabled ? Integer.toString(skinCacheStatus[1].downloadedInDatabase) : "N/A");
				logger.info("Skin Cache: {}", svr.getSkinCache() == null ? "Eagler Players Only" : "Enabled (Allow Downloads)");
				tbl.print((str) -> {
					logger.info("{}", str);
				});
			}
		});
	}

	public void registerCommand(ConsoleCommand cmd) {
		commandsList.add(cmd);
		for (String s : cmd.getAliases()) {
			commands.put(s, cmd);
		}
	}

	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		while (svr.isRunning()) {
			try {
				String str;
				while ((str = reader.readLine()) != null) {
					str = str.trim();
					if (str.length() > 0) {
						try {
							handleCommand(str);
						} catch (Throwable t) {
							logger.error("Failed to execute console command!", t);
						}
					}
				}
			} catch (IOException e) {
				logger.error("Caught IOException reading console input", e);
			}
		}
	}

	public void handleCommand(String str) {
		String[] arr = str.split("\\s+", 2);
		if (arr.length > 0) {
			String cmd = arr[0].toLowerCase();
			ConsoleCommand ccmd = commands.get(cmd);
			if (ccmd != null) {
				ccmd.handleCommand(logger, cmd, arr.length > 1 ? arr[1] : "");
			} else {
				logger.error("Unknown command: '{}'", cmd);
			}
		}
	}

}