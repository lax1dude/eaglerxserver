package net.lax1dude.eaglercraft.backend.server.velocity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.function.Consumer;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.util.AttributeKey;
import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.base.ListenerInitList;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class VelocityUnsafe {

	private static final Class<?> class_ConnectedPlayer;
	private static final Method method_ConnectedPlayer_getConnection;
	private static final Class<?> class_InitialInboundConnection;
	private static final Method method_InitialInboundConnection_getConnection;
	private static final Class<?> class_LoginInboundConnection;
	private static final Method method_LoginInboundConnection_delegatedConnection;
	private static final Class<?> class_MinecraftConnection;
	private static final Method method_MinecraftConnection_getChannel;
	private static final Method method_MinecraftConnection_getProtocolVersion;
	private static final Method method_MinecraftConnection_getState;
	private static final Method method_MinecraftConnection_close;
	private static final Method method_MinecraftConnection_closeWith;
	private static final Class<?> class_DisconnectPacket;
	private static final Class<?> class_StateRegistry;
	private static final Method method_DisconnectPacket_create;
	private static final Class<?> class_VelocityServer;
	private static final Field field_VelocityServer_cm;
	private static final Class<?> class_ConnectionManager;
	private static final Method method_ConnectionManager_getServerChannelInitializer;
	private static final Field field_ConnectionManager_endpoints;
	private static final Class<?> class_Endpoint;
	private static final Method method_Endpoint_getChannel;
	private static final Class<?> class_ServerChannelInitializerHolder;
	private static final Method method_ServerChannelInitializerHolder_get;
	private static final Method method_ServerChannelInitializerHolder_set;
	private static final Method method_ChannelInitializer_initChannel;

	static {
		try {
			class_ConnectedPlayer = Class.forName("com.velocitypowered.proxy.connection.client.ConnectedPlayer");
			method_ConnectedPlayer_getConnection = class_ConnectedPlayer.getMethod("getConnection");
			class_InitialInboundConnection = Class.forName("com.velocitypowered.proxy.connection.client.InitialInboundConnection");
			method_InitialInboundConnection_getConnection = class_InitialInboundConnection.getMethod("getConnection");
			class_LoginInboundConnection = Class.forName("com.velocitypowered.proxy.connection.client.LoginInboundConnection");
			method_LoginInboundConnection_delegatedConnection = class_LoginInboundConnection.getMethod("delegatedConnection");
			class_MinecraftConnection = Class.forName("com.velocitypowered.proxy.connection.MinecraftConnection");
			method_MinecraftConnection_getChannel = class_MinecraftConnection.getMethod("getChannel");
			method_MinecraftConnection_getProtocolVersion = class_MinecraftConnection.getMethod("getProtocolVersion");
			method_MinecraftConnection_getState = class_MinecraftConnection.getMethod("getState");
			method_MinecraftConnection_close = class_MinecraftConnection.getMethod("close");
			method_MinecraftConnection_closeWith = class_MinecraftConnection.getMethod("closeWith", Object.class);
			class_DisconnectPacket = Class.forName("com.velocitypowered.proxy.protocol.packet.DisconnectPacket");
			class_StateRegistry = Class.forName("com.velocitypowered.proxy.protocol.StateRegistry");
			method_DisconnectPacket_create = class_DisconnectPacket.getMethod("create", Component.class, ProtocolVersion.class, class_StateRegistry);
			class_VelocityServer = Class.forName("com.velocitypowered.proxy.VelocityServer");
			field_VelocityServer_cm = class_VelocityServer.getDeclaredField("cm");
			field_VelocityServer_cm.setAccessible(true);
			class_ConnectionManager = Class.forName("com.velocitypowered.proxy.network.ConnectionManager");
			method_ConnectionManager_getServerChannelInitializer = class_ConnectionManager.getMethod("getServerChannelInitializer");
			field_ConnectionManager_endpoints = class_ConnectionManager.getDeclaredField("endpoints");
			field_ConnectionManager_endpoints.setAccessible(true);
			class_Endpoint = Class.forName("com.velocitypowered.proxy.network.Endpoint");
			method_Endpoint_getChannel = class_Endpoint.getMethod("getChannel");
			class_ServerChannelInitializerHolder = Class.forName("com.velocitypowered.proxy.network.ServerChannelInitializerHolder");
			method_ServerChannelInitializerHolder_get = class_ServerChannelInitializerHolder.getMethod("get");
			method_ServerChannelInitializerHolder_set = class_ServerChannelInitializerHolder.getMethod("set", ChannelInitializer.class);
			method_ChannelInitializer_initChannel = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
			method_ChannelInitializer_initChannel.setAccessible(true);
		}catch(Exception ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	private static Object getMinecraftConnection(InboundConnection connection) {
		Class<?> clz = connection.getClass();
		Object ret;
		try {
			if(class_InitialInboundConnection.isAssignableFrom(clz)) {
				ret = method_InitialInboundConnection_getConnection.invoke(connection);
			}else if(class_LoginInboundConnection.isAssignableFrom(clz)) {
				ret = method_LoginInboundConnection_delegatedConnection.invoke(connection);
			}else if(class_ConnectedPlayer.isAssignableFrom(clz)) {
				ret = method_ConnectedPlayer_getConnection.invoke(connection);
			}else {
				throw new RuntimeException("Unknown InboundConnection type: " + clz.getName());
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw Util.propagateReflectThrowable(ex);
		}
		if(ret == null) {
			throw new NullPointerException();
		}
		return ret;
	}

	private static Object getMinecraftConnection(Player player) {
		Class<?> clz = player.getClass();
		Object ret;
		if(class_ConnectedPlayer.isAssignableFrom(clz)) {
			try {
				ret = method_ConnectedPlayer_getConnection.invoke(player);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				throw Util.propagateReflectThrowable(ex);
			}
		}else {
			throw new RuntimeException("Unknown Player type: " + clz.getName());
		}
		if(ret == null) {
			throw new NullPointerException();
		}
		return ret;
	}

	public static void disconnectInbound(InboundConnection connection) {
		disconnectMinecraftConnection(getMinecraftConnection(connection));
	}

	public static void disconnectInbound(InboundConnection connection, Component kickMessage) {
		disconnectMinecraftConnection(getMinecraftConnection(connection), kickMessage);
	}

	public static void disconnectPlayerQuiet(Player connection) {
		disconnectMinecraftConnection(getMinecraftConnection(connection));
	}

	private static void disconnectMinecraftConnection(Object minecraftConnection) {
		try {
			method_MinecraftConnection_close.invoke(minecraftConnection);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	private static void disconnectMinecraftConnection(Object minecraftConnection, Component kickMessage) {
		try {
			method_MinecraftConnection_closeWith.invoke(minecraftConnection,
					method_DisconnectPacket_create.invoke(null, kickMessage,
							method_MinecraftConnection_getProtocolVersion.invoke(minecraftConnection),
							method_MinecraftConnection_getState.invoke(minecraftConnection)));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	public static Channel getInboundChannel(InboundConnection connection) {
		return getChannel(getMinecraftConnection(connection));
	}

	public static Channel getPlayerChannel(Player connection) {
		return getChannel(getMinecraftConnection(connection));
	}

	private static Channel getChannel(Object minecraftConnection) {
		try {
			return (Channel) method_MinecraftConnection_getChannel.invoke(minecraftConnection);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	public interface IListenerInitHandler {
		void init(IEaglerXServerListener listener, Channel channel);
	}

	private static final AttributeKey<IEaglerXServerListener> EAGLER_LISTENER = AttributeKey.valueOf("eagler$3");

	private static class VelocityEaglerChannelInitializer extends ChannelInitializer<Channel> {

		protected Consumer<Channel> impl;

		protected VelocityEaglerChannelInitializer(Consumer<Channel> impl) {
			this.impl = impl;
		}

		@Override
		protected void initChannel(Channel var1) throws Exception {
			impl.accept(var1);
		}

	}

	public static Runnable injectChannelInitializer(ProxyServer server, IListenerInitHandler initHandler) {
		try {
			Object cm = field_VelocityServer_cm.get(server);
			Object holder = method_ConnectionManager_getServerChannelInitializer.invoke(cm);
			ChannelInitializer<Channel> parent = (ChannelInitializer<Channel>) method_ServerChannelInitializerHolder_get.invoke(holder);
			VelocityEaglerChannelInitializer impl = new VelocityEaglerChannelInitializer((ch) -> {
				try {
					method_ChannelInitializer_initChannel.invoke(parent, ch);
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					throw Util.propagateReflectThrowable(e);
				}
				Channel pc = ch.parent();
				if(pc != null) {
					IEaglerXServerListener listener = pc.attr(EAGLER_LISTENER).get();
					if(listener != null) {
						initHandler.init(listener, ch);
					}
				}
			});
			return () -> {
				impl.impl = (ch) -> {
					try {
						method_ChannelInitializer_initChannel.invoke(parent, ch);
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						throw Util.propagateReflectThrowable(e);
					}
				};
				try {
					ChannelInitializer<Channel> self = (ChannelInitializer<Channel>) method_ServerChannelInitializerHolder_get.invoke(holder);
					if(self == impl) {
						method_ServerChannelInitializerHolder_set.invoke(holder, parent);
					}
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					throw Util.propagateReflectThrowable(e);
				}
			};
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static void injectListenerAttr(ProxyServer server, InetSocketAddress address, ListenerInitList listenersToInit) {
		try {
			Object cm = field_VelocityServer_cm.get(server);
			Map<InetSocketAddress, Object> map = (Map<InetSocketAddress, Object>) field_ConnectionManager_endpoints.get(cm);
			Object endpoint = map.get(address);
			if(endpoint != null) {
				IEaglerXServerListener listener = listenersToInit.offer(address);
				if(listener != null) {
					Channel ch = (Channel) method_Endpoint_getChannel.invoke(endpoint);
					ch.attr(EAGLER_LISTENER).set(listener);
					listener.reportVelocityInjected(ch);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

}
