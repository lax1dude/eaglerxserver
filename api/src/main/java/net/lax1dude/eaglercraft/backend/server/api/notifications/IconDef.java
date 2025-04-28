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

import java.util.UUID;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public final class IconDef {

	@Nonnull
	public static IconDef create(@Nonnull UUID uuid, @Nonnull PacketImageData icon) {
		if (uuid == null) {
			throw new NullPointerException("uuid");
		}
		if (icon == null) {
			throw new NullPointerException("icon");
		}
		return new IconDef(uuid, icon);
	}

	private final UUID uuid;
	private final PacketImageData icon;

	private IconDef(UUID uuid, PacketImageData icon) {
		this.uuid = uuid;
		this.icon = icon;
	}

	@Nonnull
	public UUID getUUID() {
		return uuid;
	}

	@Nonnull
	public PacketImageData getIcon() {
		return icon;
	}

	@Nonnull
	public SPacketNotifIconsRegisterV4EAG.CreateIcon toPacket() {
		return new SPacketNotifIconsRegisterV4EAG.CreateIcon(uuid.getMostSignificantBits(),
				uuid.getLeastSignificantBits(), icon);
	}

}
