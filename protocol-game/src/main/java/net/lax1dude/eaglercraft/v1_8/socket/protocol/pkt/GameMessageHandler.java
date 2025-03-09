package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.*;

/**
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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
public interface GameMessageHandler {

	default void handleClient(CPacketGetOtherCapeEAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketGetOtherSkinEAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketGetSkinByURLEAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketInstallSkinSPEAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketVoiceSignalConnectEAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketVoiceSignalDescEAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketVoiceSignalDisconnectV3EAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketVoiceSignalDisconnectV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketVoiceSignalDisconnectPeerV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketVoiceSignalICEEAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketVoiceSignalRequestEAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketGetOtherClientUUIDV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketRequestServerInfoV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketWebViewMessageV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleClient(CPacketWebViewMessageEnV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketEnableFNAWSkinsEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketOtherCapeCustomEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketOtherCapePresetEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketOtherSkinCustomV3EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketOtherSkinCustomV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketOtherSkinPresetEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketUpdateCertEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketVoiceSignalAllowedEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketVoiceSignalConnectV3EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketVoiceSignalConnectV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketVoiceSignalConnectAnnounceV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketVoiceSignalDescEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketVoiceSignalDisconnectPeerEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketVoiceSignalGlobalEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketVoiceSignalICEEAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketForceClientSkinPresetV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketForceClientSkinCustomV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketSetServerCookieV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketRedirectClientV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketOtherPlayerClientUUIDV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketForceClientCapePresetV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketForceClientCapeCustomV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketInvalidatePlayerCacheV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketUnforceClientV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketCustomizePauseMenuV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketServerInfoDataChunkV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketWebViewMessageV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketNotifIconsRegisterV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketNotifIconsReleaseV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketNotifBadgeShowV4EAG packet) {
		throw new WrongPacketException();
	}

	default void handleServer(SPacketNotifBadgeHideV4EAG packet) {
		throw new WrongPacketException();
	}

}
