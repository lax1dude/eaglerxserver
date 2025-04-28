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

package net.lax1dude.eaglercraft.backend.supervisor.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFutureListener;
import net.lax1dude.eaglercraft.backend.supervisor.EaglerXSupervisorServer;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.EaglerSupervisorProtocol;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvHandshake;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvHandshakeFailure;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvHandshakeSuccess;

public class SupervisorServerHandshakeHandler implements EaglerSupervisorHandler {

	private static final Logger logger = LoggerFactory.getLogger("SupervisorServerHandshakeHandler");

	private final EaglerXSupervisorServer server;
	private final SupervisorPacketHandler handler;

	public SupervisorServerHandshakeHandler(EaglerXSupervisorServer server, SupervisorPacketHandler handler) {
		this.server = server;
		this.handler = handler;
		logger.info("[{}]: Supervisor server connection opened", handler.getChannel().remoteAddress());
	}

	public void handleClient(CPacketSvHandshake pkt) {
		find_vers: {
			int[] pp = pkt.supportedProtocols;
			for (int i = 0; i < pp.length; ++i) {
				if (pp[i] == 1) {
					break find_vers;
				}
			}
			logger.error("[{}]: Dropping connection because of protocol version mismatch",
					handler.getChannel().remoteAddress());
			handler.getChannel()
					.writeAndFlush(
							new SPacketSvHandshakeFailure(SPacketSvHandshakeFailure.FAILURE_CODE_OUTDATED_SERVER))
					.addListener(ChannelFutureListener.CLOSE);
			return;
		}

		String k = server.getConfig().getSecretKey();
		if (k != null && !k.equals(pkt.secretKey)) {
			logger.error("[{}]: Dropping connection because of invalid secret key",
					handler.getChannel().remoteAddress());
			handler.getChannel()
					.writeAndFlush(new SPacketSvHandshakeFailure(SPacketSvHandshakeFailure.FAILURE_CODE_INVALID_SECRET))
					.addListener(ChannelFutureListener.CLOSE);
			return;
		}

		SupervisorClientInstance client = server.registerClient(handler);

		handler.getChannel().writeAndFlush(new SPacketSvHandshakeSuccess(1, client.getNodeId()));

		handler.setConnectionProtocol(EaglerSupervisorProtocol.V1);
		handler.setConnectionHandler(new SupervisorServerV1Handler(server, handler, client));

		logger.info("[{}]: Handshake successful, connected with {} protocol", handler.getChannel().remoteAddress(),
				handler.getConnectionProtocol().name());
		logger.info("[{}]: Assigned node id #{}", handler.getChannel().remoteAddress(), client.getNodeId());
	}

	@Override
	public void handleDisconnected() {
		logger.error("[{}]: Disconnected during handshake", handler.getChannel().remoteAddress());
	}

}