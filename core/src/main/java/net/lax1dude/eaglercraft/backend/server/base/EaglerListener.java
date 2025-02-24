package net.lax1dude.eaglercraft.backend.server.base;

import java.io.File;
import java.net.SocketAddress;

import javax.net.ssl.SSLException;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener;
import net.lax1dude.eaglercraft.backend.server.base.config.SSLContextHolder;

public class EaglerListener implements IEaglerListenerInfo, IEaglerXServerListener {

	private final EaglerXServer<?> server;
	private final SocketAddress address;
	private final ConfigDataListener listenerConf;
	private final SSLContextHolder sslContext;

	EaglerListener(EaglerXServer<?> server, ConfigDataListener listenerConf) throws SSLException {
		this(server, listenerConf.getInjectAddress(), listenerConf);
	}

	EaglerListener(EaglerXServer<?> server, SocketAddress address, ConfigDataListener listenerConf) throws SSLException {
		this.server = server;
		this.address = address;
		this.listenerConf = listenerConf;
		if (listenerConf.isEnableTLS()) {
			this.sslContext = server.getCertificateManager().createHolder(
					new File(listenerConf.getTLSPublicChainFile()), new File(listenerConf.getTLSPrivateKeyFile()),
					listenerConf.isTLSAutoRefreshCert());
		} else {
			this.sslContext = null;
		}
	}

	public SSLContextHolder getSSLContext() {
		return sslContext;
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

}
