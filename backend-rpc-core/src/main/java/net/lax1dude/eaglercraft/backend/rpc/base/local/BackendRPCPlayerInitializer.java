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

import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayerInitializer;

class BackendRPCPlayerInitializer<PlayerObject>
		implements IBackendRPCPlayerInitializer<PlayerInstanceLocal<PlayerObject>, PlayerObject> {

	private final EaglerXBackendRPCLocal<PlayerObject> server;

	BackendRPCPlayerInitializer(EaglerXBackendRPCLocal<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializePlayer(IPlatformPlayerInitializer<PlayerInstanceLocal<PlayerObject>, PlayerObject> initializer) {
		PlayerInstanceLocal<PlayerObject> playerInstance = new PlayerInstanceLocal<PlayerObject>(server, initializer.getPlayer());
		initializer.setPlayerAttachment(playerInstance);
		server.registerPlayer(playerInstance);

	}

	@Override
	public void confirmPlayer(IPlatformPlayer<PlayerObject> player) {
		PlayerInstanceLocal<PlayerObject> playerInstance = player.getAttachment();
		if (playerInstance != null) {
			server.confirmPlayer(playerInstance);
		}
	}

	@Override
	public void destroyPlayer(IPlatformPlayer<PlayerObject> player) {
		PlayerInstanceLocal<PlayerObject> playerInstance = player.getAttachment();
		if (playerInstance != null) {
			server.unregisterPlayer(playerInstance);
		}
	}

}
