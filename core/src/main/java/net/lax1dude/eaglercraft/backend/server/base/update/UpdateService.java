package net.lax1dude.eaglercraft.backend.server.base.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.lax1dude.eaglercraft.backend.server.adapter.AbortLoadException;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IUpdateCertificate;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings.ConfigDataUpdateService;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class UpdateService {

	private final EaglerXServer<?> server;
	private final ConfigDataUpdateService config;
	private final File eagcertFolder;
	private final boolean loginPacketCerts;
	private final UpdateServiceLoop loop;
	private final UpdateCertificateMultiset certSet;
	private IPlatformTask task;
	private long lastDownload = 0;
	private Map<String, CachedClientCertificate> certsCache = Collections.emptyMap();

	private static class CachedClientCertificate {
		protected final IUpdateCertificateImpl certificate;
		protected final long lastModified;
		protected CachedClientCertificate(IUpdateCertificateImpl certificate, long lastModified) {
			this.certificate = certificate;
			this.lastModified = lastModified;
		}
	}

	public UpdateService(EaglerXServer<?> server) {
		this.server = server;
		config = server.getConfig().getSettings().getUpdateService();
		loginPacketCerts = !config.isDiscardLoginPacketCerts();
		if(config.isEnableEagcertFolder()) {
			eagcertFolder = new File(server.getPlatform().getDataFolder(), "eagcert");
			if(!eagcertFolder.isDirectory() && !eagcertFolder.mkdirs()) {
				throw new AbortLoadException("Could not create folder: " + eagcertFolder.getAbsolutePath());
			}
		}else {
			eagcertFolder = null;
		}
		certSet = new UpdateCertificateMultiset();
		loop = new UpdateServiceLoop(server.getPlatform().getScheduler(), config.getCertPacketDataRateLimit());
	}

	public IUpdateCertificateImpl createUpdateCertificate(EaglerPlayerInstance<?> player, byte[] updateCertData) {
		if(!player.isUpdateSystemSupported()) {
			return null;
		}
		if(updateCertData != null && updateCertData.length > 0) {
			IUpdateCertificateImpl newCert = UpdateCertificate.intern(updateCertData);
			certSet.dump((cert) -> {
				if(cert != newCert) {
					player.offerUpdateCertificate(cert);
				}
			});
			server.forEachEaglerPlayerInternal((player2) -> {
				player2.offerUpdateCertificate(newCert);
			});
			if(loginPacketCerts) {
				certSet.add(newCert);
			}
			return newCert;
		}else {
			certSet.dump(player::offerUpdateCertificate);
			return null;
		}
	}

	public void removeUpdateCertificate(EaglerPlayerInstance<?> player) {
		if(loginPacketCerts) {
			IUpdateCertificateImpl cert = player.getUpdateCertificate();
			if(cert != null) {
				certSet.remove(cert);
			}
		}
	}

	public void start() {
		cancelTask();
		if(eagcertFolder != null) {
			task = server.getPlatform().getScheduler().executeAsyncRepeatingTask(this::update, 0l, 10000l);
		}
		loop.start();
	}

	private void update() {
		long now = Util.steadyTime();
		if(now - lastDownload > config.getCheckForUpdateEvery() * 1000l) {
			lastDownload = now;
			download();
		}
		List<IUpdateCertificateImpl> newCerts = enumerate();
		if(newCerts != null) {
			server.forEachEaglerPlayerInternal((player) -> {
				for(int i = 0, l = newCerts.size(); i < l; ++i) {
					player.offerUpdateCertificate(newCerts.get(i));
				}
			});
		}
	}

	private void download() {
		//TODO
	}

	private synchronized List<IUpdateCertificateImpl> enumerate() {
		File[] dirList = eagcertFolder.listFiles();
		if(dirList == null) {
			server.logger().error("Could not enumerate directory: " + eagcertFolder.getAbsolutePath());
			return null;
		}
		boolean dirty = false;
		Map<String, CachedClientCertificate> certs = new HashMap<>();
		for(int i = 0; i < dirList.length; ++i) {
			File f = dirList[i];
			String n = f.getName();
			long lastModified = f.lastModified();
			CachedClientCertificate cc = certsCache.get(n);
			if(cc != null) {
				if(cc.lastModified != lastModified) {
					dirty = true;
					loadCert(certs, f, lastModified);
				}else {
					certs.put(n, cc);
				}
			}else {
				dirty = true;
				loadCert(certs, f, lastModified);
			}
		}
		if(!dirty) {
			return null;
		}
		List<IUpdateCertificateImpl> broadcastList = null;
		for(Entry<String, CachedClientCertificate> etr : certsCache.entrySet()) {
			CachedClientCertificate oldCert = etr.getValue();
			CachedClientCertificate newCert = certs.get(etr.getKey());
			if(newCert == null) {
				certSet.remove(oldCert.certificate);
			}else {
				if(newCert.certificate != oldCert.certificate) {
					certSet.remove(oldCert.certificate);
					certSet.add(newCert.certificate);
					if(broadcastList == null) {
						broadcastList = new ArrayList<>();
					}
					broadcastList.add(newCert.certificate);
				}
			}
		}
		for(Entry<String, CachedClientCertificate> etr : certs.entrySet()) {
			if(!certsCache.containsKey(etr.getKey())) {
				CachedClientCertificate newCert = etr.getValue();
				certSet.add(newCert.certificate);
				if(broadcastList == null) {
					broadcastList = new ArrayList<>();
				}
				broadcastList.add(newCert.certificate);
			}
		}
		if(!certs.isEmpty()) {
			certsCache = certs;
		}else {
			certsCache = Collections.emptyMap();
		}
		return broadcastList;
	}

	private void loadCert(Map<String, CachedClientCertificate> certs, File file, long lastModified) {
		try {
			String n = file.getName();
			if(file.length() > 32750) {
				throw new IOException("File is too long! Max: 32750 bytes");
			}
			byte[] fileData;
			try(FileInputStream fis = new FileInputStream(file)) {
				fileData = fis.readAllBytes();
			}
			if(fileData.length > 32750) {
				throw new IOException("File is too long! Max: 32750 bytes");
			}
			IUpdateCertificateImpl ch = UpdateCertificate.intern(fileData);
			certs.put(n, new CachedClientCertificate(ch, lastModified));
			server.logger().info("Reloaded certificate: " + file.getAbsolutePath());
		}catch(IOException ex) {
			server.logger().error("Failed to read: " + file.getAbsolutePath());
			server.logger().error("Reason: " + ex);
		}
	}

	public void stop() {
		cancelTask();
		loop.stop();
	}

	private void cancelTask() {
		if(task != null) {
			task.cancel();
			task = null;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<IUpdateCertificate> dumpAllCerts() {
		return (Collection<IUpdateCertificate>) (Object) certSet.dump();
	}

	public UpdateServiceLoop getLoop() {
		return loop;
	}

}
