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

package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IMessageController;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class RewindMessageControllerHandle implements IMessageController {

	IPlatformLogger logger;
	GameMessageHandler handler;
	RewindMessageControllerImpl impl;

	public RewindMessageControllerHandle(IPlatformLogger logger) {
		this.logger = logger;
	}

	@Override
	public void setOutboundHandler(GameMessageHandler handler) {
		this.handler = handler;
	}

	@Override
	public void recieveInboundMessage(GameMessagePacket packet) {
		if(impl != null) {
			impl.handlePacket(packet);
		}else {
			logger.error("Dropping inbound packet " + packet.getClass().getSimpleName()
					+ " on rewind connection because the connection is not ready!");
		}
	}

	void recieveOutboundMessage(GameMessagePacket packet) {
		if(handler != null) {
			packet.handlePacket(handler);
		}else {
			logger.error("Dropping outbound packet " + packet.getClass().getSimpleName()
					+ " on rewind connection because no handler is registered!");
		}
	}

}
