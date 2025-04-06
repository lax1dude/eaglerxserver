package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.Arrays;

import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPauseMenuCustom;

public final class CustomPauseMenuWrapper implements ICustomPauseMenu {

	public static ICustomPauseMenu wrap(CPacketRPCSetPauseMenuCustom packet) {
		return new CustomPauseMenuWrapper(packet);
	}

	public static CPacketRPCSetPauseMenuCustom unwrap(ICustomPauseMenu packet) {
		return ((CustomPauseMenuWrapper) packet).packet;
	}

	private final CPacketRPCSetPauseMenuCustom packet;

	private CustomPauseMenuWrapper(CPacketRPCSetPauseMenuCustom packet) {
		this.packet = packet;
	}

	@Override
	public int hashCode() {
		CPacketRPCSetPauseMenuCustom packet = (CPacketRPCSetPauseMenuCustom) this.packet;
		int result = 1;
		result = 31 * result + packet.discordButtonMode;
		result = 31 * result + ((packet.discordButtonText == null) ? 0 : packet.discordButtonText.hashCode());
		result = 31 * result + ((packet.discordInviteURL == null) ? 0 : packet.discordInviteURL.hashCode());
		result = 31 * result + ((packet.imageData == null) ? 0 : packet.imageData.hashCode());
		result = 31 * result + ((packet.imageMappings == null) ? 0 : packet.imageMappings.hashCode());
		result = 31 * result + ((packet.serverInfoButtonText == null) ? 0 : packet.serverInfoButtonText.hashCode());
		result = 31 * result + packet.serverInfoEmbedPerms;
		result = 31 * result + ((packet.serverInfoEmbedTitle == null) ? 0 : packet.serverInfoEmbedTitle.hashCode());
		result = 31 * result + Arrays.hashCode(packet.serverInfoHash);
		result = 31 * result + packet.serverInfoMode;
		result = 31 * result + ((packet.serverInfoURL == null) ? 0 : packet.serverInfoURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CustomPauseMenuWrapper))
			return false;
		CPacketRPCSetPauseMenuCustom self = this.packet;
		CPacketRPCSetPauseMenuCustom other = ((CustomPauseMenuWrapper) obj).packet;
		if (self.discordButtonMode != other.discordButtonMode)
			return false;
		if (self.discordButtonText == null) {
			if (other.discordButtonText != null)
				return false;
		} else if (!self.discordButtonText.equals(other.discordButtonText))
			return false;
		if (self.discordInviteURL == null) {
			if (other.discordInviteURL != null)
				return false;
		} else if (!self.discordInviteURL.equals(other.discordInviteURL))
			return false;
		if (self.imageData == null) {
			if (other.imageData != null)
				return false;
		} else if (!self.imageData.equals(other.imageData))
			return false;
		if (self.imageMappings == null) {
			if (other.imageMappings != null)
				return false;
		} else if (!self.imageMappings.equals(other.imageMappings))
			return false;
		if (self.serverInfoButtonText == null) {
			if (other.serverInfoButtonText != null)
				return false;
		} else if (!self.serverInfoButtonText.equals(other.serverInfoButtonText))
			return false;
		if (self.serverInfoEmbedPerms != other.serverInfoEmbedPerms)
			return false;
		if (self.serverInfoEmbedTitle == null) {
			if (other.serverInfoEmbedTitle != null)
				return false;
		} else if (!self.serverInfoEmbedTitle.equals(other.serverInfoEmbedTitle))
			return false;
		if (!Arrays.equals(self.serverInfoHash, other.serverInfoHash))
			return false;
		if (self.serverInfoMode != other.serverInfoMode)
			return false;
		if (self.serverInfoURL == null) {
			if (other.serverInfoURL != null)
				return false;
		} else if (!self.serverInfoURL.equals(other.serverInfoURL))
			return false;
		return true;
	}

}
