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

import net.lax1dude.eaglercraft.backend.supervisor.protocol.EaglerSupervisorProtocol;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvHandshakeFailure;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvHandshakeSuccess;

public class SupervisorClientHandshakeHandler implements EaglerSupervisorHandler {

	private final SupervisorService<?> controller;
	private final SupervisorPacketHandler handler;

	public SupervisorClientHandshakeHandler(SupervisorService<?> controller, SupervisorPacketHandler handler) {
		this.controller = controller;
		this.handler = handler;
	}

	@Override
	public void handleServer(SPacketSvHandshakeSuccess pkt) {
		handler.setConnectionProtocol(EaglerSupervisorProtocol.V1);
		//handler.setConnectionHandler(new SupervisorClientV1Handler(controller, handler));
		if(pkt.selectedProtocol == 1) {
			controller.handleHandshakeSuccess(handler, pkt.nodeId);
		}else {
			controller.handleHandshakeFailure(handler, "Wrong Protocol: " + pkt.selectedProtocol);
		}
	}

	@Override
	public void handleServer(SPacketSvHandshakeFailure pkt) {
		controller.handleHandshakeFailure(handler, SPacketSvHandshakeFailure.failureCodeToString(pkt.failureCode));
	}

	@Override
	public void handleDisconnected() {
		controller.handleDisconnected();
	}

}