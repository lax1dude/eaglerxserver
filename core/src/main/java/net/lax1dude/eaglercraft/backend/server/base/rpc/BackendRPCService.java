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

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class BackendRPCService<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final String rpcChannel;
	private final String readyChannel;
	final SerializationContext handshakeCtx;

	public BackendRPCService(EaglerXServer<PlayerObject> server) {
		this.server = server;
		this.rpcChannel = BackendChannelHelper.getRPCChannel(server);
		this.readyChannel = BackendChannelHelper.getReadyChannel(server);
		this.handshakeCtx = new SerializationContext(EaglerBackendRPCProtocol.INIT) {
			@Override
			protected IPlatformLogger logger() {
				return server.logger();
			}
		};
	}

	public VanillaPlayerRPCManager<PlayerObject> createVanillaPlayerRPCManager(
			BasePlayerInstance<PlayerObject> player) {
		return new VanillaPlayerRPCManager<>(this, player);
	}

	public EaglerPlayerRPCManager<PlayerObject> createEaglerPlayerRPCManager(
			EaglerPlayerInstance<PlayerObject> player) {
		return new EaglerPlayerRPCManager<>(this, player);
	}

	public String getRPCChannel() {
		return rpcChannel;
	}

	public String getReadyChannel() {
		return readyChannel;
	}

}
