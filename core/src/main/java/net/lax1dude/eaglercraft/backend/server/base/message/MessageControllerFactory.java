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

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class MessageControllerFactory {

	public static MessageController initializePlayer(EaglerPlayerInstance<?> instance) {
		GamePluginMessageProtocol protocol = instance.getEaglerProtocol();
		ServerMessageHandler handler = createHandler(protocol.ver, instance);
		RewindMessageControllerHandle rewindHandle = instance.getRewindMessageControllerHandle();
		if (rewindHandle != null) {
			return new RewindMessageControllerImpl(rewindHandle, protocol, handler);
		}
		EaglerXServer<?> server = instance.getEaglerXServer();
		ConfigDataSettings settings = server.getConfig().getSettings();
		int sendDelay = settings.getProtocolV4DefragSendDelay();
		int maxPackets = settings.getProtocolV4DefragMaxPackets();
		if (protocol.ver >= 5) {
			return InjectedMessageController.injectEagler(protocol, handler,
					instance.getPlatformPlayer().getChannel(), sendDelay, maxPackets);
		} else {
			boolean modernChannelNames = server.getPlatform().isModernPluginChannelNamesOnly()
					|| instance.getMinecraftProtocol() > 340;
			if (protocol.ver == 4 && sendDelay > 0) {
				return new LegacyMessageController(protocol, handler,
						instance.getPlatformPlayer().getChannel().eventLoop(), sendDelay, maxPackets,
						modernChannelNames);
			} else {
				return new LegacyMessageController(protocol, handler, null, 0, maxPackets, modernChannelNames);
			}
		}
	}

	private static ServerMessageHandler createHandler(int ver, EaglerPlayerInstance<?> instance) {
		return switch (ver) {
		case 5 -> new ServerV5MessageHandler(instance);
		case 4 -> new ServerV4MessageHandler(instance);
		case 3 -> new ServerV3MessageHandler(instance);
		default -> throw new IllegalStateException();
		};
	}

}
