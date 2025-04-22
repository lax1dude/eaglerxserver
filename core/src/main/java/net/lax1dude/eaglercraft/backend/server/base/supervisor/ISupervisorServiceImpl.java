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

import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorService;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc.SupervisorRPCHandler;

public interface ISupervisorServiceImpl<PlayerObject> extends ISupervisorService<PlayerObject> {

	void handleEnable();

	void handleDisable();

	boolean shouldIgnoreUUID(UUID uuid);

	ISupervisorResolverImpl getRemoteOnlyResolver();

	SupervisorRPCHandler getRPCHandler();

	void acceptPlayer(UUID playerUUID, UUID brandUUID, int gameProtocol, int eaglerProtocol, String username,
			Consumer<EnumAcceptPlayer> callback);

	void dropOwnPlayer(UUID playerUUID);

	void notifySkinChange(UUID playerUUID, String serverName, boolean skin, boolean cape);

}
