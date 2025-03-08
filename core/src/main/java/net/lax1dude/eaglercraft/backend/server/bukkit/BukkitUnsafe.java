package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Multimap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
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

		public Channel getChannel() {
			try {
				return (Channel) field_NetworkManager_channel.get(networkManager);
			} catch (IllegalArgumentException | IllegalAccessException e) {
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
	private static Method method_EntityPlayer_getProfile = null;
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
			method_EntityPlayer_getProfile = clz2.getMethod("getProfile");
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

	private static final Method method_Player_getPlayerProfile;
	private static final Class<?> class_PlayerProfile;
	private static final Method method_PlayerProfile_getProperties;
	private static final Class<?> class_ProfileProperty;
	private static final Method method_ProfileProperty_getName;
	private static final Method method_ProfileProperty_getValue;
	private static final boolean paperProfileAPISupport;

	private static final Class<?> class_GameProfile;
	private static final Method method_GameProfile_getId;
	private static final Method method_GameProfile_getName;
	private static final Method method_GameProfile_getProperties;
	private static final Class<?> class_Property;
	private static final Constructor<?> constructor_Property;
	private static final Method method_Property_getValue;

	static {
		Method method_Player_getPlayerProfile_ = null;
		Class<?> class_PlayerProfile_ = null;
		Method method_PlayerProfile_getProperties_ = null;
		Class<?> class_ProfileProperty_ = null;
		Method method_ProfileProperty_getName_ = null;
		Method method_ProfileProperty_getValue_ = null;
		boolean paperProfileAPISupport_ = false;
		try {
			class_PlayerProfile_ = Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
			method_Player_getPlayerProfile_ = Player.class.getMethod("getPlayerProfile");
			class_ProfileProperty_ = Class.forName("com.destroystokyo.paper.profile.ProfileProperty");
			method_PlayerProfile_getProperties_ = class_PlayerProfile_.getMethod("getProperties");
			method_ProfileProperty_getName_ = class_ProfileProperty_.getMethod("getName");
			method_ProfileProperty_getValue_ = class_ProfileProperty_.getMethod("getValue");
			paperProfileAPISupport_ = true;
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// Paper profile API is unsupported
		}
		method_Player_getPlayerProfile = method_Player_getPlayerProfile_;
		class_PlayerProfile = class_PlayerProfile_;
		method_PlayerProfile_getProperties = method_PlayerProfile_getProperties_;
		class_ProfileProperty = class_ProfileProperty_;
		method_ProfileProperty_getName = method_ProfileProperty_getName_;
		method_ProfileProperty_getValue = method_ProfileProperty_getValue_;
		paperProfileAPISupport = paperProfileAPISupport_;
		try {
			class_GameProfile = Class.forName("com.mojang.authlib.GameProfile");
			method_GameProfile_getId = class_GameProfile.getMethod("getId");
			method_GameProfile_getName = class_GameProfile.getMethod("getName");
			method_GameProfile_getProperties = class_GameProfile.getMethod("getProperties");
			class_Property = Class.forName("com.mojang.authlib.properties.Property");
			constructor_Property = class_Property.getConstructor(String.class, String.class, String.class);
			method_Property_getValue = class_Property.getMethod("getValue");
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static String getTexturesProperty(Player player) {
		if(paperProfileAPISupport) {
			return getTexturesPropertyPaper(player);
		}else {
			if(class_CraftPlayer == null) {
				bindCraftPlayer(player);
			}
			try {
				Multimap<String, Object> props = (Multimap<String, Object>) method_GameProfile_getProperties
						.invoke(method_EntityPlayer_getProfile.invoke(method_CraftPlayer_getHandle.invoke(player)));
				Collection<Object> tex = props.get("textures");
				if(!tex.isEmpty()) {
					return (String) method_Property_getValue.invoke(tex.iterator().next());
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw Util.propagateReflectThrowable(e);
			}
			return null;
		}
	}

	private static String getTexturesPropertyPaper(Player player) {
		try {
			Object profile = method_Player_getPlayerProfile.invoke(player);
			if(profile != null) {
				for(Object o : (List<Object>) method_PlayerProfile_getProperties.invoke(profile)) {
					if("textures".equals(method_ProfileProperty_getName.invoke(o))) {
						return (String) method_ProfileProperty_getValue.invoke(o);
					}
				}
			}
			return null;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static interface PropertyInjector {

		void injectTexturesProperty(String texturesPropertyValue, String texturesPropertySignature);

		void injectIsEaglerPlayerProperty();

		void complete();

	}

	private static class PaperPropertyInjector implements PropertyInjector {

		@Override
		public void injectTexturesProperty(String texturesPropertyValue, String texturesPropertySignature) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void injectIsEaglerPlayerProperty() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void complete() {
			// TODO Auto-generated method stub
			
		}

	}

	private static class BukkitPropertyInjector implements PropertyInjector {

		private final Multimap<String, Object> props;

		protected BukkitPropertyInjector(Multimap<String, Object> props) {
			this.props = props;
		}

		@Override
		public void injectTexturesProperty(String texturesPropertyValue, String texturesPropertySignature) {
			props.removeAll("textures");
			try {
				props.put("textures", constructor_Property.newInstance("textures", texturesPropertyValue, texturesPropertySignature));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}

		@Override
		public void injectIsEaglerPlayerProperty() {
			props.removeAll("isEaglerPlayer");
			try {
				props.put("isEaglerPlayer", constructor_Property.newInstance("isEaglerPlayer", "true", null));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}

		@Override
		public void complete() {
		}

	}

	public static BukkitUnsafe.PropertyInjector propertyInjector(Player player) {
		if(paperProfileAPISupport) {
			return null; //TODO: use the actual paper api
		}else {
			if(class_CraftPlayer == null) {
				bindCraftPlayer(player);
			}
			try {
				return new BukkitPropertyInjector((Multimap<String, Object>) method_GameProfile_getProperties
						.invoke(method_EntityPlayer_getProfile.invoke(method_CraftPlayer_getHandle.invoke(player))));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
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

	public static boolean isEnableNativeTransport(Server server) {
		try {
			Object dedicatedPlayerList = server.getClass().getMethod("getHandle").invoke(server);
			Object dedicatedServer = dedicatedPlayerList.getClass().getMethod("getServer").invoke(dedicatedPlayerList);
			Object propertyManager = dedicatedServer.getClass().getMethod("getPropertyManager").invoke(dedicatedServer);
			return (Boolean) propertyManager.getClass().getMethod("getBoolean", String.class, boolean.class)
					.invoke(propertyManager, "use-native-transport", true);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static EventLoopGroup getEventLoopGroup(Server server, boolean enableNativeTransport) {
		Class<?> serverConnection;
		try {
			Object dedicatedPlayerList = server.getClass().getMethod("getHandle").invoke(server);
			Object minecraftServer = dedicatedPlayerList.getClass().getMethod("getServer").invoke(dedicatedPlayerList);
			serverConnection = minecraftServer.getClass().getMethod("getServerConnection").getReturnType();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw Util.propagateReflectThrowable(e);
		}
		return getEventLoopGroup(serverConnection, enableNativeTransport);
	}

	public static EventLoopGroup getEventLoopGroup(Class<?> serverConnection, boolean enableNativeTransport) {
		Field[] fields = serverConnection.getFields();
		if(enableNativeTransport) {
			for(Field field : fields) {
				Class<?> clz = field.getClass();
				if(clz.getSimpleName().equals("LazyInitVar")) {
					Type type = field.getGenericType();
					if(type instanceof ParameterizedType) {
						Type[] args = ((ParameterizedType)type).getActualTypeArguments();
						if(args.length == 1 && "io.netty.channel.epoll.EpollEventLoopGroup".equals(args[0].getTypeName())) {
							try {
								return (EventLoopGroup) clz.getMethod("init").invoke(field.get(null));
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
									| NoSuchMethodException | SecurityException e) {
								throw Util.propagateReflectThrowable(e);
							}
						}
					}
				}
			}
		}
		for(Field field : fields) {
			Class<?> clz = field.getClass();
			if(clz.getSimpleName().equals("LazyInitVar")) {
				Type type = field.getGenericType();
				if(type instanceof ParameterizedType) {
					Type[] args = ((ParameterizedType)type).getActualTypeArguments();
					if(args.length == 1 && "io.netty.channel.nio.NioEventLoopGroup".equals(args[0].getTypeName())) {
						try {
							return (EventLoopGroup) clz.getMethod("init").invoke(field.get(null));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException e) {
							throw Util.propagateReflectThrowable(e);
						}
					}
				}
			}
		}
		throw new RuntimeException("Could not locate the server event loop!");
	}

}
