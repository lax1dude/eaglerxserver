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

package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCNotifBadgeShow;

public final class NotificationBadgeWrapper implements INotificationBadge {

	public static INotificationBadge wrap(CPacketRPCNotifBadgeShow packet) {
		return new NotificationBadgeWrapper(packet);
	}

	public static CPacketRPCNotifBadgeShow unwrap(INotificationBadge packet) {
		return ((NotificationBadgeWrapper) packet).packet;
	}

	private final CPacketRPCNotifBadgeShow packet;

	private NotificationBadgeWrapper(CPacketRPCNotifBadgeShow packet) {
		this.packet = packet;
	}

	@Override
	public int hashCode() {
		CPacketRPCNotifBadgeShow packet = this.packet;
		int result = 1;
		result = 31 * result + packet.backgroundColor;
		result = 31 * result + ((packet.badgeUUID == null) ? 0 : packet.badgeUUID.hashCode());
		result = 31 * result + ((packet.bodyComponent == null) ? 0 : packet.bodyComponent.hashCode());
		result = 31 * result + packet.bodyTxtColor;
		result = 31 * result + packet.expireAfterSec;
		result = 31 * result + packet.hideAfterSec;
		result = 31 * result + ((packet.mainIconUUID == null) ? 0 : packet.mainIconUUID.hashCode());
		result = 31 * result + (packet.managed ? 1231 : 1237);
		result = 31 * result + (int) (packet.originalTimestampSec ^ (packet.originalTimestampSec >>> 32));
		result = 31 * result + ((packet.priority == null) ? 0 : packet.priority.hashCode());
		result = 31 * result + (packet.silent ? 1231 : 1237);
		result = 31 * result + ((packet.sourceComponent == null) ? 0 : packet.sourceComponent.hashCode());
		result = 31 * result + packet.sourceTxtColor;
		result = 31 * result + ((packet.titleComponent == null) ? 0 : packet.titleComponent.hashCode());
		result = 31 * result + ((packet.titleIconUUID == null) ? 0 : packet.titleIconUUID.hashCode());
		result = 31 * result + packet.titleTxtColor;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof NotificationBadgeWrapper o2))
			return false;
		CPacketRPCNotifBadgeShow self = this.packet;
		CPacketRPCNotifBadgeShow other = o2.packet;
		if (self.backgroundColor != other.backgroundColor)
			return false;
		if (self.badgeUUID == null) {
			if (other.badgeUUID != null)
				return false;
		} else if (!self.badgeUUID.equals(other.badgeUUID))
			return false;
		if (self.bodyComponent == null) {
			if (other.bodyComponent != null)
				return false;
		} else if (!self.bodyComponent.equals(other.bodyComponent))
			return false;
		if (self.bodyTxtColor != other.bodyTxtColor)
			return false;
		if (self.expireAfterSec != other.expireAfterSec)
			return false;
		if (self.hideAfterSec != other.hideAfterSec)
			return false;
		if (self.mainIconUUID == null) {
			if (other.mainIconUUID != null)
				return false;
		} else if (!self.mainIconUUID.equals(other.mainIconUUID))
			return false;
		if (self.managed != other.managed)
			return false;
		if (self.originalTimestampSec != other.originalTimestampSec)
			return false;
		if (self.priority != other.priority)
			return false;
		if (self.silent != other.silent)
			return false;
		if (self.sourceComponent == null) {
			if (other.sourceComponent != null)
				return false;
		} else if (!self.sourceComponent.equals(other.sourceComponent))
			return false;
		if (self.sourceTxtColor != other.sourceTxtColor)
			return false;
		if (self.titleComponent == null) {
			if (other.titleComponent != null)
				return false;
		} else if (!self.titleComponent.equals(other.titleComponent))
			return false;
		if (self.titleIconUUID == null) {
			if (other.titleIconUUID != null)
				return false;
		} else if (!self.titleIconUUID.equals(other.titleIconUUID))
			return false;
		if (self.titleTxtColor != other.titleTxtColor)
			return false;
		return true;
	}

}
