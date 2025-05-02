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

package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents known subversions of capability types supported by
 * standard Eaglercraft clients.
 * 
 * @see EnumCapabilityType
 */
public enum EnumCapabilitySpec {

	/**
	 * Update certificate service, subversion #0.
	 */
	UPDATE_V0(EnumCapabilityType.UPDATE, 0),

	/**
	 * Voice channel service, subversion #0.
	 */
	VOICE_V0(EnumCapabilityType.VOICE, 0),

	/**
	 * WebSocket transfer packets, subversion #0.
	 */
	REDIRECT_V0(EnumCapabilityType.REDIRECT, 0),

	/**
	 * Notification service, subversion #0.
	 */
	NOTIFICATION_V0(EnumCapabilityType.NOTIFICATION, 0),

	/**
	 * Pause menu customization, subversion #0.
	 */
	PAUSE_MENU_V0(EnumCapabilityType.PAUSE_MENU, 0),

	/**
	 * WebView support, subversion #0.
	 */
	WEBVIEW_V0(EnumCapabilityType.WEBVIEW, 0),

	/**
	 * Cookie support, subversion #0.
	 */
	COOKIE_V0(EnumCapabilityType.COOKIE, 0),

	/**
	 * Reserved for a future update.
	 */
	EAGLER_IP_V0(EnumCapabilityType.EAGLER_IP, 0);

	private final EnumCapabilityType type;
	private final int ver;

	private EnumCapabilitySpec(EnumCapabilityType type, int ver) {
		this.type = type;
		this.ver = ver;
	}

	/**
	 * Gets the capability type enum of this capability spec.
	 * 
	 * @return The capability type enum.
	 */
	@Nonnull
	public EnumCapabilityType getType() {
		return type;
	}

	/**
	 * Gets the network ID of this capability type.
	 * 
	 * @return Capability ID, between 0 and 31.
	 */
	public int getId() {
		return type.getId();
	}

	/**
	 * Gets the bit used to represent this capability in a bitfield.
	 * 
	 * <p>Value should be equal to {@code (1 << getId())}.
	 * 
	 * @return Capability ID, between 0 and 31.
	 */
	public int getBit() {
		return type.getBit();
	}

	/**
	 * Gets the subversion of the capability spec.
	 * 
	 * @return Capability subversion, between 0 and 31.
	 */
	public int getVer() {
		return ver;
	}

	/**
	 * Finds the capability spec enum from a network ID and subversion.
	 * 
	 * @param id The network ID of the capability.
	 * @param ver The subversion of the capability.
	 * @return The capability spec enum, or {@code null} if unknown
	 */
	@Nullable
	public static EnumCapabilitySpec fromId(int id, int ver) {
		if (id >= 0 && id < capsMap.length) {
			EnumCapabilitySpec[] cap = capsMap[id];
			if (cap != null) {
				if (ver >= 0 && ver < cap.length) {
					return cap[ver];
				}
			}
		}
		return null;
	}

	private static final EnumCapabilitySpec[][] capsMap = new EnumCapabilitySpec[16][];

	static {
		for (EnumCapabilitySpec cap : values()) {
			int id = cap.type.getId();
			EnumCapabilitySpec[] lst = capsMap[id];
			if (lst == null) {
				capsMap[id] = lst = new EnumCapabilitySpec[16];
			}
			if (lst.length <= cap.ver) {
				EnumCapabilitySpec[] newList = new EnumCapabilitySpec[lst.length << 1];
				System.arraycopy(lst, 0, newList, 0, lst.length);
				capsMap[id] = lst = newList;
			}
			lst[cap.ver] = cap;
		}
	}

}
