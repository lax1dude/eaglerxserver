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

package net.lax1dude.eaglercraft.backend.server.base.notifications;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public abstract class NotificationManagerMulti<PlayerObject> extends NotificationManagerBase<PlayerObject> {

	public NotificationManagerMulti(NotificationService<PlayerObject> service) {
		super(service);
	}

	protected abstract void forTargets(Consumer<NotificationManagerBase<PlayerObject>> mgr);

	@Override
	protected void touchIcon(UUID uuid) {
		forTargets((target) -> target.touchIcon(uuid));
	}

	@Override
	protected void touchIcons(GameMessagePacket packet, UUID uuidA, UUID uuidB) {
		forTargets((target) -> target.touchIcons(packet, uuidA, uuidB));
	}

	@Override
	protected void touchIcons(Collection<UUID> uuids, Collection<UUID> tmp) {
		forTargets((target) -> target.touchIcons(uuids, tmp));
	}

	@Override
	protected void releaseIcon(UUID uuid) {
		forTargets((target) -> target.releaseIcon(uuid));
	}

	@Override
	protected void releaseIcons() {
		forTargets((target) -> target.releaseIcons());
	}

	@Override
	protected void releaseIcons(Collection<UUID> uuids, Collection<UUID> tmp) {
		forTargets((target) -> target.releaseIcons(uuids, tmp));
	}

	@Override
	protected void sendPacket(GameMessagePacket packet) {
		forTargets((target) -> target.sendPacket(packet));
	}

}
