package net.lax1dude.eaglercraft.backend.server.base.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.io.ByteStreams;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.util.HashPair;

public class SSLCertificateManager {

	private final BiMap<HashPair<File, File>, SSLContextHolder> pairs = HashBiMap.create();
	private final Multiset<File> filesRefreshable = HashMultiset.create();
	private final Map<File, RefreshWatcher> refreshableFiles = new HashMap<>();
	private final IPlatformLogger logger;

	private static class RefreshWatcher {

		protected final File file;
		protected byte[] data;
		protected long lastModified;
		protected final Set<SSLContextHolder> childrenA = new HashSet<>();
		protected final Set<SSLContextHolder> childrenB = new HashSet<>();

		protected RefreshWatcher(File file) throws SSLException {
			this.file = file;
			try {
				this.data = readFileBytes(file);
			}catch(IOException ex) {
				throw new SSLException("Could not load certificate file: " + file.getAbsolutePath(), ex);
			}
			this.lastModified = file.lastModified();
		}

	}

	public SSLCertificateManager(IPlatformLogger logger) {
		this.logger = logger;
	}

	public synchronized SSLContextHolder createHolder(File publicChain, File privateKey, boolean autoRefresh) throws SSLException {
		publicChain = publicChain.getAbsoluteFile();
		privateKey = privateKey.getAbsoluteFile();
		if(publicChain.equals(privateKey)) {
			throw new IllegalArgumentException("Public and private key are the same file");
		}
		HashPair<File, File> pair = new HashPair<>(publicChain, privateKey);
		SSLContextHolder ret = pairs.get(pair);
		if(ret != null) {
			return ret;
		}
		ret = new SSLContextHolder(createName(publicChain, privateKey));
		RefreshWatcher pub = getOrCreateRefresher(publicChain);
		RefreshWatcher priv = getOrCreateRefresher(privateKey);
		ret.pubKey = pub.data;
		ret.privKey = priv.data;
		ret.refresh();
		if(autoRefresh) {
			pairs.put(pair, ret);
			filesRefreshable.add(publicChain);
			refreshableFiles.put(publicChain, pub);
			filesRefreshable.add(privateKey);
			refreshableFiles.put(privateKey, priv);
			pub.childrenA.add(ret);
			priv.childrenB.add(ret);
		}
		return ret;
	}

	public synchronized void releaseHolder(SSLContextHolder holder) {
		HashPair<File, File> pair = pairs.inverse().remove(holder);
		if(pair != null) {
			filesRefreshable.remove(pair.valueA);
			filesRefreshable.remove(pair.valueB);
			if(!filesRefreshable.contains(pair.valueA)) {
				refreshableFiles.remove(pair.valueA);
			}else {
				RefreshWatcher w = refreshableFiles.get(pair.valueA);
				if(w != null) {
					w.childrenA.remove(holder);
				}
			}
			if(!filesRefreshable.contains(pair.valueB)) {
				refreshableFiles.remove(pair.valueB);
			}else {
				RefreshWatcher w = refreshableFiles.get(pair.valueB);
				if(w != null) {
					w.childrenB.remove(holder);
				}
			}
		}
	}

	public synchronized void update() {
		if(refreshableFiles.isEmpty()) {
			return;
		}
		Set<SSLContextHolder> holdersThatNeedRefresh = null;
		for(RefreshWatcher w : refreshableFiles.values()) {
			long l = w.file.lastModified();
			if(l != w.lastModified) {
				byte[] newData;
				try {
					newData = readFileBytes(w.file);
				} catch (IOException e) {
					logger.error("Could not read certificate: " + w.file.getAbsolutePath(), e);
					continue;
				}
				w.lastModified = l;
				if(!Arrays.equals(w.data, newData)) {
					logger.info("TLS certificate was modified: " + w.file.getAbsolutePath());
					w.data = newData;
					for(SSLContextHolder a : w.childrenA) {
						a.pubKey = newData;
						if(holdersThatNeedRefresh == null) {
							holdersThatNeedRefresh = new HashSet<>();
						}
						holdersThatNeedRefresh.add(a);
					}
					for(SSLContextHolder b : w.childrenB) {
						b.privKey = newData;
						if(holdersThatNeedRefresh == null) {
							holdersThatNeedRefresh = new HashSet<>();
						}
						holdersThatNeedRefresh.add(b);
					}
				}
			}
		}
		if(holdersThatNeedRefresh != null) {
			boolean err = false;
			for(SSLContextHolder ctx : holdersThatNeedRefresh) {
				try {
					ctx.refresh();
				}catch(SSLException ex) {
					logger.error("Could not refresh TLS certificate pair " + ctx.name + "!", ex);
					err = true;
				}
			}
			if(!err) {
				logger.info("Refreshed TLS context caches");
			}else {
				logger.error("One or more TLS contexts could not be reloaded");
			}
		}
	}

	private RefreshWatcher getOrCreateRefresher(File file) throws SSLException {
		RefreshWatcher ret = refreshableFiles.get(file);
		if(ret != null) {
			return ret;
		}else {
			return new RefreshWatcher(file);
		}
	}

	private static byte[] readFileBytes(File file) throws IOException {
		try(InputStream is = new FileInputStream(file)) {
			return ByteStreams.toByteArray(is);
		}
	}

	private static String createName(File pubFile, File privFile) {
		return "{pub = " + pubFile.getName() + ", priv = " + privFile.getName() + "}";
	}

}
