package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener;

public class EaglerListener implements IEaglerListenerInfo, IEaglerXServerListener {

	private final EaglerXServer<?> server;
	private final SocketAddress address;
	private final ConfigDataListener listenerConf;

	public EaglerListener(EaglerXServer<?> server, ConfigDataListener listenerConf) {
		this.server = server;
		this.address = listenerConf.getInjectAddress();
		this.listenerConf = listenerConf;
	}

	public EaglerListener(EaglerXServer<?> server, SocketAddress address, ConfigDataListener listenerConf) {
		this.server = server;
		this.address = address;
		this.listenerConf = listenerConf;
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
