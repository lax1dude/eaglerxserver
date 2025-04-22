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

package net.lax1dude.eaglercraft.backend.server.base.command;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class CommandVersion<PlayerObject> extends EaglerCommand<PlayerObject> {

	public CommandVersion(EaglerXServer<PlayerObject> server) {
		super(server, "eaglerxserver", "eaglercraft.command.version");
	}

	@Override
	public void handle(IEaglerXServerCommandType<PlayerObject> command, IPlatformCommandSender<PlayerObject> sender,
			String[] args) {
		sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
				.text("Server Brand: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
				.text(getServer().getServerBrand()).end().end());
		sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
				.text("Server Version: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
				.text(getServer().getServerVersion()).end().end());
		sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
				.text("Server Platform: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
				.text(getServer().getPlatformType().getName()).end().end());
	}

}
