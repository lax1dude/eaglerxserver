package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import com.google.common.collect.ForwardingList;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.papermc.paper.network.ChannelInitializeListener;
import net.kyori.adventure.key.Key;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.base.ChannelInitializerHijacker;
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

	private static volatile Class<?> class_NetworkManager = null;
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

	private static volatile Class<?> class_CraftPlayer = null;
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

	@SuppressWarnings("unused")
	private static class KeyHolder {
		public static final Key EAGLER_KEY = Key.key("eaglerxserver", "channel_initializer");
	}

	public static Runnable injectChannelInitializer(Server server, Consumer<Channel> initHandler, IEaglerXServerListener listener) {
		Object eaglerKey;
		Class<?> paperChannelInitHolder;
		Class<?> paperChannelInitListener;
		try {
			eaglerKey = Class.forName("net.lax1dude.eaglercraft.backend.server.bukkit.BukkitUnsafe.KeyHolder").getField("EAGLER_KEY").get(null);
			paperChannelInitHolder = Class.forName("io.papermc.paper.network.ChannelInitializeListenerHolder");
			paperChannelInitListener = Class.forName("io.papermc.paper.network.ChannelInitializeListener");
		}catch(ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
			return injectChannelInitializerOld(server, initHandler, listener);
		}
		return injectChannelInitializerPaper(paperChannelInitHolder, paperChannelInitListener, eaglerKey, initHandler, listener);
	}

	private static Runnable injectChannelInitializerPaper(Class<?> paperChannelInitHolder,
			Class<?> paperChannelInitListener, Object eaglerKey, Consumer<Channel> initHandler,
			IEaglerXServerListener listener) {
		try {
			Method addListener = paperChannelInitHolder.getMethod("addListener", Key.class, paperChannelInitListener);
			Method removeListener = paperChannelInitHolder.getMethod("removeListener", Key.class);
			Object listenerImpl = (ChannelInitializeListener) initHandler::accept;
			addListener.invoke(null, eaglerKey, listenerImpl);
			listener.reportPaperMCInjected();
			return () -> {
				try {
					removeListener.invoke(null, eaglerKey);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw Util.propagateReflectThrowable(e);
				}
			};
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private static Runnable injectChannelInitializerOld(Server server, Consumer<Channel> initHandler, IEaglerXServerListener listener) {
		try {
			Object dedicatedPlayerList = server.getClass().getMethod("getHandle").invoke(server);
			Object minecraftServer = dedicatedPlayerList.getClass().getMethod("getServer").invoke(dedicatedPlayerList);
			Method getServerConnection = minecraftServer.getClass().getMethod("getServerConnection");
			Object serverConnection = getServerConnection.invoke(minecraftServer);
			Class<?> serverConnectionClass;
			if(serverConnection == null) {
				serverConnectionClass = getServerConnection.getReturnType();
				for(Method meth : minecraftServer.getClass().getMethods()) {
					if(meth.getReturnType() == serverConnectionClass && !meth.equals(getServerConnection)) {
						serverConnection = meth.invoke(minecraftServer);
						if(serverConnection != null) {
							break;
						}
					}
				}
				if(serverConnection == null) {
					throw new RuntimeException("Could not get ServerConnection instance from server! (Try Paper)");
				}
			}
			serverConnectionClass = serverConnection.getClass();
			Field channelFuturesList = null;
			for(Field f : serverConnectionClass.getDeclaredFields()) {
				if(List.class.isAssignableFrom(f.getType())) {
					Type t = f.getGenericType();
					if(t instanceof ParameterizedType) {
						Type[] params = ((ParameterizedType) t).getActualTypeArguments();
						if(params.length == 1 && ChannelFuture.class.isAssignableFrom(params[0].getClass())) {
							channelFuturesList = f;
							channelFuturesList.setAccessible(true);
							break;
						}
					}
				}
			}
			if(channelFuturesList == null) {
				throw new RuntimeException("Could not get ServerConnection channel futures list! (Try Paper)");
			}
			CleanupList cleanupList = new CleanupList();
			final List<ChannelFuture> oldList = (List<ChannelFuture>) channelFuturesList.get(serverConnection);
			for(ChannelFuture ch : oldList) {
				injectChannelInitializer(ch, listener, initHandler, cleanupList);
			}
			List<ChannelFuture> hackList = new ForwardingList<ChannelFuture>() {
				@Override
				protected List<ChannelFuture> delegate() {
					return oldList;
				}
				@Override
				public boolean add(ChannelFuture element) {
					super.add(element);
					injectChannelInitializer(element, listener, initHandler, cleanupList);
					return true;
				}
			};
			channelFuturesList.set(serverConnection, hackList);
			return cleanupList;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private static void injectChannelInitializer(ChannelFuture channel, IEaglerXServerListener listenerConf,
			Consumer<Channel> initHandler, CleanupList cleanupCallback) {
		channel.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture var1) throws Exception {
				if(var1.isSuccess() && cleanupCallback.cleanup != null) {
					injectChannelInitializer(var1.channel(), listenerConf, initHandler, cleanupCallback);
				}
			}
		});
	}

	// Inspired by ViaVersion
	private static void injectChannelInitializer(Channel channel, IEaglerXServerListener listenerConf,
			Consumer<Channel> initHandler, Consumer<ChannelInitializerHijacker> cleanupCallback) {
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
					throw new RuntimeException("Could not find ChannelBootstrapAccelerator to inject into!");
				}
			}
			return;
		}
		injectInto(foundHandler, foundField, initHandler, cleanupCallback);
		listenerConf.reportNettyInjected(channel);
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
		return handler != null && ChannelInitializer.class.isAssignableFrom(handler.getClass());
	}

	public static CommandMap getCommandMap(Server server) {
		try {
			Field f = server.getClass().getDeclaredField("commandMap");
			f.setAccessible(true);
			return (CommandMap) f.get(server);
		}catch(IllegalAccessException | NoSuchFieldException | SecurityException ex) {
			try {
				Method m = server.getClass().getDeclaredMethod("getCommandMap");
				m.setAccessible(true);
				return (CommandMap) m.invoke(server);
			} catch(IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException
					| NoSuchMethodException ex1) {
				throw Util.propagateReflectThrowable(ex1);
			}
		}
	}

	private static volatile Class<?> class_PacketCompressor_maybe = null;
	private static Field field_PacketCompressor_deflater = null;

	private static synchronized void bindPacketCompressor(Class<?> packetCompressor) {
		if(class_PacketCompressor_maybe != null) {
			return;
		}
		try {
			field_PacketCompressor_deflater = findField(packetCompressor, Deflater.class);
			field_PacketCompressor_deflater.setAccessible(true);
			class_PacketCompressor_maybe = packetCompressor;
		} catch (NoSuchFieldException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static void disposeCompressionHandler(ChannelHandler remove) {
		if(remove != null) {
			if(class_PacketCompressor_maybe == null) {
				bindPacketCompressor(remove.getClass());
			}
			Deflater deflater;
			try {
				deflater = (Deflater) field_PacketCompressor_deflater.get(remove);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw Util.propagateReflectThrowable(e);
			}
			if(deflater != null) {
				deflater.end();
			}
		}
	}

	private static Class<?> class_PacketDecompressor_maybe = null;
	private static Field field_PacketDecompressor_inflater = null;

	private static synchronized void bindPacketDecompressor(Class<?> packetDecompressor) {
		if(class_PacketDecompressor_maybe != null) {
			return;
		}
		try {
			field_PacketDecompressor_inflater = findField(packetDecompressor, Inflater.class);
			field_PacketDecompressor_inflater.setAccessible(true);
			class_PacketDecompressor_maybe = packetDecompressor;
		} catch (NoSuchFieldException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static void disposeDecompressionHandler(ChannelHandler remove) {
		if(remove != null) {
			if(class_PacketDecompressor_maybe == null) {
				bindPacketDecompressor(remove.getClass());
			}
			Inflater inflater;
			try {
				inflater = (Inflater) field_PacketDecompressor_inflater.get(remove);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw Util.propagateReflectThrowable(e);
			}
			if(inflater != null) {
				inflater.end();
			}
		}
	}

	private static Field findField(Class<?> clazz, Class<?> fieldType) throws NoSuchFieldException {
		for(Field field : clazz.getDeclaredFields()) {
			if(field.getType() == fieldType) {
				field.setAccessible(true);
				return field;
			}
		}
		throw new NoSuchFieldException("Could not find field with type " + fieldType + " in class " + clazz);
	}

}
