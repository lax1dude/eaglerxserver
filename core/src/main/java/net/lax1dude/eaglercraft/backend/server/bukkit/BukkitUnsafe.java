package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class BukkitUnsafe {

	public static class LoginConnectionHolder {

		public final Object networkManager;
		public final Object loginListener;
		public final Object gameProfile;

		protected LoginConnectionHolder(Object networkManager, Object loginListener, Object gameProfile) {
			this.networkManager = networkManager;
			this.loginListener = loginListener;
			this.gameProfile = gameProfile;
		}

		public Object getGameProfile() {
			return gameProfile;
		}

		public SocketAddress getRemoteAddress() {
			try {
				Channel c = (Channel) field_NetworkManager_channel.get(networkManager);
				return c != null ? c.remoteAddress() : null;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}

		public UUID getUniqueId() {
			try {
				return (UUID) method_GameProfile_getId.invoke(gameProfile);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}

		public String getUsername() {
			try {
				return (String) method_GameProfile_getName.invoke(gameProfile);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}

		public boolean isConnected() {
			try {
				Channel c = (Channel) field_NetworkManager_channel.get(networkManager);
				return c != null && c.isActive();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}

		public void disconnect() {
			try {
				method_LoginListener_disconnect.invoke(loginListener, "Connection Closed");
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}

		public void disconnect(String message) {
			try {
				method_LoginListener_disconnect.invoke(loginListener, message);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}

	}

	private static Class<?> class_NetworkManager = null;
	private static Field field_NetworkManager_channel = null;
	private static Method method_NetworkManager_getPacketListener = null;
	private static Class<?> class_LoginListener_maybe = null;
	private static Method method_LoginListener_disconnect = null;
	private static Field field_LoginListener_gameProfile = null;
	private static Class<?> class_GameProfile = null;
	private static Method method_GameProfile_getId = null;
	private static Method method_GameProfile_getName = null;

	private static synchronized void bindLoginConnection(Object networkManager) {
		if(class_NetworkManager != null) {
			return;
		}
		Class<?> clz = networkManager.getClass();
		try {
			field_NetworkManager_channel = clz.getField("channel");
			method_NetworkManager_getPacketListener = clz.getMethod("getPacketListener");
			Object packetListener = method_NetworkManager_getPacketListener.invoke(networkManager);
			if(packetListener == null) {
				throw new IllegalStateException("NetworkManager.getPacketListener is null!");
			}
			Class<?> clz2 = packetListener.getClass();
			method_LoginListener_disconnect = clz2.getMethod("disconnect", String.class);
			Field[] fields = clz2.getDeclaredFields();
			for(int i = 0; i < fields.length; ++i) {
				Field f = fields[i];
				if(f.getType().getSimpleName().equals("GameProfile")) {
					field_LoginListener_gameProfile = f;
					break;
				}
			}
			Object gameProfile = field_LoginListener_gameProfile.get(packetListener);
			if(gameProfile == null) {
				throw new IllegalStateException("LoginListener.gameProfile is null!");
			}
			Class<?> clz3 = gameProfile.getClass();
			method_GameProfile_getId = clz3.getMethod("getId");
			method_GameProfile_getName = clz3.getMethod("getName");
			class_LoginListener_maybe = clz2;
			class_NetworkManager = clz;
		}catch(IllegalStateException ex) {
			throw ex;
		}catch(Exception ex) {
			throw Util.propagateReflectThrowable(ex);
		}
		
	}

	public static LoginConnectionHolder getLoginConnection(Object networkManager) {
		if(class_NetworkManager == null) {
			bindLoginConnection(networkManager);
		}
		try {
			Object packetListener = method_NetworkManager_getPacketListener.invoke(networkManager);
			if(packetListener == null) {
				throw new IllegalStateException("NetworkManager.getPacketListener is null!");
			}
			if(!class_LoginListener_maybe.isAssignableFrom(packetListener.getClass())) {
				throw new IllegalStateException("PacketListener class is not LoginListener: "
						+ packetListener.getClass().getName() + " != " + class_LoginListener_maybe.getName());
			}
			Object gameProfile = field_LoginListener_gameProfile.get(packetListener);
			if(gameProfile == null) {
				throw new IllegalStateException("LoginListener.gameProfile is null!");
			}
			return new LoginConnectionHolder(networkManager, packetListener, gameProfile);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private static Class<?> class_CraftPlayer = null;
	private static Method method_CraftPlayer_getHandle = null;
	private static Class<?> class_EntityPlayer = null;
	private static Field field_EntityPlayer_playerConnection = null;
	private static Class<?> class_PlayerConnection = null;
	private static Field field_PlayerConnection_networkManager = null;

	private static synchronized void bindCraftPlayer(Player playerObject) {
		if(class_CraftPlayer != null) {
			return;
		}
		Class<?> clz = playerObject.getClass();
		try {
			method_CraftPlayer_getHandle = clz.getMethod("getHandle");
			Object entityPlayer = method_CraftPlayer_getHandle.invoke(playerObject);
			Class<?> clz2 = entityPlayer.getClass();
			field_EntityPlayer_playerConnection = clz2.getField("playerConnection");
			Object playerConnection = field_EntityPlayer_playerConnection.get(entityPlayer);
			Class<?> clz3 = playerConnection.getClass();
			field_PlayerConnection_networkManager = clz3.getField("networkManager");
			Object networkManager = field_PlayerConnection_networkManager.get(playerConnection);
			Class<?> clz4 = networkManager.getClass();
			field_NetworkManager_channel = clz4.getField("channel");
			class_NetworkManager = clz4;
			class_PlayerConnection = clz3;
			class_EntityPlayer = clz2;
			class_CraftPlayer = clz;
		}catch(Exception ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	public static Channel getPlayerChannel(Player playerObject) {
		if(class_CraftPlayer == null) {
			bindCraftPlayer(playerObject);
		}
		try {
			return (Channel) field_NetworkManager_channel.get(field_PlayerConnection_networkManager
					.get(field_EntityPlayer_playerConnection.get(method_CraftPlayer_getHandle.invoke(playerObject))));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

}
