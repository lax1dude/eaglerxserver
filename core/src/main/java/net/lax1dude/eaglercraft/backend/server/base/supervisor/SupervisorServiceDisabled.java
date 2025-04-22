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

package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorConnection;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorResolver;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc.SupervisorRPCHandler;

public class SupervisorServiceDisabled<PlayerObject> implements ISupervisorServiceImpl<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;

	public SupervisorServiceDisabled(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isSupervisorEnabled() {
		return false;
	}

	@Override
	public boolean isSupervisorConnected() {
		return false;
	}

	@Override
	public ISupervisorConnection getConnection() {
		throw supervisorDisable();
	}

	@Override
	public int getNodeId() {
		return -1;
	}

	@Override
	public int getPlayerTotal() {
		return server.getPlatform().getPlayerTotal();
	}

	@Override
	public int getPlayerMax() {
		return server.getPlatform().getPlayerMax();
	}

	@Override
	public SupervisorRPCHandler getRPCHandler() {
		throw supervisorDisable();
	}

	@Override
	public ISupervisorResolver getPlayerResolver() {
		throw supervisorDisable();
	}

	@Override
	public ISupervisorResolverImpl getRemoteOnlyResolver() {
		throw supervisorDisable();
	}

	private static UnsupportedOperationException supervisorDisable() {
		return new UnsupportedOperationException("Supervisor is not enabled!");
	}

	@Override
	public void handleEnable() {
	}

	@Override
	public void handleDisable() {
	}

	@Override
	public boolean shouldIgnoreUUID(UUID uuid) {
		return true;
	}

	@Override
	public void acceptPlayer(UUID playerUUID, UUID brandUUID, int gameProtocol, int eaglerProtocol, String username,
			Consumer<EnumAcceptPlayer> callback) {
		throw supervisorDisable();
	}

	@Override
	public void dropOwnPlayer(UUID playerUUID) {
	}

	@Override
	public void notifySkinChange(UUID playerUUID, String serverName, boolean skin, boolean cape) {
	}

}
