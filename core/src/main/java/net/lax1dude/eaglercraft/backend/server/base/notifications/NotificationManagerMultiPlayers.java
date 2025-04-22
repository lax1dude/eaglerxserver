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
import java.util.function.Consumer;

import com.google.common.collect.Collections2;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public class NotificationManagerMultiPlayers<PlayerObject> extends NotificationManagerMulti<PlayerObject> {

	private final Collection<NotificationManagerPlayer<PlayerObject>> players;

	public NotificationManagerMultiPlayers(NotificationService<PlayerObject> service,
			Collection<NotificationManagerPlayer<PlayerObject>> players) {
		super(service);
		this.players = players;
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getPlayerList() {
		return Collections2.transform(players, (mgr) -> mgr.player);
	}

	@Override
	protected void forTargets(Consumer<NotificationManagerBase<PlayerObject>> mgr) {
		players.forEach(mgr);
	}

}
