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

package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.notifications.EnumBadgePriority;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;

class NotificationBadgeHelper {

	static EnumBadgePriority wrap(net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority priority) {
		return switch(priority) {
		case HIGHEST -> EnumBadgePriority.HIGHEST;
		case HIGHER -> EnumBadgePriority.HIGHER;
		default -> EnumBadgePriority.NORMAL;
		case LOW -> EnumBadgePriority.LOW;
		};
	}

	static net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority unwrap(EnumBadgePriority priority) {
		return switch(priority) {
		case HIGHEST -> net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority.HIGHEST;
		case HIGHER -> net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority.HIGHER;
		default -> net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority.NORMAL;
		case LOW -> net.lax1dude.eaglercraft.backend.server.api.notifications.EnumBadgePriority.LOW;
		};
	}

	static INotificationBadge wrap(SPacketNotifBadgeShowV4EAG badge, boolean managed) {
		return new NotificationBadgeLocal(badge, managed);
	}

	static NotificationBadgeLocal unwrap(INotificationBadge badge) {
		return (NotificationBadgeLocal) badge;
	}

	static class NotificationBadgeLocal implements INotificationBadge {

		final SPacketNotifBadgeShowV4EAG packet;
		final boolean managed;

		NotificationBadgeLocal(SPacketNotifBadgeShowV4EAG packet, boolean managed) {
			this.packet = packet;
			this.managed = managed;
		}

	}

}
