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

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.WrongPacketException;

public abstract class ServerMessageHandler implements MessageController.IMessageHandler {

	protected final EaglerPlayerInstance<?> eaglerHandle;

	public ServerMessageHandler(EaglerPlayerInstance<?> eaglerHandle) {
		this.eaglerHandle = eaglerHandle;
	}

	public EaglerXServer<?> getServer() {
		return eaglerHandle.getEaglerXServer();
	}

	@Override
	public void handleException(Exception ex) {
		EaglerXServer<?> server = getServer();
		server.logger().error("Exception thrown while handling eagler packet for \"" + eaglerHandle.getUsername() + "\"!", ex);
		eaglerHandle.disconnect(server.componentBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED)
				.end().text("Eaglercraft Packet Error").end());
	}

	protected RuntimeException wrongPacket() {
		return new WrongPacketException();
	}

	protected RuntimeException notCapable() {
		return new NotCapableException();
	}

}
