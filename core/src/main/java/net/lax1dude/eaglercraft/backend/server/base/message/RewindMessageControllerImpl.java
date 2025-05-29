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

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class RewindMessageControllerImpl extends MessageController {

	private final RewindMessageControllerHandle handle;

	public RewindMessageControllerImpl(RewindMessageControllerHandle handle, GamePluginMessageProtocol protocol,
			GameMessageHandler handler, IExceptionCallback exceptionHandler) {
		super(protocol, handler, exceptionHandler, null, -1, 0);
		this.handle = handle;
		this.handle.impl = this;
	}

	public RewindMessageControllerImpl(RewindMessageControllerHandle handle, GamePluginMessageProtocol protocol,
			IMessageHandler handler) {
		this(handle, protocol, handler, handler);
	}

	@Override
	protected void writePacket(GameMessagePacket packet) throws IOException {
		handle.recieveOutboundMessage(packet);
	}

	@Override
	protected void writeMultiPacket(GameMessagePacket[] packets) throws IOException {
		throw new IllegalStateException();
	}

}
