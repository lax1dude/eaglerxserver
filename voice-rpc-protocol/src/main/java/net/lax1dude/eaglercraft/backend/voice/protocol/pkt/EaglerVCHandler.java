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

package net.lax1dude.eaglercraft.backend.voice.protocol.pkt;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.*;

public interface EaglerVCHandler {

	default void handleClient(CPacketVCCapable packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCConnect packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCConnectPeer packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCDisconnect packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCDisconnectPeer packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCDescription packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCICECandidate packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCCapable packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCAllowed packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCPlayerList packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCAnnounce packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCConnectPeer packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCDisconnectPeer packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCDescription packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCICECandidate packet) {
		throw new WrongVCPacketException();
	}

}
