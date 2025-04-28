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

package net.lax1dude.eaglercraft.backend.server.base.skins;

import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.event.SkinApplyEvent;
import net.skinsrestorer.api.property.SkinProperty;

public class SkinsRestorerHelper {

	private static final boolean classExists = Util.classExists("net.skinsrestorer.api.SkinsRestorerProvider");

	public static <PlayerObject> ISkinsRestorerHelper<PlayerObject> instance(EaglerXServer<PlayerObject> server) {
		if (classExists) {
			try {
				return new Impl<>(server);
			} catch (Throwable t) {
				server.logger().error("Encountered exception trying to access SkinsRestorer API", t);
				return null;
			}
		} else {
			return null;
		}
	}

	private static class Impl<PlayerObject> implements ISkinsRestorerHelper<PlayerObject> {

		private final EaglerXServer<PlayerObject> server;
		private ISkinsRestorerListener<PlayerObject> listener;

		private Impl(EaglerXServer<PlayerObject> server) {
			this.server = server;
			SkinsRestorer skinsRestorer = SkinsRestorerProvider.get();
			skinsRestorer.getEventBus().subscribe(server.getPlatform(), SkinApplyEvent.class, this::skinApplyHandler);
		}

		@Override
		public void setListener(ISkinsRestorerListener<PlayerObject> listener) {
			this.listener = listener;
		}

		private void skinApplyHandler(SkinApplyEvent evt) {
			if (listener != null) {
				PlayerObject playerObj = evt.getPlayer(server.getPlayerClass());
				if (playerObj != null) {
					BasePlayerInstance<PlayerObject> player = server.getPlayer(playerObj);
					if (player != null) {
						SkinProperty prop = evt.getProperty();
						listener.handleSRSkinApply(player, prop.getValue(), prop.getSignature());
					}
				}
			}
		}

	}

}
