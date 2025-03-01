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
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.proxy.connection.MinecraftConnection;
import com.velocitypowered.proxy.connection.backend.VelocityServerConnection;
import com.velocitypowered.proxy.connection.util.VelocityInboundConnection;
import com.velocitypowered.proxy.protocol.packet.PluginMessagePacket;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.util.AttributeKey;
import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.base.ListenerInitList;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class VelocityUnsafe {

	private static final Class<?> class_MinecraftConnection;
	private static final Method method_MinecraftConnection_getState;
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
	private static final Class<?> class_VelocityConnectionEvent;
	private static final Object enum_VelocityConnectionEvent_COMPRESSION_ENABLED;

	static {
		try {
			class_MinecraftConnection = Class.forName("com.velocitypowered.proxy.connection.MinecraftConnection");
			method_MinecraftConnection_getState = class_MinecraftConnection.getMethod("getState");
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
			class_VelocityConnectionEvent = Class.forName("com.velocitypowered.proxy.protocol.VelocityConnectionEvent");
			enum_VelocityConnectionEvent_COMPRESSION_ENABLED = class_VelocityConnectionEvent.getField("COMPRESSION_ENABLED").get(null);
		}catch(Exception ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	private static MinecraftConnection getMinecraftConnection(InboundConnection connection) {
		if(connection instanceof VelocityInboundConnection) {
			return ((VelocityInboundConnection) connection).getConnection();
		}else {
			throw new RuntimeException("Unknown InboundConnection type: " + connection.getClass().getName());
		}
	}

	private static MinecraftConnection getBackendConnection(ServerConnection connection) {
		if(connection instanceof VelocityServerConnection) {
			return ((VelocityServerConnection) connection).getConnection();
		}else {
			throw new RuntimeException("Unknown ServerConnection type: " + connection.getClass().getName());
		}
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
		((MinecraftConnection) minecraftConnection).close();
	}

	private static void disconnectMinecraftConnection(Object minecraftConnection, Component kickMessage) {
		MinecraftConnection conn = (MinecraftConnection) minecraftConnection;
		try {
			conn.closeWith(method_DisconnectPacket_create.invoke(null, kickMessage, conn.getProtocolVersion(),
					method_MinecraftConnection_getState.invoke(minecraftConnection)));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	public static Channel getInboundChannel(InboundConnection connection) {
		if(connection instanceof VelocityInboundConnection) {
			return ((VelocityInboundConnection) connection).getConnection().getChannel();
		}else {
			throw new RuntimeException("Unknown InboundConnection type: " + connection.getClass().getName());
		}
	}

	public static boolean isCompressionEnableEvent(Object event) {
		return event == enum_VelocityConnectionEvent_COMPRESSION_ENABLED;
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

	public static void disableCompression(ChannelHandler handler) {
		if(handler instanceof MinecraftConnection) {
			((MinecraftConnection) handler).setCompressionThreshold(-1);
		}else {
			throw new RuntimeException("Unknown MinecraftConnection type: " + handler.getClass().getName());
		}
	}

	public static void sendDataClient(InboundConnection connection, String channel, byte[] data) {
		getMinecraftConnection(connection).write(new PluginMessagePacket(channel, Unpooled.wrappedBuffer(data)));
	}

	public static void sendDataBackend(ServerConnection connection, String channel, byte[] data) {
		getBackendConnection(connection).write(new PluginMessagePacket(channel, Unpooled.wrappedBuffer(data)));
	}

}
