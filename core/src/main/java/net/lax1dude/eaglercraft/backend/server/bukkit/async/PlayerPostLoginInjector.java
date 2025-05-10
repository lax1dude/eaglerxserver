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

package net.lax1dude.eaglercraft.backend.server.bukkit.async;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.PlayerLoginPostEvent;
import net.lax1dude.eaglercraft.backend.server.bukkit.BukkitUnsafe;
import net.lax1dude.eaglercraft.backend.server.bukkit.PlatformPluginBukkit;
import net.lax1dude.eaglercraft.backend.server.util.ClassProxy;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerPostLoginInjector {

	public static final AttributeKey<LoginEventContext> attr = AttributeKey.valueOf("eagler-postlogin-hack");

	protected final PlatformPluginBukkit plugin;

	protected Class<Object> netManagerClass;
	protected Constructor<Object> netManagerCtor;
	protected ClassProxy<Object> netManagerProxy;
	protected Field netManagerDir;
	protected Field netManagerChannel;
	protected Method setHandlerMethod;
	protected Method sendPacketMethod1;
	protected Method sendPacketMethod2;
	protected Method sendPacketMethod3;
	protected Method getHandlerMethod;
	protected Class<Object> handshakeListenerClass;
	protected Field handshakeListenerNetManager;
	protected Field handlerAdded;

	protected Class<Object> loginListenerClass;
	protected Constructor<Object> loginListenerCtor;
	protected ClassProxy<Object> loginListenerProxy;
	protected Field loginListenerServer;
	protected Field loginListenerNetManager;
	protected Class<Object> enumProtocolState;
	protected Object protocolStateOnResume;
	protected Field loginListenerState;
	protected Method loginListenerTick;
	protected Method loginListenerDisconnect;
	protected Field loginListenerPlayer;

	protected Class<Object> packetLoginSuccessClass;
	protected Field packetLoginSuccessGameProfile;

	protected Class<?> packetPlayDisconnect;
	protected Constructor<?> packetPlayDisconnectCtor;
	protected Field packetLoginDisconnectMsg;

	protected final ConcurrentMap<Property, Player> entityPlayers;

	public PlayerPostLoginInjector(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
		this.entityPlayers = (new MapMaker()).concurrencyLevel(8).weakKeys().weakValues().makeMap();
	}

	private synchronized void bind(Object netManager) {
		if (this.netManagerClass != null) {
			return;
		}
		try {
			Class<Object> netManagerClass = (Class<Object>) netManager.getClass();
			Class<Object> protocolDirType = null;
			Field protocolDirField = null;
			Field channelField = null;
			for (Field f : netManagerClass.getDeclaredFields()) {
				Class<?> clz = f.getType();
				if (clz.getSimpleName().equals("EnumProtocolDirection")) {
					f.setAccessible(true);
					protocolDirType = (Class<Object>) f.getType();
					protocolDirField = f;
					break;
				}
			}
			for (Field f : netManagerClass.getFields()) {
				Class<?> clz = f.getType();
				if (Channel.class.isAssignableFrom(clz)) {
					f.setAccessible(true);
					channelField = f;
					break;
				}
			}
			if (protocolDirField == null) {
				throw new IllegalStateException("Could not locate direction field of " + netManagerClass.getName());
			}
			if (channelField == null) {
				throw new IllegalStateException("Could not locate channel field of " + netManagerClass.getName());
			}
			Method setHandlerMethod = null;
			Method sendPacketMethod1 = null;
			Method sendPacketMethod2 = null;
			Method sendPacketMethod3 = null;
			Method getHandlerMethod = null;
			Class<?> futureListenerArr = Array.newInstance(GenericFutureListener.class, 0).getClass();
			for (Method m : netManagerClass.getMethods()) {
				Class<?>[] params = m.getParameterTypes();
				if (setHandlerMethod == null && params.length == 1
						&& params[0].getSimpleName().equals("PacketListener")) {
					setHandlerMethod = m;
				} else if (sendPacketMethod1 == null && params.length == 1
						&& params[0].getSimpleName().equals("Packet")) {
					sendPacketMethod1 = m;
				} else if (sendPacketMethod3 == null && params.length == 3 && params[0].getSimpleName().equals("Packet")
						&& params[1].equals(GenericFutureListener.class) && params[2].equals(futureListenerArr)) {
					sendPacketMethod3 = m;
					sendPacketMethod2 = null;
				} else if (sendPacketMethod3 == null && sendPacketMethod2 == null && params.length == 2
						&& params[0].getSimpleName().equals("Packet")
						&& params[1].equals(GenericFutureListener.class)) {
					sendPacketMethod2 = m;
				} else if (getHandlerMethod == null && params.length == 0
						&& m.getReturnType().getSimpleName().equals("PacketListener")) {
					getHandlerMethod = m;
				}
				if (setHandlerMethod != null && sendPacketMethod1 != null && sendPacketMethod3 != null
						&& getHandlerMethod != null) {
					break;
				}
			}
			if (setHandlerMethod == null) {
				throw new IllegalStateException(
						"Could not locate set handler function of " + netManagerClass.getName());
			}
			if (sendPacketMethod1 == null) {
				throw new IllegalStateException(
						"Could not locate send packet (1 param) function of " + netManagerClass.getName());
			}
			if (sendPacketMethod2 == null && sendPacketMethod3 == null) {
				throw new IllegalStateException(
						"Could not locate send packet (2 or 3 param) function of " + netManagerClass.getName());
			}
			if (getHandlerMethod == null) {
				throw new IllegalStateException(
						"Could not locate get handler function of " + netManagerClass.getName());
			}
			Object handshakeListener = getHandlerMethod.invoke(netManager);
			Class<Object> handshakeListenerClass = (Class<Object>) handshakeListener.getClass();
			Field handshakeListenerNetManager = null;
			for (Field f : handshakeListenerClass.getDeclaredFields()) {
				if (f.getType() == netManagerClass) {
					f.setAccessible(true);
					handshakeListenerNetManager = f;
				}
			}
			if (handshakeListenerNetManager == null) {
				throw new IllegalStateException(
						"Could not locate network manager field of " + handshakeListenerClass.getName());
			}
			this.netManagerCtor = netManagerClass.getDeclaredConstructor(protocolDirType);
			this.netManagerCtor.setAccessible(true);
			this.netManagerClass = netManagerClass;
			this.netManagerProxy = ClassProxy.bindProxy(PlayerPostLoginInjector.class.getClassLoader(),
					netManagerClass);
			this.netManagerDir = protocolDirField;
			this.netManagerChannel = channelField;
			this.setHandlerMethod = setHandlerMethod;
			this.sendPacketMethod1 = sendPacketMethod1;
			this.sendPacketMethod2 = sendPacketMethod2;
			this.sendPacketMethod3 = sendPacketMethod3;
			this.getHandlerMethod = getHandlerMethod;
			this.handshakeListenerClass = handshakeListenerClass;
			this.handshakeListenerNetManager = handshakeListenerNetManager;
			this.handlerAdded = ChannelHandlerAdapter.class.getDeclaredField("added");
			this.handlerAdded.setAccessible(true);
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static class LoginEventContext {

		protected final Object originalNetworkManager;
		protected final Channel channel;
		protected Object proxiedNetworkManager;
		protected boolean compressionDisable;
		protected boolean throwOnLoginSuccess;
		protected volatile boolean clientPlayState;

		protected LoginEventContext(Object originalNetworkManager, Channel channel) {
			this.originalNetworkManager = originalNetworkManager;
			this.channel = channel;
		}

		public Object originalNetworkManager() {
			return originalNetworkManager;
		}

		public void markCompressionDisable(boolean en) {
			this.compressionDisable = en;
		}

		public void markThrowOnLoginSuccess(boolean en) {
			this.throwOnLoginSuccess = en;
		}

		public void markClientPlayState(boolean en) {
			this.clientPlayState = en;
		}

	}

	public static class EaglerError extends Error {

		protected final GameProfile gameProfile;

		public EaglerError(GameProfile gameProfile) {
			this.gameProfile = gameProfile;
		}

	}

	public Object wrapNetworkManager(Object netManager, Channel channel) {
		if (netManagerClass == null) {
			bind(netManager);
		}
		if (!netManagerClass.isAssignableFrom(netManager.getClass())) {
			throw new IllegalStateException("Unknown NetworkManager type: " + netManager.getClass().getName());
		}
		try {
			final LoginEventContext ctx = new LoginEventContext(netManager, channel);
			Object ret = netManagerProxy.createProxy(netManagerCtor, new Object[] { netManagerDir.get(netManager) },
					(obj, meth, args) -> {
						if (setHandlerMethod.equals(meth)) {
							if (args[0].getClass().getSimpleName().equals("LoginListener")) {
								meth.invoke(netManager, args);
								fireEventLoginInit(channel);
								args[0] = wrapLoginListener(getHandlerMethod.invoke(netManager), ctx);
								meth.invoke(netManager, args);
								return null;
							}
						} else if (sendPacketMethod1.equals(meth)) {
							String nm = args[0].getClass().getSimpleName();
							if (nm.equals("PacketLoginOutDisconnect") && ctx.clientPlayState) {
								if (packetPlayDisconnect == null) {
									bindPacketPlayDisconnect(args[0].getClass());
								}
								if (packetPlayDisconnect != void.class) {
									args[0] = packetPlayDisconnectCtor.newInstance(packetLoginDisconnectMsg.get(args[0]));
								} else {
									return null;
								}
							}
							meth.invoke(netManager, args);
							if (ctx.throwOnLoginSuccess && nm.equals("PacketLoginOutSuccess")) {
								throw new EaglerError(getPacketProfile(args[0]));
							}
							return null;
						} else if (ctx.compressionDisable && (sendPacketMethod3 != null ? sendPacketMethod3.equals(meth)
								: sendPacketMethod2.equals(meth))) {
							if (args[0].getClass().getSimpleName().equals("PacketLoginOutSetCompression")) {
								return null;
							}
						}
						return meth.invoke(netManager, args);
					});
			ctx.proxiedNetworkManager = ret;
			channel.attr(attr).set(ctx);
			handshakeListenerNetManager.set(getHandlerMethod.invoke(netManager), ret);
			netManagerChannel.set(ret, channel);
			return ret;
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private synchronized void bindPacketProfile(Object packet) {
		if (packetLoginSuccessClass != null) {
			return;
		}
		Field gameProfile = null;
		Class<Object> clz = (Class<Object>) packet.getClass();
		for (Field f : clz.getDeclaredFields()) {
			if (f.getType().equals(GameProfile.class)) {
				f.setAccessible(true);
				gameProfile = f;
				break;
			}
		}
		if (gameProfile == null) {
			throw new IllegalStateException("Could not locate game profile field of " + clz.getName());
		}
		packetLoginSuccessClass = clz;
		packetLoginSuccessGameProfile = gameProfile;
	}

	private GameProfile getPacketProfile(Object packet) {
		if (packetLoginSuccessClass == null) {
			bindPacketProfile(packet);
		}
		if (!packetLoginSuccessClass.isAssignableFrom(packet.getClass())) {
			throw new IllegalStateException("Unknown PacketLoginOutSuccess type: " + packet.getClass().getName());
		}
		try {
			return (GameProfile) packetLoginSuccessGameProfile.get(packet);
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private synchronized void bindLogin(Object loginListener) {
		if (this.loginListenerClass != null) {
			return;
		}
		if (netManagerClass == null) {
			throw new IllegalStateException();
		}
		try {
			Class<Object> loginListenerClass = (Class<Object>) loginListener.getClass();
			Class<Object> mcServerClass = null;
			Constructor<Object> loginListenerCtor = null;
			for (Constructor<? extends Object> ctor : loginListenerClass.getConstructors()) {
				Class<?>[] params = ctor.getParameterTypes();
				if (params.length == 2 && params[1] == netManagerClass) {
					loginListenerCtor = (Constructor<Object>) ctor;
					mcServerClass = (Class<Object>) params[0];
					break;
				}
			}
			if (loginListenerCtor == null) {
				throw new IllegalStateException("Could not locate constructor of " + loginListenerClass.getName());
			}
			Field loginListenerServer = null;
			Field loginListenerNetManager = null;
			Class<Object> enumProtocolState = null;
			Field loginListenerState = null;
			Field loginListenerPlayer = null;
			for (Field f : loginListenerClass.getDeclaredFields()) {
				if (f.getType() == mcServerClass) {
					f.setAccessible(true);
					loginListenerServer = f;
				} else if (f.getType() == netManagerClass) {
					f.setAccessible(true);
					loginListenerNetManager = f;
				} else if (f.getType().getName().equals(loginListenerClass.getName() + "$EnumProtocolState")) {
					f.setAccessible(true);
					loginListenerState = f;
					enumProtocolState = (Class<Object>) f.getType();
				} else if (f.getType().getSimpleName().equals("EntityPlayer")) {
					f.setAccessible(true);
					loginListenerPlayer = f;
				}
				if (loginListenerServer != null && loginListenerNetManager != null && loginListenerState != null
						&& loginListenerPlayer != null) {
					break;
				}
			}
			if (loginListenerServer == null) {
				throw new IllegalStateException("Could not locate server field of " + loginListenerClass.getName());
			}
			if (loginListenerNetManager == null) {
				throw new IllegalStateException(
						"Could not locate network manager field of " + loginListenerClass.getName());
			}
			if (loginListenerState == null) {
				throw new IllegalStateException("Could not locate state field of " + loginListenerClass.getName());
			}
			if (loginListenerPlayer == null) {
				throw new IllegalStateException("Could not locate player field of " + loginListenerClass.getName());
			}
			Method loginListenerTick = null;
			Method loginListenerDisconnect = loginListenerClass.getMethod("disconnect", String.class);
			for (Class<?> clz : loginListenerClass.getInterfaces()) {
				String s = clz.getSimpleName();
				if (s.equals("IUpdatePlayerListBox") || s.equals("ITickable")) {
					loginListenerTick = loginListenerClass.getMethod(clz.getMethods()[0].getName());
					break;
				}
			}
			if (loginListenerTick == null) {
				try {
					loginListenerTick = loginListenerClass.getMethod("tick");
				} catch (ReflectiveOperationException ex) {
				}
			}
			if (loginListenerTick == null) {
				throw new IllegalStateException("Could not locate tick function of " + loginListenerClass.getName());
			}
			Object protocolStateOnResume = null;
			Object[] obj = enumProtocolState.getEnumConstants();
			if (obj != null && obj.length > 4) {
				protocolStateOnResume = obj[obj.length - 2];
			}
			if (protocolStateOnResume == null) {
				throw new IllegalStateException(
						"Could not locate stalling state enum of " + enumProtocolState.getName());
			}
			this.loginListenerClass = loginListenerClass;
			this.loginListenerCtor = loginListenerCtor;
			this.loginListenerProxy = ClassProxy.bindProxy(PlayerPostLoginInjector.class.getClassLoader(),
					loginListenerClass);
			this.loginListenerServer = loginListenerServer;
			this.loginListenerNetManager = loginListenerNetManager;
			this.enumProtocolState = enumProtocolState;
			this.protocolStateOnResume = protocolStateOnResume;
			this.loginListenerState = loginListenerState;
			this.loginListenerTick = loginListenerTick;
			this.loginListenerDisconnect = loginListenerDisconnect;
			this.loginListenerPlayer = loginListenerPlayer;
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private Object wrapLoginListener(Object loginListener, LoginEventContext ctx) {
		if (loginListenerClass == null) {
			bindLogin(loginListener);
		}
		if (!loginListenerClass.isAssignableFrom(loginListener.getClass())) {
			throw new IllegalStateException("Unknown LoginListener type: " + loginListener.getClass().getName());
		}
		try {
			return loginListenerProxy.createProxy(loginListenerCtor,
					new Object[] { loginListenerServer.get(loginListener), ctx.proxiedNetworkManager },
					(obj, meth, args) -> {
						if (loginListenerTick.equals(meth)) {
							try {
								ctx.markThrowOnLoginSuccess(true);
								try {
									return meth.invoke(loginListener, args);
								} finally {
									ctx.markThrowOnLoginSuccess(false);
								}
							} catch (InvocationTargetException ex) {
								Throwable er = ex.getCause();
								if (er instanceof EaglerError err) {
									Player player = null;
									Iterator<Property> itr = err.gameProfile.getProperties().values().iterator();
									while (itr.hasNext()) {
										Property prop = itr.next();
										if (prop.getName().startsWith("$eaglerMarker_")) {
											Player e = entityPlayers.remove(prop);
											if (e != null) {
												player = e;
											}
											itr.remove();
										}
									}
									if (player != null) {
										final Player playerFinal = player;
										fireEventLoginPostAsync(playerFinal, ctx, (res) -> {
											try {
												if (!res.isCancelled()) {
													handlerAdded.set(ctx.originalNetworkManager, false);
													ctx.channel.pipeline().replace("packet_handler", "packet_handler",
															(ChannelHandler) ctx.originalNetworkManager);
													Object entityPlayer = BukkitUnsafe.getHandle(playerFinal);
													loginListenerNetManager.set(loginListener,
															ctx.originalNetworkManager);
													loginListenerPlayer.set(loginListener, entityPlayer);
													loginListenerState.set(loginListener, protocolStateOnResume);
												} else {
													BaseComponent comp = res.getMessage();
													if (comp == null) {
														comp = new TextComponent("Connection Closed");
													}
													loginListenerDisconnect.invoke(loginListener, comp.toLegacyText());
												}
											} catch (ReflectiveOperationException e) {
												throw Util.propagateReflectThrowable(e);
											}
										});
										return null;
									} else {
										throw new IllegalStateException();
									}
								} else {
									if (er instanceof RuntimeException ee)
										throw ee;
									throw new RuntimeException(er);
								}
							}
						}
						return meth.invoke(loginListener, args);
					});
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private synchronized void bindPacketPlayDisconnect(Class<?> loginDisconnectPacket) {
		if (packetPlayDisconnect != null) {
			return;
		}
		try {
			String nm2 = loginDisconnectPacket.getName();
			nm2 = nm2.substring(0, nm2.lastIndexOf('.') + 1);
			Class<?> clz;
			try {
				clz = Class.forName(nm2 + "PacketPlayOutKickDisconnect");
			} catch (ReflectiveOperationException ex) {
				if (nm2.endsWith(".login.")) {
					clz = Class.forName(nm2.substring(0, nm2.length() - 7) + ".game.PacketPlayOutKickDisconnect");
				} else {
					throw ex;
				}
			}
			Constructor<?> ctor = null;
			Class<?> cmp = null;
			Field f = null;
			for (Constructor<?> ctor2 : loginDisconnectPacket.getConstructors()) {
				if (ctor2.getParameterCount() == 1) {
					Class<?>[] params = ctor2.getParameterTypes();
					ctor = clz.getConstructor(params);
					cmp = params[0];
					break;
				}
			}
			if (ctor == null) {
				throw new ReflectiveOperationException();
			}
			for (Field ff : loginDisconnectPacket.getDeclaredFields()) {
				if (cmp.equals(ff.getType())) {
					ff.setAccessible(true);
					f = ff;
					break;
				}
			}
			if (f == null) {
				throw new ReflectiveOperationException();
			}
			packetLoginDisconnectMsg = f;
			packetPlayDisconnectCtor = ctor;
			packetPlayDisconnect = clz;
		} catch (ReflectiveOperationException ex) {
			packetPlayDisconnect = void.class;
		}
	}

	public void handleLoginEvent(PlayerLoginEvent event) {
		Property marker = new Property("$eaglerMarker_" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE), "TMP");
		Object player = BukkitUnsafe.getHandle(event.getPlayer());
		GameProfile profile = BukkitUnsafe.getGameProfile(player);
		profile.getProperties().put(marker.getName(), marker);
		entityPlayers.put(marker, event.getPlayer());
	}

	private void fireEventLoginInit(Channel channel) {
		plugin.getServer().getPluginManager().callEvent(new PlayerLoginInitEventImpl(channel));
	}

	private void fireEventLoginPostAsync(Player player, LoginEventContext ctx, Consumer<PlayerLoginPostEvent> callback) {
		PlayerLoginPostEventImpl evt = new PlayerLoginPostEventImpl(player, ctx, callback);
		plugin.getServer().getPluginManager().callEvent(evt);
		evt.complete();
	}

	public static void setPlayState(PlayerLoginPostEvent evt) {
		((PlayerLoginPostEventImpl) evt).ctx.clientPlayState = true;
	}

}
