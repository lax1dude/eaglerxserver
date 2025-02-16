package net.lax1dude.eaglercraft.backend.server.bungee;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.md_5.bungee.api.connection.PendingConnection;

public class BungeeUnsafe {

	private static final Class<?> class_InitialHandler;
	private static final Method method_InitialHandler_getBrandMessage;
	private static final Field field_InitialHandler_ch;
	private static final Class<?> class_ChannelWrapper;
	private static final Method method_ChannelWrapper_getHandle;
	private static final Class<?> class_PluginMessage;
	private static final Method class_PluginMessage_getData;

	static {
		try {
			class_InitialHandler = Class.forName("net.md_5.bungee.connection.InitialHandler");
			method_InitialHandler_getBrandMessage = class_InitialHandler.getMethod("getBrandMessage");
			field_InitialHandler_ch = class_InitialHandler.getField("ch");
			field_InitialHandler_ch.setAccessible(true);
			class_ChannelWrapper = Class.forName("net.md_5.bungee.netty.ChannelWrapper");
			method_ChannelWrapper_getHandle = class_ChannelWrapper.getMethod("getHandle");
			class_PluginMessage = Class.forName("net.md_5.bungee.connection.PluginMessage");
			class_PluginMessage_getData = class_PluginMessage.getMethod("getData");
		}catch(Exception ex) {
			throw Util.propagateReflectThrowable(ex);
		}
	}

	public static byte[] getBrandMessage(PendingConnection pendingConnection) {
		if(class_InitialHandler.isAssignableFrom(pendingConnection.getClass())) {
			try {
				Object obj = method_InitialHandler_getBrandMessage.invoke(pendingConnection);
				if(obj == null) {
					return null;
				}
				return (byte[]) class_PluginMessage_getData.invoke(obj);
			}catch(IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
				throw Util.propagateReflectThrowable(ex);
			}
		}else {
			return null;
		}
	}

	public static Channel getInitialHandlerChannel(PendingConnection pendingConnection) {
		if(class_InitialHandler.isAssignableFrom(pendingConnection.getClass())) {
			try {
				return (Channel) method_ChannelWrapper_getHandle.invoke(field_InitialHandler_ch.get(pendingConnection));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
				throw Util.propagateReflectThrowable(ex);
			}
		}else {
			throw new RuntimeException("PendingConnection is an unknown type: " + pendingConnection.getClass().getName());
		}
	}

}
