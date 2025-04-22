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

package net.lax1dude.eaglercraft.backend.server.api.notifications;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsReleaseV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface INotificationManager<PlayerObject> {

	@Nonnull
	Collection<IEaglerPlayer<PlayerObject>> getPlayerList();

	@Nonnull
	INotificationService<PlayerObject> getNotificationService();

	void registerNotificationIcon(@Nonnull UUID iconUUID);

	void registerNotificationIcons(@Nonnull Collection<UUID> iconUUIDs);

	void registerUnmanagedNotificationIcon(@Nonnull UUID iconUUID, @Nonnull PacketImageData icon);

	void registerUnmanagedNotificationIcons(@Nonnull Collection<IconDef> icons);

	void registerUnmanagedNotificationIconsRaw(@Nonnull Collection<SPacketNotifIconsRegisterV4EAG.CreateIcon> icons);

	void releaseUnmanagedNotificationIcon(@Nonnull UUID iconUUID);

	void releaseUnmanagedNotificationIcons(@Nonnull Collection<UUID> iconUUIDs);

	void releaseUnmanagedNotificationIconsRaw(@Nonnull Collection<SPacketNotifIconsReleaseV4EAG.DestroyIcon> iconUUIDs);

	void releaseNotificationIcon(@Nonnull UUID iconUUID);

	void releaseNotificationIcons(@Nonnull Collection<UUID> iconUUIDs);

	void releaseNotificationIcons();

	void showNotificationBadge(@Nonnull INotificationBuilder<?> builder);

	void showUnmanagedNotificationBadge(@Nonnull INotificationBuilder<?> builder);

	void showNotificationBadge(@Nonnull SPacketNotifBadgeShowV4EAG packet);

	void showUnmanagedNotificationBadge(@Nonnull SPacketNotifBadgeShowV4EAG packet);

	void hideNotificationBadge(@Nonnull UUID badgeUUID);

}
