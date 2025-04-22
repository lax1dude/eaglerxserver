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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.*;

public class BackendV1VCProtocolHandler extends BackendVCProtocolHandler {

	public BackendV1VCProtocolHandler(VoiceManagerRemote<?> voiceManager) {
		super(voiceManager);
	}

	public void handleClient(CPacketVCConnect packet) {
		voiceManager.handlePlayerSignalPacketTypeConnect();
	}

	public void handleClient(CPacketVCConnectPeer packet) {
		voiceManager.handlePlayerSignalPacketTypeRequest(packet.uuidMost, packet.uuidLeast);
	}

	public void handleClient(CPacketVCDisconnect packet) {
		voiceManager.handlePlayerSignalPacketTypeDisconnect();
	}

	public void handleClient(CPacketVCDisconnectPeer packet) {
		voiceManager.handlePlayerSignalPacketTypeDisconnectPeer(packet.uuidMost, packet.uuidLeast);
	}

	public void handleClient(CPacketVCDescription packet) {
		voiceManager.handlePlayerSignalPacketTypeDesc(packet.uuidMost, packet.uuidLeast, packet.desc);
	}

	public void handleClient(CPacketVCICECandidate packet) {
		voiceManager.handlePlayerSignalPacketTypeICE(packet.uuidMost, packet.uuidLeast, packet.ice);
	}

}
