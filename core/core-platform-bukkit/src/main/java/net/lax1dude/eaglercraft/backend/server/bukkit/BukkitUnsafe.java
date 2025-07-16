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

package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.papermc.paper.network.ChannelInitializeListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.util.ChannelInitializerHijacker;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class BukkitUnsafe {

	private static final VarHandle CLASS_CRAFTPLAYER_HANDLE;
	private static final VarHandle CLASS_NETWORKMANAGER_HANDLE;

	static {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			CLASS_CRAFTPLAYER_HANDLE = lookup.findStaticVarHandle(BukkitUnsafe.class, "class_CraftPlayer", Class.class);
			CLASS_NETWORKMANAGER_HANDLE = lookup.findStaticVarHandle(BukkitUnsafe.class, "class_NetworkManager", Class.class);
		} catch(ReflectiveOperationException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static volatile Class<?> class_CraftPlayer = null;
	private static Method method_CraftPlayer_getHandle = null;
	private static Method method_CraftPlayer_addChannel = null;
	private static Class<?> class_EntityPlayer = null;
	private static Field field_EntityPlayer_playerConnection = null;
	private static Method method_EntityPlayer_getProfile = null;
	private static Class<?> class_PlayerConnection = null;
	private static Field field_PlayerConnection_networkManager = null;
	private static volatile Class<?> class_NetworkManager = null;
	private static Field field_NetworkManager_channel = null;
	private static Field field_NetworkManager_address = null;

	private static synchronized void bindCraftPlayer(Player playerObject) {
		if (CLASS_CRAFTPLAYER_HANDLE.getAcquire() != null) {
			return;
		}
		Class<?> clz = playerObject.getClass();
		try {
			method_CraftPlayer_getHandle = clz.getMethod("getHandle");
			method_CraftPlayer_addChannel = clz.getMethod("addChannel", String.class);
			Object entityPlayer = method_CraftPlayer_getHandle.invoke(playerObject);
			Class<?> clz2 = entityPlayer.getClass();
			for (Field f : clz2.getFields()) {
				if (f.getType().getSimpleName().equals("PlayerConnection")) {
					field_EntityPlayer_playerConnection = f;
					break;
				}
			}
			if (field_EntityPlayer_playerConnection == null) {
				throw new IllegalStateException("Could not locate player connection field of " + clz2.getName());
			}
			Class<?> clz3 = field_EntityPlayer_playerConnection.getType();
			for (Field f : clz3.getFields()) {
				if (f.getType().getSimpleName().equals("NetworkManager")) {
					field_PlayerConnection_networkManager = f;
					break;
				}
			}
			if (field_PlayerConnection_networkManager == null) {
				throw new IllegalStateException("Could not locate network manager field of " + clz3.getName());
			}
			Class<?> clz4 = field_PlayerConnection_networkManager.getType();
			for (Field f : clz4.getFields()) {
				if (Channel.class.isAssignableFrom(f.getType())) {
					field_NetworkManager_channel = f;
					break;
				}
			}
			for (Field f : clz4.getDeclaredFields()) {
				if (SocketAddress.class.isAssignableFrom(f.getType())) {
					f.setAccessible(true);
					field_NetworkManager_address = f;
					break;
				}
			}
			if (field_NetworkManager_channel == null) {
				throw new IllegalStateException("Could not locate channel field of " + clz4.getName());
			}
			if (field_NetworkManager_address == null) {
				System.err.println("Could not find SocketAddress field in class " + clz4.getName());
				System.err.println("Use Spigot if you want EaglerXServer to forward player IPs");
			}
			method_EntityPlayer_getProfile = clz2.getMethod("getProfile");
			CLASS_NETWORKMANAGER_HANDLE.setRelease(clz4);
			class_PlayerConnection = clz3;
			class_EntityPlayer = clz2;
			CLASS_CRAFTPLAYER_HANDLE.setRelease(clz);
		} catch (Exception ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	public static Channel getPlayerChannel(Player playerObject) {
		if (CLASS_CRAFTPLAYER_HANDLE.getAcquire() == null) {
			bindCraftPlayer(playerObject);
		}
		try {
			return (Channel) field_NetworkManager_channel.get(field_PlayerConnection_networkManager
					.get(field_EntityPlayer_playerConnection.get(method_CraftPlayer_getHandle.invoke(playerObject))));
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static String getTexturesProperty(Player player) {
		if (CLASS_CRAFTPLAYER_HANDLE.getAcquire() == null) {
			bindCraftPlayer(player);
		}
		try {
			Multimap<String, Property> props = ((GameProfile) method_EntityPlayer_getProfile
					.invoke(method_CraftPlayer_getHandle.invoke(player))).getProperties();
			Collection<Property> tex = props.get("textures");
			if (!tex.isEmpty()) {
				return tex.iterator().next().getValue();
			}
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
		return null;
	}

	private static final Property isEaglerPlayerPropertyT = new Property("isEaglerPlayer", "true", null);
	private static final Property isEaglerPlayerPropertyF = new Property("isEaglerPlayer", "false", null);

	public static class PropertyInjector {

		private final Multimap<String, Property> props;

		protected PropertyInjector(Multimap<String, Property> props) {
			this.props = props;
		}

		public void injectTexturesProperty(String texturesPropertyValue, String texturesPropertySignature) {
			props.removeAll("textures");
			props.put("textures", new Property("textures", texturesPropertyValue, texturesPropertySignature));
		}

		public void injectIsEaglerPlayerProperty(boolean val) {
			props.removeAll("isEaglerPlayer");
			props.put("isEaglerPlayer", val ? isEaglerPlayerPropertyT : isEaglerPlayerPropertyF);
		}

		public void complete() {
		}

	}

	public static BukkitUnsafe.PropertyInjector propertyInjector(Player player) {
		if (CLASS_CRAFTPLAYER_HANDLE.getAcquire() == null) {
			bindCraftPlayer(player);
		}
		try {
			return new PropertyInjector(
					((GameProfile) method_EntityPlayer_getProfile.invoke(method_CraftPlayer_getHandle.invoke(player)))
							.getProperties());
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static Object getHandle(Player player) {
		if (CLASS_CRAFTPLAYER_HANDLE.getAcquire() == null) {
			bindCraftPlayer(player);
		}
		try {
			return method_CraftPlayer_getHandle.invoke(player);
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static GameProfile getGameProfile(Object entityPlayer) {
		try {
			return (GameProfile) method_EntityPlayer_getProfile.invoke(entityPlayer);
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private static synchronized void bindRealAddress(Object networkManager) {
		if (CLASS_NETWORKMANAGER_HANDLE.getAcquire() != null) {
			return;
		}
		Class<?> clz = networkManager.getClass();
		for (Field field : clz.getDeclaredFields()) {
			if (SocketAddress.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				field_NetworkManager_address = field;
				CLASS_NETWORKMANAGER_HANDLE.setRelease(clz);
				return;
			}
		}
		CLASS_NETWORKMANAGER_HANDLE.setRelease(clz);
		System.err.println("Could not find SocketAddress field in class " + clz.getName());
		System.err.println("Use Spigot if you want EaglerXServer to forward player IPs");
	}

	public static void updateRealAddress(Object networkManager, SocketAddress address) {
		Class<?> clz;
		if ((clz = (Class<?>) CLASS_NETWORKMANAGER_HANDLE.getAcquire()) == null) {
			bindRealAddress(networkManager);
			clz = (Class<?>) CLASS_NETWORKMANAGER_HANDLE.getAcquire();
		}
		if (field_NetworkManager_address != null && clz.isAssignableFrom(networkManager.getClass())) {
			try {
				field_NetworkManager_address.set(networkManager, address);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}
	}

	public static void addPlayerChannel(Player player, String ch) {
		if (CLASS_CRAFTPLAYER_HANDLE.getAcquire() == null) {
			bindCraftPlayer(player);
		}
		try {
			method_CraftPlayer_addChannel.invoke(player, ch);
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private static class CleanupList implements Consumer<ChannelInitializerHijacker>, Runnable {

		protected List<ChannelInitializerHijacker> cleanup = new ArrayList<>();

		@Override
		public void accept(ChannelInitializerHijacker c) {
			synchronized (this) {
				if (cleanup != null) {
					cleanup.add(c);
					return;
				}
			}
			c.deactivate();
		}

		@Override
		public void run() {
			List<ChannelInitializerHijacker> cc;
			synchronized (this) {
				cc = new ArrayList<>(cleanup);
				cleanup = null;
			}
			for (ChannelInitializerHijacker c : cc) {
				c.deactivate();
			}
		}

	}

	public static Runnable injectChannelInitializer(Server server, Consumer<Channel> initHandler,
			IEaglerXServerListener listener) {
		Class<?> keyClz;
		Object eaglerKey;
		Class<?> paperChannelInitHolder;
		Class<?> paperChannelInitListener;
		try {
			keyClz = Class.forName("net.kyori.adventure.key.Key");
			eaglerKey = keyClz.getMethod("key", String.class, String.class).invoke(null, "eaglerxserver",
					"channel_initializer");
			paperChannelInitHolder = Class.forName("io.papermc.paper.network.ChannelInitializeListenerHolder");
			paperChannelInitListener = Class.forName("io.papermc.paper.network.ChannelInitializeListener");
		} catch (ReflectiveOperationException ex) {
			return injectChannelInitializerOld(server, initHandler, listener);
		}
		return injectChannelInitializerPaper(paperChannelInitHolder, paperChannelInitListener, keyClz, eaglerKey,
				initHandler, listener);
	}

	private static Runnable injectChannelInitializerPaper(Class<?> paperChannelInitHolder,
			Class<?> paperChannelInitListener, Class<?> keyClz, Object eaglerKey, Consumer<Channel> initHandler,
			IEaglerXServerListener listener) {
		try {
			Method addListener = paperChannelInitHolder.getMethod("addListener", keyClz, paperChannelInitListener);
			Method removeListener = paperChannelInitHolder.getMethod("removeListener", keyClz);
			Object listenerImpl = (ChannelInitializeListener) initHandler::accept;
			addListener.invoke(null, eaglerKey, listenerImpl);
			listener.reportPaperMCInjected();
			return () -> {
				try {
					removeListener.invoke(null, eaglerKey);
				} catch (ReflectiveOperationException e) {
					throw Util.propagateReflectThrowable(e);
				}
			};
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private static Runnable injectChannelInitializerOld(Server server, Consumer<Channel> initHandler,
			IEaglerXServerListener listener) {
		try {
			Object dedicatedPlayerList = server.getClass().getMethod("getHandle").invoke(server);
			Object minecraftServer = dedicatedPlayerList.getClass().getMethod("getServer").invoke(dedicatedPlayerList);
			Method getServerConnection = minecraftServer.getClass().getMethod("getServerConnection");
			Object serverConnection = getServerConnection.invoke(minecraftServer);
			Class<?> serverConnectionClass;
			if (serverConnection == null) {
				serverConnectionClass = getServerConnection.getReturnType();
				for (Method meth : minecraftServer.getClass().getMethods()) {
					if (meth.getReturnType() == serverConnectionClass && !meth.equals(getServerConnection)) {
						serverConnection = meth.invoke(minecraftServer);
						if (serverConnection != null) {
							break;
						}
					}
				}
				if (serverConnection == null) {
					throw new RuntimeException("Could not get ServerConnection instance from server! (Try Paper)");
				}
			}
			serverConnectionClass = serverConnection.getClass();
			Field channelFuturesList = null;
			for (Field f : serverConnectionClass.getDeclaredFields()) {
				if (List.class.isAssignableFrom(f.getType())) {
					Type t = f.getGenericType();
					if (t instanceof ParameterizedType tt) {
						Type[] params = tt.getActualTypeArguments();
						if (params.length == 1 && "io.netty.channel.ChannelFuture".equals(params[0].getTypeName())) {
							channelFuturesList = f;
							channelFuturesList.setAccessible(true);
							break;
						}
					}
				}
			}
			if (channelFuturesList == null) {
				throw new RuntimeException("Could not get ServerConnection channel futures list! (Try Paper)");
			}
			CleanupList cleanupList = new CleanupList();
			final List<ChannelFuture> oldList = (List<ChannelFuture>) channelFuturesList.get(serverConnection);
			for (ChannelFuture ch : oldList) {
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
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private static void injectChannelInitializer(ChannelFuture channel, IEaglerXServerListener listenerConf,
			Consumer<Channel> initHandler, CleanupList cleanupCallback) {
		channel.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture var1) throws Exception {
				if (var1.isSuccess() && cleanupCallback.cleanup != null) {
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
			for (String name : names) {
				ChannelHandler handler = channel.pipeline().get(name);
				if (isServerInitializer(handler)) {
					try {
						foundField = handler.getClass().getDeclaredField("childHandler");
						foundField.setAccessible(true);
						foundHandler = handler;
						break eagler;
					} catch (IllegalArgumentException | ReflectiveOperationException ex) {
					}
				}
			}
			foundHandler = channel.pipeline().first();
			if (isServerInitializer(foundHandler)) {
				try {
					foundField = foundHandler.getClass().getDeclaredField("childHandler");
					foundField.setAccessible(true);
					break eagler;
				} catch (ReflectiveOperationException ex) {
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
				if (this != newInitializer) {
					System.err.println("Detected another plugin's channel initializer ("
							+ newInitializer.getClass().getName() + ") injected into the pipeline, "
							+ "reinjecting EaglerXServer again to make sure its first, because we "
							+ "really are that rude");
					injectInto(foundHandler, foundField, init, cleanupCallback);
					return true;
				} else {
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
		return handler != null && ChannelInboundHandlerAdapter.class.isAssignableFrom(handler.getClass());
	}

	public static CommandMap getCommandMap(Server server) {
		try {
			Field f = server.getClass().getDeclaredField("commandMap");
			f.setAccessible(true);
			return (CommandMap) f.get(server);
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
			try {
				Method m = server.getClass().getDeclaredMethod("getCommandMap");
				m.setAccessible(true);
				return (CommandMap) m.invoke(server);
			} catch (ReflectiveOperationException ex1) {
				throw Util.propagateReflectThrowable(ex1);
			}
		}
	}

	private static Field findField(Class<?> clazz, Class<?> fieldType) throws NoSuchFieldException {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getType() == fieldType) {
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
		} catch (ReflectiveOperationException e) {
			try {
				Object dedicatedPlayerList = server.getClass().getMethod("getHandle").invoke(server);
				Object dedicatedServer = dedicatedPlayerList.getClass().getMethod("getServer").invoke(dedicatedPlayerList);
				Object propertyManager = dedicatedServer.getClass().getMethod("getDedicatedServerProperties").invoke(dedicatedServer);
				Method getBoolean = propertyManager.getClass().getSuperclass().getDeclaredMethod("getBoolean", String.class, boolean.class);
				getBoolean.setAccessible(true);
				return (Boolean) getBoolean.invoke(propertyManager, "use-native-transport", true);
			} catch (Exception e1) {
				throw Util.propagateReflectThrowable(e1);
			}
		}
	}

	public static EventLoopGroup getEventLoopGroup(Server server, boolean enableNativeTransport) {
		Class<?> serverConnection;
		try {
			Object dedicatedPlayerList = server.getClass().getMethod("getHandle").invoke(server);
			Object minecraftServer = dedicatedPlayerList.getClass().getMethod("getServer").invoke(dedicatedPlayerList);
			serverConnection = minecraftServer.getClass().getMethod("getServerConnection").getReturnType();
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
		return getEventLoopGroup(serverConnection, enableNativeTransport);
	}

	public static EventLoopGroup getEventLoopGroup(Class<?> serverConnection, boolean enableNativeTransport) {
		Field[] fields = serverConnection.getFields();
		if (enableNativeTransport) {
			for (Field field : fields) {
				Class<?> clz = field.getType();
				if (clz.getSimpleName().equals("LazyInitVar")) {
					Type type = field.getGenericType();
					if (type instanceof ParameterizedType tt) {
						Type[] args = tt.getActualTypeArguments();
						if (args.length == 1
								&& "io.netty.channel.epoll.EpollEventLoopGroup".equals(args[0].getTypeName())) {
							for (Method m : clz.getMethods()) {
								if (m.getGenericReturnType() != m.getReturnType()) {
									try {
										return (EventLoopGroup) m.invoke(field.get(null));
									} catch (ReflectiveOperationException e) {
										throw Util.propagateReflectThrowable(e);
									}
								}
							}
						}
					}
				}
			}
		}
		for (Field field : fields) {
			Class<?> clz = field.getType();
			if (clz.getSimpleName().equals("LazyInitVar")) {
				Type type = field.getGenericType();
				if (type instanceof ParameterizedType tt) {
					Type[] args = tt.getActualTypeArguments();
					if (args.length == 1 && "io.netty.channel.nio.NioEventLoopGroup".equals(args[0].getTypeName())) {
						for (Method m : clz.getMethods()) {
							if (m.getGenericReturnType() != m.getReturnType()) {
								try {
									return (EventLoopGroup) m.invoke(field.get(null));
								} catch (ReflectiveOperationException e) {
									throw Util.propagateReflectThrowable(e);
								}
							}
						}
					}
				}
			}
		}
		throw new RuntimeException("Could not locate the server event loop!");
	}

}
