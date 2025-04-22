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

package net.lax1dude.eaglercraft.backend.server.bungee;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ForwardingSet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.base.ChannelInitializerHijacker;
import net.lax1dude.eaglercraft.backend.server.base.ListenerInitList;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.netty.ChannelWrapper;

public class BungeeUnsafe {

	private static final Class<?> class_InitialHandler;
	private static final Method method_InitialHandler_getBrandMessage;
	private static final Field field_InitialHandler_loginProfile;
	private static final Field field_InitialHandler_ch;
	private static final Class<?> class_ChannelWrapper;
	private static final Method method_ChannelWrapper_close;
	private static final Class<?> class_PluginMessage;
	private static final Method method_PluginMessage_getData;
	private static final Class<?> class_LoginResult;
	private static final Constructor<?> constructor_LoginResult;
	private static final Method method_LoginResult_getProperties;
	private static final Method method_LoginResult_setProperties;
	private static final Class<?> class_Property;
	private static final Constructor<?> constructor_Property;
	private static final Object isEaglerPlayerPropertyT;
	private static final Object isEaglerPlayerPropertyF;
	private static final Method method_Property_getName;
	private static final Method method_Property_getValue;
	private static final Class<?> class_BungeeCord;
	private static final Field field_BungeeCord_listeners;
	private static final Field field_BungeeCord_config;
	private static final Field field_BungeeCord_eventLoops;
	private static final Field field_BungeeCord_bossEventLoopGroup;
	private static final Field field_BungeeCord_workerEventLoopGroup;
	private static final Class<?> class_Configuration;
	private static final Method method_Configuration_isOnlineMode;
	private static final Method method_Configuration_getPlayerLimit;
	private static final Class<?> class_PipelineUtils;
	private static final Method method_PipelineUtils_getChannel;
	private static final Method method_PipelineUtils_getServerChannel;
	private static final Class<?> class_HandlerBoss;
	private static final Field field_HandlerBoss_channel;

	static {
		try {
			class_InitialHandler = Class.forName("net.md_5.bungee.connection.InitialHandler");
			method_InitialHandler_getBrandMessage = class_InitialHandler.getMethod("getBrandMessage");
			field_InitialHandler_loginProfile = class_InitialHandler.getDeclaredField("loginProfile");
			field_InitialHandler_loginProfile.setAccessible(true);
			field_InitialHandler_ch = class_InitialHandler.getDeclaredField("ch");
			field_InitialHandler_ch.setAccessible(true);
			class_ChannelWrapper = Class.forName("net.md_5.bungee.netty.ChannelWrapper");
			method_ChannelWrapper_close = class_ChannelWrapper.getMethod("close");
			class_PluginMessage = Class.forName("net.md_5.bungee.protocol.packet.PluginMessage");
			method_PluginMessage_getData = class_PluginMessage.getMethod("getData");
			class_LoginResult = Class.forName("net.md_5.bungee.connection.LoginResult");
			class_Property = Class.forName("net.md_5.bungee.protocol.Property");
			Class<?> propArrayClass = Array.newInstance(class_Property, 0).getClass();
			constructor_LoginResult = class_LoginResult.getConstructor(String.class, String.class, propArrayClass);
			method_LoginResult_getProperties = class_LoginResult.getMethod("getProperties");
			method_LoginResult_setProperties = class_LoginResult.getMethod("setProperties", propArrayClass);
			constructor_Property = class_Property.getConstructor(String.class, String.class, String.class);
			isEaglerPlayerPropertyT = constructor_Property.newInstance("isEaglerPlayer", "true", null);
			isEaglerPlayerPropertyF = constructor_Property.newInstance("isEaglerPlayer", "false", null);
			method_Property_getName = class_Property.getMethod("getName");
			method_Property_getValue = class_Property.getMethod("getValue");
			class_BungeeCord = Class.forName("net.md_5.bungee.BungeeCord");
			field_BungeeCord_listeners = class_BungeeCord.getDeclaredField("listeners");
			field_BungeeCord_listeners.setAccessible(true);
			field_BungeeCord_config = class_BungeeCord.getDeclaredField("config");
			field_BungeeCord_config.setAccessible(true);
			Field bossGroup = null, workerGroup = null, group = null;
			try {
				bossGroup = class_BungeeCord.getField("bossEventLoopGroup");
				workerGroup = class_BungeeCord.getField("workerEventLoopGroup");
			}catch(Exception ex) {
				group = class_BungeeCord.getField("eventLoops");
			}
			field_BungeeCord_eventLoops = group;
			field_BungeeCord_bossEventLoopGroup = bossGroup;
			field_BungeeCord_workerEventLoopGroup = workerGroup;
			class_Configuration = Class.forName("net.md_5.bungee.conf.Configuration");
			method_Configuration_isOnlineMode = class_Configuration.getMethod("isOnlineMode");
			method_Configuration_getPlayerLimit = class_Configuration.getMethod("getPlayerLimit");
			class_PipelineUtils = Class.forName("net.md_5.bungee.netty.PipelineUtils");
			method_PipelineUtils_getChannel = class_PipelineUtils.getMethod("getChannel", SocketAddress.class);
			method_PipelineUtils_getServerChannel = class_PipelineUtils.getMethod("getServerChannel", SocketAddress.class);
			class_HandlerBoss = Class.forName("net.md_5.bungee.netty.HandlerBoss");
			field_HandlerBoss_channel = class_HandlerBoss.getDeclaredField("channel");
			field_HandlerBoss_channel.setAccessible(true);
		}catch(ReflectiveOperationException ex) {
			throw new ExceptionInInitializerError(ex);
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
			}catch(ReflectiveOperationException ex) {
				throw Util.propagateReflectThrowable(ex);
			}
		}else {
			return null;
		}
	}

	public static Channel getInitialHandlerChannel(PendingConnection pendingConnection) {
		if(class_InitialHandler.isAssignableFrom(pendingConnection.getClass())) {
			try {
				return ((ChannelWrapper)field_InitialHandler_ch.get(pendingConnection)).getHandle();
			} catch (ReflectiveOperationException ex) {
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
			} catch (ReflectiveOperationException ex) {
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

	@SuppressWarnings("deprecation")
	public static boolean isOnlineMode(ProxyServer proxy) {
		try {
			return (Boolean) method_Configuration_isOnlineMode.invoke(field_BungeeCord_config.get(proxy));
		}catch(ReflectiveOperationException e) {
			return proxy.getConfig().isOnlineMode();
		}
	}

	@SuppressWarnings("deprecation")
	public static int getPlayerMax(ProxyServer proxy) {
		try {
			return (Integer) method_Configuration_getPlayerLimit.invoke(field_BungeeCord_config.get(proxy));
		}catch(ReflectiveOperationException e) {
			return proxy.getConfig().getPlayerLimit();
		}
	}

	private static boolean hasShownChannelWrapperWarning = false;

	public static void injectCompressionDisable(PendingConnection conn) {
		if(class_InitialHandler.isAssignableFrom(conn.getClass())) {
			try {
				ChannelWrapper ch = (ChannelWrapper) field_InitialHandler_ch.get(conn);
				if(!ch.isClosing()) {
					if(ch.getClass() != class_ChannelWrapper && !hasShownChannelWrapperWarning) {
						hasShownChannelWrapperWarning = true;
						System.err.println("ERROR: ChannelWrapper is unknown class \"" + ch.getClass().getName()
								+ "\" and will be overridden, set compression threshold to -1 in the BungeeCord "
								+ "config.yml if that is an issue");
					}
					field_InitialHandler_ch.set(conn, new ChannelWrapper((ChannelHandlerContext) Proxy.newProxyInstance(
							BungeeUnsafe.class.getClassLoader(), new Class[] { ChannelHandlerContext.class },
							(proxy, meth, args) -> {
						if ("channel".equals(meth.getName())) {
							return ch.getHandle();
						}
						throw new IllegalStateException();
					})) {
						@Override
						public void setCompressionThreshold(int compressionThreshold) {
							// FUCK YOU!!!
						}
					});
				}
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}else {
			throw new RuntimeException("PendingConnection is an unknown type: " + conn.getClass().getName());
		}
	}

	public static String getTexturesProperty(PendingConnection conn) {
		if(class_InitialHandler.isAssignableFrom(conn.getClass())) {
			try {
				Object loginResult = field_InitialHandler_loginProfile.get(conn);
				if(loginResult != null) {
					Object[] props = (Object[]) method_LoginResult_getProperties.invoke(loginResult);
					if(props != null) {
						for(int i = 0; i < props.length; ++i) {
							Object p = props[i];
							if("textures".equals(method_Property_getName.invoke(p))) {
								return (String) method_Property_getValue.invoke(p);
							}
						}
					}
				}
				return null;
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}else {
			throw new RuntimeException("PendingConnection is an unknown type: " + conn.getClass().getName());
		}
	}

	public static void updateRealAddress(Object handler, SocketAddress addr) {
		if(class_HandlerBoss.isAssignableFrom(handler.getClass())) {
			try {
				((ChannelWrapper) field_HandlerBoss_channel.get(handler)).setRemoteAddress(addr);
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
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
		} catch (ReflectiveOperationException ex) {
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
					}catch (ReflectiveOperationException ex) {
					}
				}
			}
			foundHandler = channel.pipeline().first();
			if(isServerInitializer(foundHandler)) {
				try {
					foundField = foundHandler.getClass().getDeclaredField("childHandler");
					foundField.setAccessible(true);
					break eagler;
				}catch (ReflectiveOperationException ex) {
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
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
		ChannelInitializerHijacker newInit = new ChannelInitializerHijacker(init) {

			@Override
			protected void callParent(Channel channel) {
				try {
					initChannel.invoke(parent, channel);
				} catch (ReflectiveOperationException e) {
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
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
		cleanupCallback.accept(newInit);
	}

	private static boolean isServerInitializer(ChannelHandler handler) {
		return handler != null && ChannelInboundHandlerAdapter.class.isAssignableFrom(handler.getClass())
				&& !"net.md_5.bungee.query.QueryHandler".equals(handler.getClass().getName());
	}

	public static EventLoopGroup getBossEventLoopGroup(ProxyServer proxy) {
		if(field_BungeeCord_bossEventLoopGroup != null) {
			try {
				return (EventLoopGroup) field_BungeeCord_bossEventLoopGroup.get(proxy);
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}else {
			return null;
		}
	}

	public static EventLoopGroup getWorkerEventLoopGroup(ProxyServer proxy) {
		if(field_BungeeCord_workerEventLoopGroup != null) {
			try {
				return (EventLoopGroup) field_BungeeCord_workerEventLoopGroup.get(proxy);
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}else if(field_BungeeCord_eventLoops != null) {
			try {
				return (EventLoopGroup) field_BungeeCord_eventLoops.get(proxy);
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}else {
			throw new IllegalStateException("Event loop group field could not be found");
		}
	}

	public static Function<SocketAddress, Class<? extends Channel>> getChannelFactory() {
		return (addr) -> {
			try {
				return (Class<? extends Channel>) method_PipelineUtils_getChannel.invoke(null, addr);
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static Function<SocketAddress, Class<? extends ServerChannel>> getServerChannelFactory() {
		return (addr) -> {
			try {
				return (Class<? extends ServerChannel>) method_PipelineUtils_getServerChannel.invoke(null, addr);
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		};
	}

	private static final Predicate<Object> removeTexturesProperty = (obj) -> {
		try {
			return "textures".equals(method_Property_getName.invoke(obj));
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	};

	private static final Predicate<Object> removeEaglerPlayerProperty = (obj) -> {
		try {
			return "isEaglerPlayer".equals(method_Property_getName.invoke(obj));
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	};

	public static class PropertyInjector {

		private final Object loginResult;
		private final List<Object> propList;

		protected PropertyInjector(Object loginResult, Object[] propList) {
			this.loginResult = loginResult;
			this.propList = new LinkedList<>();
			if(propList != null) {
				for(int i = 0; i < propList.length; ++i) {
					this.propList.add(propList[i]);
				}
			}
		}

		public void injectTexturesProperty(String value, String signature) {
			Object o;
			try {
				o = constructor_Property.newInstance("textures", value, signature);
			}catch(ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
			propList.removeIf(removeTexturesProperty);
			propList.add(o);
		}

		public void injectIsEaglerPlayerProperty(boolean val) {
			propList.removeIf(removeEaglerPlayerProperty);
			propList.add(val ? isEaglerPlayerPropertyT : isEaglerPlayerPropertyF);
		}

		public void complete() {
			try {
				method_LoginResult_setProperties.invoke(loginResult, new Object[] {
						propList.toArray((Object[]) Array.newInstance(class_Property, propList.size())) });
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}

	}

	public static BungeeUnsafe.PropertyInjector propertyInjector(PendingConnection conn) {
		if(class_InitialHandler.isAssignableFrom(conn.getClass())) {
			try {
				Object loginResult = field_InitialHandler_loginProfile.get(conn);
				Object[] oldPropertyList = null;
				if(loginResult == null) {
					loginResult = constructor_LoginResult.newInstance(conn.getName(),
							Util.toUUIDStringUndashed(conn.getUniqueId()).toString(), null);
					field_InitialHandler_loginProfile.set(conn, loginResult);
				}else {
					oldPropertyList = (Object[]) method_LoginResult_getProperties.invoke(loginResult);
				}
				return new PropertyInjector(loginResult, oldPropertyList);
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}else {
			throw new RuntimeException("PendingConnection is an unknown type: " + conn.getClass().getName());
		}
	}

}
