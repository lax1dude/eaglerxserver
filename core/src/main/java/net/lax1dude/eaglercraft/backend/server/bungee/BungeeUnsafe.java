package net.lax1dude.eaglercraft.backend.server.bungee;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.ForwardingSet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.base.ChannelInitializerHijacker;
import net.lax1dude.eaglercraft.backend.server.base.ListenerInitList;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public class BungeeUnsafe {

	private static final Class<?> class_InitialHandler;
	private static final Method method_InitialHandler_getBrandMessage;
	private static final Field field_InitialHandler_ch;
	private static final Class<?> class_ChannelWrapper;
	private static final Method method_ChannelWrapper_getHandle;
	private static final Method method_ChannelWrapper_close;
	private static final Class<?> class_PluginMessage;
	private static final Method method_PluginMessage_getData;
	private static final Class<?> class_BungeeCord;
	private static final Field field_BungeeCord_listeners;

	static {
		try {
			class_InitialHandler = Class.forName("net.md_5.bungee.connection.InitialHandler");
			method_InitialHandler_getBrandMessage = class_InitialHandler.getMethod("getBrandMessage");
			field_InitialHandler_ch = class_InitialHandler.getDeclaredField("ch");
			field_InitialHandler_ch.setAccessible(true);
			class_ChannelWrapper = Class.forName("net.md_5.bungee.netty.ChannelWrapper");
			method_ChannelWrapper_getHandle = class_ChannelWrapper.getMethod("getHandle");
			method_ChannelWrapper_close = class_ChannelWrapper.getMethod("close");
			class_PluginMessage = Class.forName("net.md_5.bungee.connection.PluginMessage");
			method_PluginMessage_getData = class_PluginMessage.getMethod("getData");
			class_BungeeCord = Class.forName("net.md_5.bungee.BungeeCord");
			field_BungeeCord_listeners = class_BungeeCord.getDeclaredField("listeners");
			field_BungeeCord_listeners.setAccessible(true);
		}catch(Exception ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	public static byte[] getBrandMessage(PendingConnection pendingConnection) {
		if(class_InitialHandler.isAssignableFrom(pendingConnection.getClass())) {
			try {
				Object obj = method_InitialHandler_getBrandMessage.invoke(pendingConnection);
				if(obj == null) {
					return null;
				}
				return (byte[]) method_PluginMessage_getData.invoke(obj);
			}catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
				throw Util.propagateReflectThrowable(ex);
			}
		}else {
			return null;
		}
	}

	public static Channel getInitialHandlerChannel(PendingConnection pendingConnection) {
		if(class_InitialHandler.isAssignableFrom(pendingConnection.getClass())) {
			try {
				return (Channel) method_ChannelWrapper_getHandle.invoke(field_InitialHandler_ch.get(pendingConnection));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
				throw Util.propagateReflectThrowable(ex);
			}
		}else {
			throw new RuntimeException("PendingConnection is an unknown type: " + pendingConnection.getClass().getName());
		}
	}

	public static void disconnectPlayerQuiet(ProxiedPlayer player) {
		PendingConnection pendingConnection = player.getPendingConnection();
		if(class_InitialHandler.isAssignableFrom(pendingConnection.getClass())) {
			try {
				method_ChannelWrapper_close.invoke(field_InitialHandler_ch.get(pendingConnection));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
				throw Util.propagateReflectThrowable(ex);
			}
		}else {
			throw new RuntimeException("PendingConnection is an unknown type: " + pendingConnection.getClass().getName());
		}
		Server serverConn = player.getServer();
		if(serverConn != null) {
			serverConn.disconnect(new TextComponent("Quitting"));
		}
	}

	private static class CleanupList implements Consumer<ChannelInitializerHijacker>, Runnable {

		protected List<ChannelInitializerHijacker> cleanup = new ArrayList<>();

		@Override
		public void accept(ChannelInitializerHijacker c) {
			synchronized(this) {
				if(cleanup != null) {
					cleanup.add(c);
					return;
				}
			}
			c.deactivate();
		}

		@Override
		public void run() {
			List<ChannelInitializerHijacker> cc;
			synchronized(this) {
				cc = new ArrayList<>(cleanup);
				cleanup = null;
			}
			for(ChannelInitializerHijacker c : cc) {
				c.deactivate();
			}
		}

	}

	public interface IListenerInitHandler {
		void init(IEaglerXServerListener listener, Channel channel);
	}

	public static Runnable injectChannelInitializer(ProxyServer server, IListenerInitHandler initHandler,
			Collection<IEaglerXServerListener> listenersList) {
		CleanupList cleanupList = new CleanupList();
		final ListenerInitList initList = new ListenerInitList(listenersList);
		try {
			final Set<Channel> channels = (Set<Channel>) field_BungeeCord_listeners.get(server);
			for(Channel ch : channels) {
				injectChannelInitializer(ch, initList, initHandler, cleanupList);
			}
			Set<Channel> hackSet = new ForwardingSet<Channel>() {
				@Override
				protected Set<Channel> delegate() {
					return channels;
				}
				@Override
				public boolean add(Channel element) {
					if(super.add(element)) {
						if(cleanupList.cleanup != null) {
							injectChannelInitializer(element, initList, initHandler, cleanupList);
						}
						return true;
					}else {
						return false;
					}
				}
			};
			field_BungeeCord_listeners.set(server, hackSet);
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			throw Util.propagateReflectThrowable(ex);
		}
		return cleanupList;
	}

	// Inspired by ViaVersion
	private static void injectChannelInitializer(Channel channel, ListenerInitList initList,
			IListenerInitHandler initHandler, Consumer<ChannelInitializerHijacker> cleanupCallback) {
		List<String> names = channel.pipeline().names();
		ChannelHandler foundHandler;
		Field foundField;
		eagler: {
			for(String name : names) {
				ChannelHandler handler = channel.pipeline().get(name);
				if(isServerInitializer(handler)) {
					try {
						foundField = handler.getClass().getDeclaredField("childHandler");
						foundField.setAccessible(true);
						foundHandler = handler;
						break eagler;
					}catch (IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
					}
				}
			}
			foundHandler = channel.pipeline().first();
			if(isServerInitializer(foundHandler)) {
				try {
					foundField = foundHandler.getClass().getDeclaredField("childHandler");
					foundField.setAccessible(true);
					break eagler;
				}catch (IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
					IEaglerXServerListener listener = initList.offer(channel.localAddress());
					if (listener != null) {
						throw new RuntimeException("Could not find ChannelBootstrapAccelerator to inject into for " + listener);
					}
				}
			}
			return;
		}
		final IEaglerXServerListener listener = initList.offer(channel.localAddress());
		if(listener != null) {
			injectInto(foundHandler, foundField, (channel2) -> {
				initHandler.init(listener, channel2);
			}, cleanupCallback);
			listener.reportNettyInjected(channel);
		}
	}

	private static void injectInto(ChannelHandler foundHandler, Field foundField, Consumer<Channel> init,
			Consumer<ChannelInitializerHijacker> cleanupCallback) {
		ChannelInitializer<Channel> parent;
		Method initChannel;
		try {
			parent = (ChannelInitializer<Channel>) foundField.get(foundHandler);
			initChannel = parent.getClass().getDeclaredMethod("initChannel", Channel.class);
			initChannel.setAccessible(true);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException e) {
			throw Util.propagateReflectThrowable(e);
		}
		ChannelInitializerHijacker newInit = new ChannelInitializerHijacker(init) {

			@Override
			protected void callParent(Channel channel) {
				try {
					initChannel.invoke(parent, channel);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw Util.propagateReflectThrowable(e);
				}
			}

			@Override
			protected boolean reInject() {
				Object newInitializer;
				try {
					newInitializer = foundField.get(foundHandler);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw Util.propagateReflectThrowable(e);
				}
				if(this != newInitializer) {
					System.err.println("Detected another plugin's channel initializer ("
							+ newInitializer.getClass().getName() + ") injected into the pipeline, "
							+ "reinjecting EaglerXServer again to make sure its first, because we "
							+ "really are that rude");
					injectInto(foundHandler, foundField, init, cleanupCallback);
					return true;
				}else {
					return false;
				}
			}

		};
		try {
			foundField.set(foundHandler, newInit);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			throw Util.propagateReflectThrowable(e);
		}
		cleanupCallback.accept(newInit);
	}

	private static boolean isServerInitializer(ChannelHandler handler) {
		return handler != null && ChannelInitializer.class.isAssignableFrom(handler.getClass())
				&& !"net.md_5.bungee.query.QueryHandler".equals(handler.getClass().getName());
	}

}
