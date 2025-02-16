package net.lax1dude.eaglercraft.backend.server.velocity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;

import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
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

}
