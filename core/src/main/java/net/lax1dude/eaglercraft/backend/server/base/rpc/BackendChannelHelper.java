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

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageChannel;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.MessageChannel;
import net.lax1dude.eaglercraft.backend.server.base.voice.IVoiceManagerImpl;
import net.lax1dude.eaglercraft.backend.voice.protocol.EaglerVCProtocol;

public class BackendChannelHelper {

	public static <PlayerObject> Collection<IEaglerXServerMessageChannel<PlayerObject>> getBackendChannels(
			EaglerXServer<PlayerObject> server) {
		ImmutableList.Builder<IEaglerXServerMessageChannel<PlayerObject>> backendChannelBuilder = ImmutableList
				.builder();
		backendChannelBuilder.add(new MessageChannel<>(EaglerBackendRPCProtocol.CHANNEL_NAME,
				EaglerBackendRPCProtocol.CHANNEL_NAME_MODERN, (channel, player, message) -> {
			BasePlayerInstance<PlayerObject> attachment = player
					.<BasePlayerInstance<PlayerObject>>getPlayerAttachment();
			if (attachment != null) {
				BasePlayerRPCManager<PlayerObject> rpcManager = attachment.getPlayerRPCManager();
				if (rpcManager != null) {
					rpcManager.handleRPCPacketData(message);
				}
			}
		}));
		backendChannelBuilder.add(new MessageChannel<>(EaglerVCProtocol.CHANNEL_NAME,
				EaglerVCProtocol.CHANNEL_NAME_MODERN, (channel, player, message) -> {
			BasePlayerInstance<PlayerObject> attachment = player
					.<BasePlayerInstance<PlayerObject>>getPlayerAttachment();
			if (attachment != null) {
				EaglerPlayerInstance<PlayerObject> eaglerPlayer = attachment.asEaglerPlayer();
				if (eaglerPlayer != null) {
					IVoiceManagerImpl<PlayerObject> voiceManager = eaglerPlayer.getVoiceManager();
					if (voiceManager != null) {
						voiceManager.handleBackendMessage(message);
					}
				}
			}
		}));
		backendChannelBuilder.add(new MessageChannel<>(EaglerBackendRPCProtocol.CHANNEL_NAME_READY,
				EaglerBackendRPCProtocol.CHANNEL_NAME_READY_MODERN, null));
		return backendChannelBuilder.build();
	}

	public static String getRPCChannel(EaglerXServer<?> server) {
		return server.getConfig().getSettings().isUseModernizedChannelNames()
				? EaglerBackendRPCProtocol.CHANNEL_NAME_MODERN
				: EaglerBackendRPCProtocol.CHANNEL_NAME;
	}

	public static String getReadyChannel(EaglerXServer<?> server) {
		return server.getConfig().getSettings().isUseModernizedChannelNames()
				? EaglerBackendRPCProtocol.CHANNEL_NAME_READY_MODERN
				: EaglerBackendRPCProtocol.CHANNEL_NAME_READY;
	}

	public static String getRPCVoiceChannel(EaglerXServer<?> server) {
		return server.getConfig().getSettings().isUseModernizedChannelNames() ? EaglerVCProtocol.CHANNEL_NAME_MODERN
				: EaglerVCProtocol.CHANNEL_NAME;
	}

}
