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

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.JsonParseException;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.ITask;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;

public class EaglerMOTD<PlayerObject> {

	private final IEaglerMOTDPlatform<PlayerObject> platform;
	private IEaglerXServerAPI<PlayerObject> server;
	private EaglerMOTDConfiguration config;
	private ITask motdUpdateTask;
	private Collection<EaglerMOTDConnectionUpdater> activeConnections;

	public EaglerMOTD(IEaglerMOTDPlatform<PlayerObject> platform) {
		this.platform = platform;
		this.activeConnections = new HashSet<>();
	}

	public IEaglerMOTDPlatform<PlayerObject> getPlatform() {
		return platform;
	}

	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	public void onEnable(IEaglerXServerAPI<PlayerObject> server) {
		this.server = server;
		try {
			this.config = EaglerMOTDConfiguration.load(platform.getDataFolder(), server, logger(),
					server.getAllEaglerListeners().stream().map(IEaglerListenerInfo::getName).collect(Collectors.toSet()));
		} catch (JsonParseException | IOException e) {
			if(e instanceof RuntimeException ee) throw ee;
			throw new RuntimeException("Could not load EaglerMOTD config files!", e);
		}
		this.motdUpdateTask = server.getScheduler().executeAsyncRepeatingTask(this::updateMOTDs, 50l, 50l);
		platform.setOnMOTD(this::onMOTD);
		for(Entry<String, QueryType> etr : config.queryTypes.entrySet()) {
			server.getQueryServer().registerQueryType(this, etr.getKey(), etr.getValue()::doQuery);
		}
	}

	public void onDisable(IEaglerXServerAPI<PlayerObject> server) {
		if(motdUpdateTask != null) {
			motdUpdateTask.cancel();
			motdUpdateTask = null;
		}
		platform.setOnMOTD(null);
		for(String etr : config.queryTypes.keySet()) {
			server.getQueryServer().unregisterQueryType(this, etr);
		}
	}

	public void onMOTD(IEaglercraftMOTDEvent<PlayerObject> event) {
		EaglerMOTDConnectionUpdater updater = new EaglerMOTDConnectionUpdater(config, event.getMOTDConnection());
		if(updater.execute()) {
			synchronized(activeConnections) {
				if(config.max_total_sockets > 0) {
					while(activeConnections.size() >= config.max_total_sockets) {
						Iterator<EaglerMOTDConnectionUpdater> itr = activeConnections.iterator();
						if(itr.hasNext()) {
							EaglerMOTDConnectionUpdater c = itr.next();
							itr.remove();
							c.close();
						}
					}
				}
				activeConnections.add(updater);
			}
		}
	}

	public void updateMOTDs() {
		EaglerMOTDConnectionUpdater[] conns;
		synchronized(activeConnections) {
			conns = activeConnections.toArray(new EaglerMOTDConnectionUpdater[activeConnections.size()]);
		}
		for(int i = 0; i < conns.length; ++i) {
			EaglerMOTDConnectionUpdater up = conns[i];
			if(!up.tick()) {
				synchronized(activeConnections) {
					activeConnections.remove(up);
				}
			}
		}
	}

	public IEaglerMOTDLogger logger() {
		return platform.logger();
	}

}
