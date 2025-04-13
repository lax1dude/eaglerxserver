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

package net.lax1dude.eaglercraft.backend.supervisor;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.IntContainer;
import com.carrotsearch.hppc.IntHashSet;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntSet;
import com.carrotsearch.hppc.cursors.IntCursor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import net.lax1dude.eaglercraft.backend.skin_cache.HTTPClient;
import net.lax1dude.eaglercraft.backend.skin_cache.ISkinCacheDatastore;
import net.lax1dude.eaglercraft.backend.skin_cache.ISkinCacheDownloader;
import net.lax1dude.eaglercraft.backend.skin_cache.ISkinCacheService;
import net.lax1dude.eaglercraft.backend.skin_cache.SkinCacheDatastore;
import net.lax1dude.eaglercraft.backend.skin_cache.SkinCacheDownloader;
import net.lax1dude.eaglercraft.backend.skin_cache.SkinCacheService;
import net.lax1dude.eaglercraft.backend.supervisor.config.EaglerXSupervisorConfig;
import net.lax1dude.eaglercraft.backend.supervisor.console.EaglerXSupervisorConsole;
import net.lax1dude.eaglercraft.backend.supervisor.netty.PipelineFactory;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvDropAllPlayers;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvDropPlayer;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvTotalPlayerCount;
import net.lax1dude.eaglercraft.backend.supervisor.server.SupervisorClientInstance;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.PlayerCapeData;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.PlayerSkinData;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.SupervisorPlayerInstance;
import net.lax1dude.eaglercraft.backend.supervisor.status.SkinCacheStatus;
import net.lax1dude.eaglercraft.backend.supervisor.status.StatusRendererHTML;
import net.lax1dude.eaglercraft.backend.supervisor.util.AlreadyRegisteredException;
import net.lax1dude.eaglercraft.backend.supervisor.util.LoggerSv;
import net.lax1dude.eaglercraft.backend.util.EaglerDrivers;

public class EaglerXSupervisorServer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger("EaglerXSupervisorServer");

	private static final String serverBrand = "EaglercraftXSupervisor";
	private static final String serverVersion = "1.0.0";

	private static final File configFile = new File("supervisor_config.properties");
	private final EaglerXSupervisorConfig config = new EaglerXSupervisorConfig();

	private EventLoopGroup eventLoopGroup;

	private final Collection<Channel> listeningChannels = new LinkedList<>();

	private final ReadWriteLock activeClientsLock = new ReentrantReadWriteLock();
	private final List<SupervisorClientInstance> activeClients = new ArrayList<>();
	private final IntObjectMap<SupervisorClientInstance> activeClientsMap = new IntObjectHashMap<>();
	private int nextNodeId = 0;

	private final ReadWriteLock activePlayersLock = new ReentrantReadWriteLock();
	private final Map<UUID, SupervisorPlayerInstance> activePlayersMap = new HashMap<>(2048);
	private final Map<String, SupervisorPlayerInstance> activePlayersNameMap = new HashMap<>(2048);
	private volatile int maxPlayers = 0;

	private boolean running = true;

	private Thread consoleThread;
	private EaglerXSupervisorConsole console = null;

	private HTTPClient skinHTTPClient = null;
	private ISkinCacheService skinCache = null;
	private Connection skinJDBCConnection = null;
	private ISkinCacheDatastore datastore = null;

	private StatusRendererHTML statusRendererHTML = null;

	@Override
	public synchronized void run() {
		logger.info("Starting {}-{}...", serverBrand, serverVersion);

		logger.info("Loading configuration...");
		try {
			config.load(configFile);
		} catch (IOException e) {
			logger.error("Failed to load configuration!", e);
			return;
		}

		eventLoopGroup = PipelineFactory.createEventLoopGroup();

		if(config.isDownloadVanillaSkins()) {
			logger.info("Starting skin cache...");
			
			skinHTTPClient = new HTTPClient(eventLoopGroup, PipelineFactory.getClientChannel(null), getUserAgent());
			
			ISkinCacheDownloader downloader = new SkinCacheDownloader(skinHTTPClient, config.getAllowedSkinDownloadOrigins());
			
			int threads = config.getSkinCacheThreadPoolSize();
			if(threads <= 0) {
				threads = Runtime.getRuntime().availableProcessors();
			}
			
			try {
				skinJDBCConnection = EaglerDrivers.connectToDatabase(config.getSkinCacheDBURI(),
						config.getSQLDriverClass(), config.getSQLDriverPath(), new Properties(),
						LoggerSv.getLogger("EaglerDrivers"));
				datastore = new SkinCacheDatastore(skinJDBCConnection, threads,
						config.getDatabaseKeepObjectsDays(), config.getDatabaseMaxObjects(),
						config.getDatabaseCompressionLevel(), config.getSkinCacheDBSQLiteCompatible(),
						LoggerSv.getLogger("SkinCacheDatastore"));
				logger.info("Connected to database: '{}'", config.getSkinCacheDBURI());
			}catch(SQLException ex) {
				logger.info("Failed to connect to database!", ex);
				if(skinJDBCConnection != null) {
					try {
						skinJDBCConnection.close();
					}catch(SQLException exx) {
					}
				}
				return;
			}
			
			skinCache = new SkinCacheService(downloader, datastore, config.getMemoryCacheKeepObjectsSeconds(),
					Math.min(1024, config.getMemoryCacheMaxObjects()), config.getMemoryCacheMaxObjects(),
					LoggerSv.getLogger("SkinCacheService"));
		}

		logger.info("Starting listeners...");
	
		eventLoopGroup = PipelineFactory.createEventLoopGroup();

		CountDownLatch cnt = new CountDownLatch(2);
		AtomicBoolean issues = new AtomicBoolean(false);
	
		PipelineFactory.bindListener(eventLoopGroup, config.getListenAddress(),
				PipelineFactory.getServerChildInitializer(this, config.getReadTimeout())).addListener((future) -> {
					if(future.isSuccess()) {
						synchronized(listeningChannels) {
							logger.info("Supervisor server is listening on: {}", config.getListenAddress());
							listeningChannels.add(((ChannelFuture)future).channel());
						}
					}else {
						synchronized(listeningChannels) {
							logger.error("Could not bind port: {}", config.getListenAddress());
							logger.error("Reason: {}", future.cause().toString());
						}
						issues.set(true);
					}
					cnt.countDown();
				});
		
		if(config.isEnableStatus()) {
			statusRendererHTML = new StatusRendererHTML(this);
			PipelineFactory.bindListener(eventLoopGroup, config.getListenStatusAddress(),
					PipelineFactory.getStatusChildInitializer(this, config.getReadTimeout())).addListener((future) -> {
						if(future.isSuccess()) {
							synchronized(listeningChannels) {
								logger.info("Status HTTP server is listening on: {}", config.getListenStatusAddress());
								listeningChannels.add(((ChannelFuture)future).channel());
							}
						}else {
							synchronized(listeningChannels) {
								logger.error("Could not bind port: {}", config.getListenStatusAddress());
								logger.error("Reason: {}", future.cause().toString());
							}
							issues.set(true);
						}
						cnt.countDown();
					});
		}else {
			cnt.countDown();
		}

		try {
			cnt.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			stopListeners();
			running = false;
			return;
		}

		if(issues.get()) {
			logger.error("One or more listeners failed to bind ports!");
			stopListeners();
			running = false;
			return;
		}
	
		console = new EaglerXSupervisorConsole(this);
		consoleThread = new Thread(console, "Console Thread");
		consoleThread.setDaemon(true);
		consoleThread.start();

		logger.info("Server started successfully!");
		logger.info("Type \"help\" for a list of commands");

		while(running) {
			try {
				runServerTick();
				Thread.sleep(1000l);
			}catch(Throwable t) {
				logger.error("Caught exception from tick loop", t);
			}
		}

		logger.info("Closing listeners...");
		stopListeners();

		if(skinCache != null) {
			logger.info("Stopping skin cache...");
			datastore.dispose();
			eag: {
				try {
					skinJDBCConnection.close();
				}catch(SQLException ex) {
					logger.error("Failed to disconnect from database '{}'", ex);
					break eag;
				}
				logger.info("Successfully disconnected from database '{}'", config.getSkinCacheDBURI());
			}
		}

		logger.info("Stopping IO event loop...");
		try {
			eventLoopGroup.shutdownGracefully().await();
		} catch (InterruptedException e) {
		}

		logger.info("Server Stopped!");
	}

	private void runServerTick() {
		List<SupervisorClientInstance> clients = getClientList();
		for(SupervisorClientInstance cl : clients) {
			cl.update();
		}
		if(skinCache != null) {
			skinCache.tick();
		}
	}

	public void stopListeners() {
		List<Channel> ch;
		synchronized(listeningChannels) {
			ch = new ArrayList<>(listeningChannels);
			listeningChannels.clear();
		}
		for(Channel c : ch) {
			c.close().syncUninterruptibly();
			logger.info("Listener closed: {}", c.attr(PipelineFactory.LOCAL_ADDRESS).get().toString());
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void shutdown() {
		running = false;
	}

	public SupervisorClientInstance registerClient(SupervisorPacketHandler handler) {
		activeClientsLock.writeLock().lock();
		try {
			int i;
			do {
				i = ++nextNodeId;
			}while(activeClientsMap.containsKey(i));
			
			SupervisorClientInstance client = new SupervisorClientInstance(i, this, handler);
			
			activeClients.add(client);
			activeClientsMap.put(i, client);
			
			return client;
		}finally {
			activeClientsLock.writeLock().unlock();
		}
	}

	public SupervisorPlayerInstance registerPlayer(SupervisorClientInstance owner, UUID playerUUID, UUID brandUUID,
			int gameProtocol, int eaglerProtocol, String username) throws AlreadyRegisteredException {
		SupervisorPlayerInstance player;
		int newTotal;
		activePlayersLock.writeLock().lock();
		try {
			if(activePlayersMap.containsKey(playerUUID)) {
				throw AlreadyRegisteredException.uuid();
			}
			if(activePlayersNameMap.containsKey(username)) {
				throw AlreadyRegisteredException.username();
			}
			player = new SupervisorPlayerInstance(owner, playerUUID, brandUUID, gameProtocol, eaglerProtocol, username);
			activePlayersMap.put(playerUUID, player);
			activePlayersNameMap.put(username, player);
			newTotal = activePlayersMap.size();
		}finally {
			activePlayersLock.writeLock().unlock();
		}
		resendPlayerCounts(newTotal, maxPlayers);
		return player;
	}

	public SupervisorPlayerInstance getPlayerByUUID(UUID playerUUID) {
		activePlayersLock.readLock().lock();
		try {
			return activePlayersMap.get(playerUUID);
		}finally {
			activePlayersLock.readLock().unlock();
		}
	}

	public SupervisorPlayerInstance getPlayerByUsername(String playerName) {
		activePlayersLock.readLock().lock();
		try {
			return activePlayersNameMap.get(playerName);
		}finally {
			activePlayersLock.readLock().unlock();
		}
	}

	public void unregisterClient(SupervisorClientInstance client) {
		activeClientsLock.writeLock().lock();
		int nodeId;
		try {
			if(!activeClients.remove(client)) {
				return;
			}
			nodeId = client.getNodeId();
			activeClientsMap.remove(nodeId);
		}finally {
			activeClientsLock.writeLock().unlock();
		}
		recalcMaxPlayers();
		IntSet clientSet = null;
		activePlayersLock.writeLock().lock();
		try {
			Iterator<SupervisorPlayerInstance> itr = activePlayersMap.values().iterator();
			while(itr.hasNext()) {
				SupervisorPlayerInstance player = itr.next();
				if(player.getOwner().getNodeId() == nodeId) {
					itr.remove();
					activePlayersNameMap.remove(player.getUsername());
					if(clientSet == null) {
						clientSet = new IntHashSet();
					}
					player.addKnownClientsNotOwner(clientSet);
				}
				player.forgetClient(nodeId);
			}
		}finally {
			activePlayersLock.writeLock().unlock();
		}
		if(clientSet != null) {
			List<SupervisorClientInstance> toNotify = new ArrayList<>(clientSet.size());
			activeClientsLock.readLock().lock();
			try {
				for(IntCursor cur : clientSet) {
					SupervisorClientInstance client2 = activeClientsMap.get(cur.value);
					if(client2 != null) {
						toNotify.add(client2);
					}
				}
			}finally {
				activeClientsLock.readLock().unlock();
			}
			SPacketSvDropAllPlayers dropPacket = new SPacketSvDropAllPlayers(nodeId);
			for(int i = 0, l = toNotify.size(); i < l; ++i) {
				toNotify.get(i).sendPacket(dropPacket);
			}
		}
	}

	public void unregisterPlayer(UUID playerUUID) {
		int total;
		activePlayersLock.writeLock().lock();
		SupervisorPlayerInstance player;
		try {
			player = activePlayersMap.remove(playerUUID);
			if(player != null) {
				activePlayersNameMap.remove(player.getUsername());
			}
			total = activePlayersMap.size();
		}finally {
			activePlayersLock.writeLock().unlock();
		}
		if(player != null) {
			IntContainer lst = player.allKnownClients();
			List<SupervisorClientInstance> toNotify = null;
			activeClientsLock.readLock().lock();
			try {
				for(IntCursor cur : lst) {
					SupervisorClientInstance client = activeClientsMap.get(cur.value);
					if(client != null) {
						if(toNotify == null) {
							toNotify = new ArrayList<>(activeClientsMap.size());
						}
						toNotify.add(client);
					}
				}
			}finally {
				activeClientsLock.readLock().unlock();
			}
			if(toNotify != null) {
				SPacketSvDropPlayer dropPacket = new SPacketSvDropPlayer(player.getPlayerUUID());
				for(int i = 0, l = toNotify.size(); i < l; ++i) {
					toNotify.get(i).sendPacket(dropPacket);
				}
			}
			resendPlayerCounts(total, maxPlayers);
		}
	}

	public void recalcMaxPlayers() {
		int max = 0;
		activeClientsLock.readLock().lock();
		try {
			for(SupervisorClientInstance client : activeClients) {
				max += client.getPlayerMax();
			}
		}finally {
			activeClientsLock.readLock().unlock();
		}
		if(maxPlayers != max) {
			maxPlayers = max;
			resendPlayerCounts(getPlayerCount(), max);
		}
	}

	public int getPlayerCount() {
		activePlayersLock.readLock().lock();
		try {
			return activePlayersMap.size();
		}finally {
			activePlayersLock.readLock().unlock();
		}
	}

	private void resendPlayerCounts(int count, int max) {
		SPacketSvTotalPlayerCount pkt = new SPacketSvTotalPlayerCount(count, max);
		List<SupervisorClientInstance> toNotify;
		activeClientsLock.readLock().lock();
		try {
			toNotify = new ArrayList<>(activeClients);
		}finally {
			activeClientsLock.readLock().unlock();
		}
		for(SupervisorClientInstance client : toNotify) {
			client.getHandler().channelWrite(pkt);
		}
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public Logger getLogger() {
		return logger;
	}

	public EaglerXSupervisorConsole getConsole() {
		return console;
	}

	public String getServerBrand() {
		return serverBrand;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public String getServerString() {
		return serverBrand + "/" + serverVersion;
	}

	public String getUserAgent() {
		return "Mozilla/5.0 " + serverBrand + "/" + serverVersion;
	}

	public EaglerXSupervisorConfig getConfig() {
		return config;
	}

	public ISkinCacheService getSkinCache() {
		return skinCache;
	}

	public StatusRendererHTML getStatusRendererHTML() {
		return statusRendererHTML;
	}

	public SupervisorClientInstance getClient(int nodeId) {
		activeClientsLock.readLock().lock();
		try {
			return activeClientsMap.get(nodeId);
		}finally {
			activeClientsLock.readLock().unlock();
		}
	}

	public List<SupervisorClientInstance> getClients(IntContainer lst) {
		List<SupervisorClientInstance> ret = new ArrayList<>(lst.size());
		activeClientsLock.readLock().lock();
		try {
			for(IntCursor cur : lst) {
				ret.add(activeClientsMap.get(cur.value));
			}
		}finally {
			activeClientsLock.readLock().unlock();
		}
		return ret;
	}

	public List<SupervisorClientInstance> getClientList() {
		activeClientsLock.readLock().lock();
		try {
			return new ArrayList<>(activeClients);
		}finally {
			activeClientsLock.readLock().unlock();
		}
	}

	public List<SupervisorPlayerInstance> getPlayerList() {
		activePlayersLock.readLock().lock();
		try {
			return new ArrayList<>(activePlayersMap.values());
		}finally {
			activePlayersLock.readLock().unlock();
		}
	}

	public void shutdownHook() {
		shutdown();
		synchronized(this) {
			logger.info("Aquired lock");
		}
	}

	public SkinCacheStatus[] getSkinCacheStatus() {
		int totalPresetSkins = 0;
		int totalPresetCapes = 0;
		int totalCustomSkins = 0;
		int totalCustomCapes = 0;
		PlayerSkinData err = PlayerSkinData.ERROR;
		PlayerCapeData err2 = PlayerCapeData.ERROR;
		activePlayersLock.readLock().lock();
		try {
			for(SupervisorPlayerInstance player : activePlayersMap.values()) {
				PlayerSkinData dat = player.getSkinDataIfLoaded();
				if(dat != null && dat != err) {
					if(dat instanceof PlayerSkinData.Custom) {
						++totalCustomSkins;
					}else {
						++totalPresetSkins;
					}
				}
				PlayerCapeData dat2 = player.getCapeDataIfLoaded();
				if(dat2 != null && dat2 != err2) {
					if(dat2 instanceof PlayerCapeData.Custom) {
						++totalCustomCapes;
					}else {
						++totalPresetCapes;
					}
				}
			}
		}finally {
			activePlayersLock.readLock().unlock();
		}
		if(skinCache != null) {
			int downloadedMemorySkins = skinCache.getTotalMemorySkins();
			int downloadedMemoryCapes = skinCache.getTotalMemoryCapes();
			int downloadedDatabaseSkins = datastore.getTotalStoredSkins();
			int downloadedDatabaseCapes = datastore.getTotalStoredCapes();
			return new SkinCacheStatus[] {
				new SkinCacheStatus(totalPresetSkins, totalCustomSkins, true, downloadedMemorySkins, downloadedDatabaseSkins),
				new SkinCacheStatus(totalPresetCapes, totalCustomCapes, true, downloadedMemoryCapes, downloadedDatabaseCapes)
			};
		}else {
			return new SkinCacheStatus[] {
				new SkinCacheStatus(totalPresetSkins, totalCustomSkins, false, 0, 0),
				new SkinCacheStatus(totalPresetCapes, totalCustomCapes, false, 0, 0)
			};
		}
	}

	public static void main(String[] args) {
		EaglerXSupervisorServer server = new EaglerXSupervisorServer();
		Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownHook, "Supervisor Shutdown Hook"));
		server.run();
		System.exit(0);
	}

}