package net.lax1dude.eaglercraft.backend.server.base;

import java.util.function.Consumer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class ChannelInitializerHijacker extends ChannelInitializer<Channel> {

	private class ImplInitial implements Consumer<Channel> {

		@Override
		public void accept(Channel channel) {
			Consumer<Channel> run;
			eagler: {
				synchronized(ChannelInitializerHijacker.this) {
					run = impl;
					if(run != this) {
						break eagler;
					}
					if(reInject()) {
						impl = ChannelInitializerHijacker.this::callParent;
						run = (c) -> c.close(channel.voidPromise());
						break eagler;
					}
					run = impl = ChannelInitializerHijacker.this::callParentAndInit;
				}
				run.accept(channel);
				return;
			}
			if(run != null) {
				run.accept(channel);
			}
		}

	}

	protected final Consumer<Channel> initServerChild;

	public ChannelInitializerHijacker(Consumer<Channel> initServerChild) {
		this.initServerChild = initServerChild;
	}

	protected Consumer<Channel> impl = new ImplInitial();

	protected abstract void callParent(Channel channel);

	protected void callParentAndInit(Channel channel) {
		callParent(channel);
		initServerChild.accept(channel);
	}

	protected abstract boolean reInject();

	@Override
	protected void initChannel(Channel var1) throws Exception {
		impl.accept(var1);
	}

	public void deactivate() {
		impl = ChannelInitializerHijacker.this::callParent;
	}

}
