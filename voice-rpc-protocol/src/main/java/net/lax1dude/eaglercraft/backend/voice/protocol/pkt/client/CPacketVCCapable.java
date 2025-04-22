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

package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class CPacketVCCapable implements EaglerVCPacket {

	public int[] versions;

	public CPacketVCCapable() {
	}

	public CPacketVCCapable(int[] versions) {
		this.versions = versions;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		int cnt = buffer.readUnsignedByte();
		if(cnt > 0) {
			versions = new int[cnt];
			for(int i = 0; i < cnt; ++i) {
				versions[i] = buffer.readUnsignedByte();
			}
		}else {
			versions = null;
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(versions != null) {
			int cnt = versions.length;
			buffer.writeByte(cnt);
			for(int i = 0; i < cnt; ++i) {
				buffer.writeByte(versions[i]);
			}
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		int i = 1;
		if(versions != null) i += versions.length;
		return i;
	}

}
