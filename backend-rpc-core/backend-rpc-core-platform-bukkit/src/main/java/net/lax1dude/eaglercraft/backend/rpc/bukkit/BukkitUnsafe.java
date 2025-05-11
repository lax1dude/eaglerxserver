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
		if (class_CraftPlayer != null) {
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
		} catch (Exception ex) {
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
		if (paperProfileAPISupport) {
			return isEaglerPlayerPropertyPaper(player);
		} else {
			if (class_CraftPlayer == null) {
				bindCraftPlayer(player);
			}
			try {
				Multimap<String, Property> props = ((GameProfile) method_EntityPlayer_getProfile
						.invoke(method_CraftPlayer_getHandle.invoke(player))).getProperties();
				Collection<Property> tex = props.get("isEaglerPlayer");
				if (!tex.isEmpty()) {
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
		if (profile != null) {
			for (ProfileProperty o : profile.getProperties()) {
				if ("isEaglerPlayer".equals(o.getName())) {
					return Boolean.parseBoolean(o.getValue());
				}
			}
		}
		return false;
	}

}
