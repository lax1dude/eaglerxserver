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

package net.lax1dude.eaglercraft.backend.server.base.voice;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCAllowed;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCAnnounce;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCConnectPeer;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCDescription;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCDisconnectPeer;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCICECandidate;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCPlayerList;

public class ServerV1VCProtocolHandler extends ServerVCProtocolHandler {

	public ServerV1VCProtocolHandler(VoiceManagerRemote<?> voiceManager, String[] iceServerStash,
			boolean iceServerOverride) {
		super(voiceManager, iceServerStash, iceServerOverride);
	}

	public void handleServer(SPacketVCAllowed packet) {
		voiceManager.handleBackendSignalPacketAllowed(packet.allowed);
	}

	public void handleServer(SPacketVCPlayerList packet) {
		voiceManager.handleBackendSignalPacketPlayerList(packet.users);
	}

	public void handleServer(SPacketVCAnnounce packet) {
		voiceManager.handleBackendSignalPacketAnnounce(packet.uuidMost, packet.uuidLeast);
	}

	public void handleServer(SPacketVCConnectPeer packet) {
		voiceManager.handleBackendSignalPacketConnectPeer(packet.uuidMost, packet.uuidLeast, packet.offer);
	}

	public void handleServer(SPacketVCDisconnectPeer packet) {
		voiceManager.handleBackendSignalPacketDisconnectPeer(packet.uuidMost, packet.uuidLeast);
	}

	public void handleServer(SPacketVCDescription packet) {
		voiceManager.handleBackendSignalPacketDescription(packet.uuidMost, packet.uuidLeast, packet.desc);
	}

	public void handleServer(SPacketVCICECandidate packet) {
		voiceManager.handleBackendSignalPacketICECandidate(packet.uuidMost, packet.uuidLeast, packet.ice);
	}

}
