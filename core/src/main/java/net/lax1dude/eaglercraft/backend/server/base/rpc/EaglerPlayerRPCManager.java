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

package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.util.stream.Collectors;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccess;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccessEaglerV2;
import net.lax1dude.eaglercraft.backend.server.base.EaglerConnectionInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;

public class EaglerPlayerRPCManager<PlayerObject> extends BasePlayerRPCManager<PlayerObject> {

	private final EaglerPlayerInstance<PlayerObject> player;

	EaglerPlayerRPCManager(BackendRPCService<PlayerObject> service, EaglerPlayerInstance<PlayerObject> player) {
		super(service);
		this.player = player;
	}

	@Override
	public EaglerPlayerInstance<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	protected void handleEnabled(EaglerBackendRPCProtocol protocol) {
		if(protocol == EaglerBackendRPCProtocol.V1) {
			sendRPCInitPacket(new SPacketRPCEnabledSuccess(protocol.vers, player.getEaglerProtocol().ver));
		}else {
			EaglerConnectionInstance conn = player.connectionImpl();
			sendRPCInitPacket(new SPacketRPCEnabledSuccessEaglerV2(protocol.vers, player.getMinecraftProtocol(),
					player.getEaglerXServer().getSupervisorService().getNodeId(), player.getHandshakeEaglerProtocol(),
					player.getEaglerProtocol().ver, player.getRewindProtocolVersion(), conn.getCapabilityMask(),
					conn.getCapabilityVers(), conn.getExtCapabilities().entrySet().stream().map(
							(etr) -> new SPacketRPCEnabledSuccessEaglerV2.ExtCapability(etr.getKey(), etr.getValue() & 0xFF))
							.collect(Collectors.toList())));
		}
		handleEnableContext(new EaglerPlayerRPCContext<>(this, protocol));
	}

	@Override
	protected void sendReadyMessage() {
		int renderDistance = player.getEaglerXServer().getConfig().getSettings().getEaglerPlayersViewDistance();
		player.getPlatformPlayer().sendDataBackend(service.getReadyChannel(), new byte[] { (byte) 1, (byte) renderDistance });
	}
}
