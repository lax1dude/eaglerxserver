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

package net.lax1dude.eaglercraft.backend.rewind_v1_5.base;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.collect.HPPC;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectObjectMap;

public class TabListTracker {

	public static class ListItem {

		public final String playerName;
		public final UUID playerUUID;
		public String displayName;
		public String oldDisplayName;
		public int pingValue;
		public int entityId;
		public boolean dirty;

		public ListItem(String playerName, UUID playerUUID, String displayName, int pingValue) {
			this.playerName = playerName;
			this.playerUUID = playerUUID;
			this.displayName = this.oldDisplayName = displayName;
			this.pingValue = pingValue;
			this.dirty = false;
		}

	}

	private final ObjectObjectMap<UUID, ListItem> playerUUIDToItem;
	private final ObjectObjectMap<String, ListItem> playerNameToItem;

	public TabListTracker(HPPC hppc) {
		this.playerUUIDToItem = hppc.createObjectObjectHashMap(32);
		this.playerNameToItem = hppc.createObjectObjectHashMap(32);
	}

	public ListItem getItemByUUID(UUID uuid) {
		return playerUUIDToItem.get(uuid);
	}

	public ListItem getItemByName(String name) {
		return playerNameToItem.get(name);
	}

	/**
	 * Note: returns the old item!
	 */
	public ListItem handleAddPlayer(String playerName, UUID playerUUID, String displayName, int pingValue,
			IEaglerXServerAPI<?> interner) {
		playerName = playerName.intern();
		playerUUID = interner.intern(playerUUID);
		ListItem newItem = new ListItem(playerName, playerUUID,
				playerName.equals(displayName) ? playerName : displayName.intern(), pingValue);
		ListItem oldItem = playerUUIDToItem.put(playerUUID, newItem);
		if (oldItem != null) {
			playerNameToItem.remove(oldItem.playerName);
		}
		playerNameToItem.put(playerName, newItem);
		return oldItem;
	}

	public ListItem handleSpawnPlayer(UUID playerUUID, int entityId) {
		ListItem itm = playerUUIDToItem.get(playerUUID);
		if (itm != null) {
			itm.entityId = entityId;
		}
		return itm;
	}

	public ListItem handleUpdatePing(UUID playerUUID, int pingValue) {
		ListItem itm = playerUUIDToItem.get(playerUUID);
		if (itm != null) {
			itm.pingValue = pingValue;
		}
		return itm;
	}

	public ListItem handleUpdateDisplayName(UUID playerUUID, String displayName) {
		ListItem itm = playerUUIDToItem.get(playerUUID);
		if (itm != null) {
			if (displayName != null) {
				displayName = displayName.intern();
				if (!displayName.equals(itm.displayName)) {
					itm.displayName = displayName;
					itm.dirty = true;
				}
			} else {
				displayName = itm.playerName;
				if (!displayName.equals(itm.displayName)) {
					itm.displayName = displayName;
				}
			}
		}
		return itm;
	}

	public ListItem handleRemovePlayer(UUID pliUuid) {
		ListItem itm = playerUUIDToItem.remove(pliUuid);
		if (itm != null) {
			playerNameToItem.remove(itm.playerName);
		}
		return itm;
	}

}
