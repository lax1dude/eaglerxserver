package net.lax1dude.eaglercraft.backend.server.base;

import java.io.File;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLException;

import com.google.common.collect.ImmutableList;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.ITLSManager;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.base.EaglerAttributeManager.EaglerAttributeHolder;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;

public class EaglerListener implements IEaglerListenerInfo, IEaglerXServerListener {

	private final EaglerXServer<?> server;
	private final SocketAddress address;
	private final ConfigDataListener listenerConf;
	private final EaglerAttributeHolder attrHolder;
	private final boolean sslPluginManaged;
	private final ISSLContextProvider sslContext;
	private final byte[] legacyRedirectAddressBuf;
	private byte[] cachedServerIcon;
	private List<String> cachedServerMOTD;

	EaglerListener(EaglerXServer<?> server, ConfigDataListener listenerConf) throws SSLException {
		this(server, listenerConf.getInjectAddress(), listenerConf);
	}

	EaglerListener(EaglerXServer<?> server, SocketAddress address, ConfigDataListener listenerConf) throws SSLException {
		this.server = server;
		this.address = address;
		this.listenerConf = listenerConf;
		this.attrHolder = server.getEaglerAttribManager().createEaglerHolder();
		if (listenerConf.isEnableTLS()) {
			this.sslPluginManaged = listenerConf.isTLSManagedByExternalPlugin();
			if(this.sslPluginManaged) {
				this.sslContext = new SSLContextHolderPlugin(this);
			}else {
				this.sslContext = server.getCertificateManager().createHolder(
						new File(listenerConf.getTLSPublicChainFile()), new File(listenerConf.getTLSPrivateKeyFile()),
						listenerConf.getTLSPrivateKeyPassword(), listenerConf.isTLSAutoRefreshCert());
			}
		} else {
			this.sslPluginManaged = false;
			this.sslContext = null;
		}
		if (listenerConf.getRedirectLegacyClientsTo() != null) {
			this.legacyRedirectAddressBuf = WebSocketEaglerInitialHandler.prepareRedirectAddr(listenerConf.getRedirectLegacyClientsTo());
		}else {
			this.legacyRedirectAddressBuf = null;
		}
	}

	public ISSLContextProvider getSSLContext() {
		return sslContext;
	}

	@Override
	public <T> T get(IAttributeKey<T> key) {
		return attrHolder.get(key);
	}

	@Override
	public <T> void set(IAttributeKey<T> key, T value) {
		attrHolder.set(key, value);
	}

	@Override
	public String getName() {
		return listenerConf.getListenerName();
	}

	@Override
	public SocketAddress getAddress() {
		return address;
	}

	@Override
	public boolean isDualStack() {
		return listenerConf.isDualStack();
	}

	@Override
	public boolean isTLSEnabled() {
		return listenerConf.isEnableTLS();
	}

	@Override
	public boolean isTLSRequired() {
		return listenerConf.isRequireTLS();
	}

	@Override
	public boolean isTLSManagedByPlugin() {
		return sslPluginManaged;
	}

	@Override
	public ITLSManager getTLSManager() throws IllegalStateException {
		if(!listenerConf.isEnableTLS()) {
			throw new IllegalStateException("TLS is not enabled on this listener!");
		}
		if(!sslPluginManaged) {
			throw new IllegalStateException("TLS manager is disabled for this listener! (Set 'tls_managed_by_external_plugin' to true)");
		}
		return (ITLSManager) sslContext;
	}

	@Override
	public byte[] getServerIcon() {
		return cachedServerIcon;
	}

	@Override
	public void setServerIcon(byte[] pixels) {
		if(pixels != null && pixels.length != 16384) {
			throw new IllegalArgumentException("Server icon is the wrong length, should be 16384");
		}
		cachedServerIcon = pixels;
	}

	@Override
	public List<String> getServerMOTD() {
		return cachedServerMOTD;
	}

	@Override
	public void setServerMOTD(List<String> motd) {
		if(motd.size() == 0) {
			cachedServerMOTD = Collections.emptyList();
		}else if(motd.size() == 1) {
			cachedServerMOTD = ImmutableList.of(motd.get(0));
		}else {
			cachedServerMOTD = ImmutableList.of(motd.get(0), motd.get(1));
		}
	}

	@Override
	public boolean isForwardIP() {
		return listenerConf.isForwardIP();
	}

	@Override
	public boolean matchListenerAddress(SocketAddress addr) {
		return addr.equals(listenerConf.getInjectAddress());
	}

	@Override
	public void reportVelocityInjected(Channel channel) {
		server.logger().info("Listener \"" + listenerConf.getListenerName() + "\" injected into channel " + channel + " successfully (Velocity method)");
	}

	@Override
	public void reportPaperMCInjected() {
		server.logger().info("Default listener injected into server channel successfully (PaperMC method)");
	}

	@Override
	public void reportNettyInjected(Channel channel) {
		server.logger().info("Listener \"" + listenerConf.getListenerName() + "\" injected into channel " + channel + " successfully (Generic Netty method)");
	}

	public byte[] getLegacyRedirectAddressBuf() {
		return legacyRedirectAddressBuf;
	}

	public boolean isAllowMOTD() {
		return listenerConf.isAllowMOTD();
	}

	public boolean isAllowQuery() {
		return listenerConf.isAllowQuery();
	}

	public boolean isShowMOTDPlayerList() {
		return listenerConf.isShowMOTDPlayerList();
	}

	public ConfigDataListener getConfigData() {
		return listenerConf;
	}

}
