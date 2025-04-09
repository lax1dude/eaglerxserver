package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.bukkit.entity.Player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class BukkitUnsafe {

	private static Class<?> class_CraftPlayer = null;
	private static Method method_CraftPlayer_getHandle = null;
	private static Class<?> class_EntityPlayer = null;
	private static Method method_EntityPlayer_getProfile = null;

	private static synchronized void bindCraftPlayer(Player playerObject) {
		if(class_CraftPlayer != null) {
			return;
		}
		Class<?> clz = playerObject.getClass();
		try {
			method_CraftPlayer_getHandle = clz.getMethod("getHandle");
			Object entityPlayer = method_CraftPlayer_getHandle.invoke(playerObject);
			Class<?> clz2 = entityPlayer.getClass();
			method_EntityPlayer_getProfile = clz2.getMethod("getProfile");
			class_EntityPlayer = clz2;
			class_CraftPlayer = clz;
		}catch(Exception ex) {
			throw new RuntimeException("Reflection failed!", ex);
		}
	}

	private static final boolean paperProfileAPISupport;

	static {
		boolean paperProfileAPISupport_ = false;
		try {
			Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
			paperProfileAPISupport_ = true;
		} catch (ClassNotFoundException e) {
			// Paper profile API is unsupported
		}
		paperProfileAPISupport = paperProfileAPISupport_;
	}

	public static boolean isEaglerPlayerProperty(Player player) {
		if(paperProfileAPISupport) {
			return isEaglerPlayerPropertyPaper(player);
		}else {
			if(class_CraftPlayer == null) {
				bindCraftPlayer(player);
			}
			try {
				Multimap<String, Property> props = ((GameProfile) method_EntityPlayer_getProfile
						.invoke(method_CraftPlayer_getHandle.invoke(player))).getProperties();
				Collection<Property> tex = props.get("isEaglerPlayer");
				if(!tex.isEmpty()) {
					return Boolean.parseBoolean(tex.iterator().next().getValue());
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Reflection failed!", e);
			}
			return false;
		}
	}

	private static boolean isEaglerPlayerPropertyPaper(Player player) {
		PlayerProfile profile = player.getPlayerProfile();
		if(profile != null) {
			for(ProfileProperty o : profile.getProperties()) {
				if("isEaglerPlayer".equals(o.getName())) {
					return Boolean.parseBoolean(o.getValue());
				}
			}
		}
		return false;
	}

}
