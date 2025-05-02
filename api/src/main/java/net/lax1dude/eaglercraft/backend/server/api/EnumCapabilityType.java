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

import javax.annotation.Nullable;

/**
 * Represents capability types supported by standard Eaglercraft clients.
 */
public enum EnumCapabilityType {

	/**
	 * Update certificate service.
	 */
	UPDATE(0),

	/**
	 * Voice channel service.
	 */
	VOICE(1),

	/**
	 * WebSocket transfer packets.
	 */
	REDIRECT(2),

	/**
	 * Notification service.
	 */
	NOTIFICATION(3),

	/**
	 * Pause menu customization.
	 */
	PAUSE_MENU(4),

	/**
	 * WebView support.
	 */
	WEBVIEW(5),

	/**
	 * Cookie support.
	 */
	COOKIE(6),

	/**
	 * Reserved for a future update.
	 */
	EAGLER_IP(7);

	private final int id;
	private final int bit;

	private EnumCapabilityType(int id) {
		this.id = id;
		this.bit = 1 << id;
	}

	/**
	 * Gets the network ID of this capability type.
	 * 
	 * @return Capability ID, between 0 and 31.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the bit used to represent this capability in a bitfield.
	 * 
	 * <p>Value should be equal to {@code (1 << getId())}.
	 * 
	 * @return Capability ID, between 0 and 31.
	 */
	public int getBit() {
		return bit;
	}

	/**
	 * Finds the capability type enum from a network ID.
	 * 
	 * @param id The network ID of the capability.
	 * @return The capability type enum, or {@code null} if unknown.
	 */
	@Nullable
	public static EnumCapabilityType getById(int id) {
		return id >= 0 && id < LOOKUP.length ? LOOKUP[id] : null;
	}

	private static final EnumCapabilityType[] LOOKUP = new EnumCapabilityType[8];

	static {
		for (EnumCapabilityType cap : values()) {
			LOOKUP[cap.id] = cap;
		}
	}

}
