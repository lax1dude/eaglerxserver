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

package net.lax1dude.eaglercraft.backend.server.velocity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.Multimap;
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
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.base.ListenerInitList;
import net.lax1dude.eaglercraft.backend.server.util.ClassProxy;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class VelocityUnsafe {

	private static final Class<?> class_LoginInboundConnection;
	private static final MethodHandle method_LoginInboundConnection_delegatedConnection;
	private static final Constructor<?> ctor_MinecraftConnection;
	private static final ClassProxy<MinecraftConnection> classProxy_MinecraftConnection;
	private static final MethodHandle method_MinecraftConnection_getState;
	private static final VarHandle field_MinecraftConnection_activeSessionHandler;
	private static final VarHandle field_MinecraftConnection_remoteAddress;
	private static final Class<?> class_AuthSessionHandler;
	private static final VarHandle field_AuthSessionHandler_mcConnection;
	private static final Class<?> class_DisconnectPacket;
	private static final Class<?> class_StateRegistry;
	private static final MethodHandle method_DisconnectPacket_create;
	private static final Class<?> class_VelocityServer;
	private static final VarHandle field_VelocityServer_cm;
	private static final Class<?> class_ConnectionManager;
	private static final MethodHandle method_ConnectionManager_getServerChannelInitializer;
	private static final Class<?> class_ServerChannelInitializerHolder;
	private static final MethodHandle method_ServerChannelInitializerHolder_get;
	private static final MethodHandle method_ServerChannelInitializerHolder_set;
	private static final MethodHandle method_ChannelInitializer_initChannel;
	private static final VarHandle field_ConnectionManager_endpoints;
	private static final VarHandle field_ConnectionManager_bossGroup;
	private static final VarHandle field_ConnectionManager_workerGroup;
	private static final VarHandle field_ConnectionManager_transportType;
	private static final Class<?> class_TransportType;
	private static final VarHandle field_TransportType_socketChannelFactory;
	private static final VarHandle field_TransportType_serverSocketChannelFactory;
	private static final Class<?> class_Endpoint;
	private static final MethodHandle method_Endpoint_getChannel;

	static {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			class_VelocityServer = Class.forName("com.velocitypowered.proxy.VelocityServer");
			class_LoginInboundConnection = Class
					.forName("com.velocitypowered.proxy.connection.client.LoginInboundConnection");
			method_LoginInboundConnection_delegatedConnection = Util.findDeclaredMethod(lookup,
					class_LoginInboundConnection, "delegatedConnection");
			ctor_MinecraftConnection = MinecraftConnection.class.getConstructor(Channel.class, class_VelocityServer);
			classProxy_MinecraftConnection = ClassProxy.bindProxy(VelocityUnsafe.class.getClassLoader(),
					MinecraftConnection.class);
			class_AuthSessionHandler = Class.forName("com.velocitypowered.proxy.connection.client.AuthSessionHandler");
			class_StateRegistry = Class.forName("com.velocitypowered.proxy.protocol.StateRegistry");
			method_MinecraftConnection_getState = lookup.findVirtual(class_AuthSessionHandler, "getState",
					MethodType.methodType(class_StateRegistry));
			field_MinecraftConnection_activeSessionHandler = Util.findDeclaredField(lookup, MinecraftConnection.class,
					"activeSessionHandler");
			field_MinecraftConnection_remoteAddress = Util.findDeclaredField(lookup, MinecraftConnection.class,
					"remoteAddress");
			field_AuthSessionHandler_mcConnection = Util.findDeclaredField(lookup, class_AuthSessionHandler,
					"mcConnection");
			class_DisconnectPacket = Class.forName("com.velocitypowered.proxy.protocol.packet.DisconnectPacket");
			method_DisconnectPacket_create = lookup.findStatic(class_DisconnectPacket, "create", MethodType
					.methodType(class_DisconnectPacket, Component.class, ProtocolVersion.class, class_StateRegistry));
			field_VelocityServer_cm = Util.findDeclaredField(lookup, class_VelocityServer, "cm");
			class_ConnectionManager = Class.forName("com.velocitypowered.proxy.network.ConnectionManager");
			class_ServerChannelInitializerHolder = Class
					.forName("com.velocitypowered.proxy.network.ServerChannelInitializerHolder");
			method_ConnectionManager_getServerChannelInitializer = lookup.findVirtual(class_ConnectionManager,
					"getServerChannelInitializer", MethodType.methodType(class_ServerChannelInitializerHolder));
			method_ServerChannelInitializerHolder_get = lookup.findVirtual(class_ServerChannelInitializerHolder, "get",
					MethodType.methodType(ChannelInitializer.class));
			method_ServerChannelInitializerHolder_set = lookup.findVirtual(class_ServerChannelInitializerHolder, "set",
					MethodType.methodType(void.class, ChannelInitializer.class));
			method_ChannelInitializer_initChannel = Util.findDeclaredMethod(lookup, ChannelInitializer.class,
					"initChannel", Channel.class);
			field_ConnectionManager_endpoints = Util.findDeclaredField(lookup, class_ConnectionManager, "endpoints");
			field_ConnectionManager_bossGroup = Util.findDeclaredField(lookup, class_ConnectionManager, "bossGroup");
			field_ConnectionManager_workerGroup = Util.findDeclaredField(lookup, class_ConnectionManager,
					"workerGroup");
			field_ConnectionManager_transportType = Util.findDeclaredField(lookup, class_ConnectionManager,
					"transportType");
			class_TransportType = Class.forName("com.velocitypowered.proxy.network.TransportType");
			field_TransportType_socketChannelFactory = Util.findDeclaredField(lookup, class_TransportType,
					"socketChannelFactory");
			field_TransportType_serverSocketChannelFactory = Util.findDeclaredField(lookup, class_TransportType,
					"serverSocketChannelFactory");
			class_Endpoint = Class.forName("com.velocitypowered.proxy.network.Endpoint");
			method_Endpoint_getChannel = lookup.findVirtual(class_Endpoint, "getChannel",
					MethodType.methodType(Channel.class));
		} catch (ReflectiveOperationException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static MinecraftConnection getMinecraftConnection(InboundConnection connection) {
		if (connection instanceof VelocityInboundConnection conn) {
			return conn.getConnection();
		} else if (class_LoginInboundConnection.isAssignableFrom(connection.getClass())) {
			try {
				return (MinecraftConnection) method_LoginInboundConnection_delegatedConnection.invoke(connection);
			} catch (Throwable e) {
				throw Util.propagateInvokeThrowable(e);
			}
		} else {
			throw new RuntimeException("Unknown InboundConnection type: " + connection.getClass().getName());
		}
	}

	private static MinecraftConnection getBackendConnection(ServerConnection connection) {
		if (connection instanceof VelocityServerConnection conn) {
			return conn.getConnection();
		} else {
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
		} catch (Throwable ex) {
			throw Util.propagateInvokeThrowable(ex);
		}
	}

	public static Channel getInboundChannel(InboundConnection connection) {
		return getMinecraftConnection(connection).getChannel();
	}

	public static void updateRealAddress(Object o, SocketAddress addr) {
		if (o instanceof MinecraftConnection) {
			field_MinecraftConnection_remoteAddress.set(o, addr);
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
		Object cm = field_VelocityServer_cm.get(server);
		Object holder;
		ChannelInitializer<Channel> parent;
		try {
			holder = method_ConnectionManager_getServerChannelInitializer.invoke(cm);
			parent = (ChannelInitializer<Channel>) method_ServerChannelInitializerHolder_get.invoke(holder);
		} catch (Throwable e) {
			throw Util.propagateInvokeThrowable(e);
		}
		VelocityEaglerChannelInitializer impl = new VelocityEaglerChannelInitializer((ch) -> {
			try {
				method_ChannelInitializer_initChannel.invoke(parent, ch);
			} catch (Throwable e) {
				throw Util.propagateInvokeThrowable(e);
			}
			Channel pc = ch.parent();
			if (pc != null) {
				IEaglerXServerListener listener = pc.attr(EAGLER_LISTENER).get();
				if (listener != null) {
					initHandler.init(listener, ch);
				}
			}
		});
		try {
			method_ServerChannelInitializerHolder_set.invoke(holder, impl);
		} catch (Throwable e) {
			throw Util.propagateInvokeThrowable(e);
		}
		return () -> {
			impl.impl = (ch) -> {
				try {
					method_ChannelInitializer_initChannel.invoke(parent, ch);
				} catch (Throwable e) {
					throw Util.propagateInvokeThrowable(e);
				}
			};
			try {
				ChannelInitializer<Channel> self = (ChannelInitializer<Channel>) method_ServerChannelInitializerHolder_get
						.invoke(holder);
				if (self == impl) {
					method_ServerChannelInitializerHolder_set.invoke(holder, parent);
				}
			} catch (Throwable e) {
				throw Util.propagateInvokeThrowable(e);
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void injectListenerAttr(ProxyServer server, InetSocketAddress address,
			ListenerInitList listenersToInit) {
		Object cm = field_VelocityServer_cm.get(server);
		Object obj = field_ConnectionManager_endpoints.get(cm);
		Object endpoint;
		if (obj instanceof Multimap) {
			Collection<Object> endpoints = ((Multimap) obj).get(address);
			if (!endpoints.isEmpty()) {
				endpoint = endpoints.iterator().next();
			} else {
				endpoint = null;
			}
		} else {
			endpoint = ((Map) obj).get(address);
		}
		if (endpoint != null) {
			IEaglerXServerListener listener = listenersToInit.offer(address);
			if (listener != null) {
				Channel ch;
				try {
					ch = (Channel) method_Endpoint_getChannel.invoke(endpoint);
				} catch (Throwable e) {
					throw Util.propagateInvokeThrowable(e);
				}
				ch.attr(EAGLER_LISTENER).set(listener);
				listener.reportVelocityInjected(ch);
			}
		}
	}

	public static void injectCompressionDisable(ProxyServer server, Player player) {
		// Note: This does not affect the MinecraftConnection in the pipeline or player
		// object
		// therefore performance is not a concern
		Object o = field_MinecraftConnection_activeSessionHandler.get(getMinecraftConnection(player));
		if (class_AuthSessionHandler.isAssignableFrom(o.getClass())) {
			final MinecraftConnection parent = (MinecraftConnection) field_AuthSessionHandler_mcConnection.get(o);
			field_AuthSessionHandler_mcConnection.set(o, classProxy_MinecraftConnection.createProxy(
					ctor_MinecraftConnection, new Object[] { parent.getChannel(), server }, (obj, meth, args) -> {
						if ("setCompressionThreshold".equals(meth.getName())) {
							// FUCK YOU!
							return null;
						}
						return meth.invoke(parent, args);
					}));
		} else {
			throw new RuntimeException("Unexpected session handler type: " + o.getClass().getName());
		}
	}

	public static void sendDataClient(InboundConnection connection, String channel, byte[] data) {
		getMinecraftConnection(connection).write(new PluginMessagePacket(channel, Unpooled.wrappedBuffer(data)));
	}

	public static void sendDataBackend(ServerConnection connection, String channel, byte[] data) {
		getBackendConnection(connection).write(new PluginMessagePacket(channel, Unpooled.wrappedBuffer(data)));
	}

	public static EventLoopGroup getBossEventLoopGroup(ProxyServer proxyIn) {
		return (EventLoopGroup) field_ConnectionManager_bossGroup.get(field_VelocityServer_cm.get(proxyIn));
	}

	public static EventLoopGroup getWorkerEventLoopGroup(ProxyServer proxyIn) {
		return (EventLoopGroup) field_ConnectionManager_workerGroup.get(field_VelocityServer_cm.get(proxyIn));
	}

	public static ChannelFactory<? extends Channel> getChannelFactory(ProxyServer proxyIn) {
		return (ChannelFactory<? extends Channel>) field_TransportType_socketChannelFactory
				.get(field_ConnectionManager_transportType.get(field_VelocityServer_cm.get(proxyIn)));
	}

	public static ChannelFactory<? extends Channel> getUnixChannelFactory(ProxyServer proxyIn) {
		return null;
	}

	public static ChannelFactory<? extends ServerChannel> getServerChannelFactory(ProxyServer proxyIn) {
		return (ChannelFactory<? extends ServerChannel>) field_TransportType_serverSocketChannelFactory
				.get(field_ConnectionManager_transportType.get(field_VelocityServer_cm.get(proxyIn)));
	}

	public static ChannelFactory<? extends ServerChannel> getServerUnixChannelFactory(ProxyServer proxyIn) {
		return null;
	}

}
