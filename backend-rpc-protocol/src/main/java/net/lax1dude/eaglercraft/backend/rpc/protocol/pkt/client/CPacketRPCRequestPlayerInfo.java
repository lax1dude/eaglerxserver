/*
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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCRequestPlayerInfo implements EaglerBackendRPCPacket {

	public static final int REQUEST_PLAYER_REAL_UUID = 0;
	public static final int REQUEST_PLAYER_REAL_IP = 1;
	public static final int REQUEST_PLAYER_ORIGIN = 2;
	public static final int REQUEST_PLAYER_USER_AGENT = 3;
	public static final int REQUEST_PLAYER_SKIN_DATA = 4;
	public static final int REQUEST_PLAYER_CAPE_DATA = 5;
	public static final int REQUEST_PLAYER_COOKIE = 6;
	public static final int REQUEST_PLAYER_CLIENT_BRAND_STR = 7;
	public static final int REQUEST_PLAYER_CLIENT_VERSION_STR = 8;
	public static final int REQUEST_PLAYER_CLIENT_BRAND_VERSION_STR = 9;
	public static final int REQUEST_PLAYER_CLIENT_BRAND_UUID = 10;
	public static final int REQUEST_PLAYER_CLIENT_VOICE_STATUS = 11;
	public static final int REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS = 12;

	public int requestID;
	public int requestType;

	public CPacketRPCRequestPlayerInfo() {
	}

	public CPacketRPCRequestPlayerInfo(int requestID, int requestType) {
		this.requestID = requestID;
		this.requestType = requestType;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		requestType = buffer.readUnsignedByte();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		buffer.writeByte(requestType);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 5;
	}

}