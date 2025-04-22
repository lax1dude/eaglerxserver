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

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCResponseTypeError;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms;

public class ServerV2RPCProtocolHandler extends ServerV1RPCProtocolHandler {

	public ServerV2RPCProtocolHandler(BasePlayerRPCContext<?> rpcContext) {
		super(rpcContext);
	}

	public void handleClient(CPacketRPCDisabled packet) {
		throw wrongPacket();
	}

	public void handleClient(CPacketRPCRequestPlayerInfo packet) {
		try {
			switch(packet.requestType) {
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS:
				throw wrongPacket();
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_TEXTURE_DATA:
				rpcContext.handleRequestTextureData(packet.requestID);
				return;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_DATA:
				rpcContext.handleRequestBrandData(packet.requestID);
				return;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_MINECRAFT_BRAND:
				rpcContext.handleRequestMinecraftBrand(packet.requestID);
				return;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_AUTH_USERNAME:
				rpcContext.handleRequestAuthUsername(packet.requestID);
				return;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS_V2:
				rpcContext.handleRequestWebViewStatusV2(packet.requestID);
				return;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_HEADER_HOST:
				rpcContext.handleRequestHeader(packet.requestID, EnumWebSocketHeader.HEADER_HOST);
				return;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_HEADER_COOKIE:
				rpcContext.handleRequestHeader(packet.requestID, EnumWebSocketHeader.HEADER_COOKIE);
				return;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_HEADER_AUTHORIZATION:
				rpcContext.handleRequestHeader(packet.requestID, EnumWebSocketHeader.HEADER_AUTHORIZATION);
				return;
			case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REQUEST_PATH:
				rpcContext.handleRequestPath(packet.requestID);
				return;
			}
		}catch(Exception ex) {
			rpcContext.sendRPCPacket(new SPacketRPCResponseTypeError(packet.requestID, ex.toString()));
			rpcContext.manager().handleException(ex);
			return;
		}
		super.handleClient(packet);
	}

	public void handleClient(CPacketRPCSetPlayerSkinPresetV2 packet) {
		rpcContext.handleSetPlayerSkinPreset(packet.presetSkinId, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerCapePresetV2 packet) {
		rpcContext.handleSetPlayerCapePreset(packet.presetCapeId, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCInjectRawBinaryFrameV2 packet) {
		rpcContext.handleInjectRawBinaryFrame(packet.messageData);
	}

	public void handleClient(CPacketRPCSetPlayerTexturesV2 packet) {
		rpcContext.handleSetPlayerTextures(packet.texturesPacket, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerTexturesPresetV2 packet) {
		rpcContext.handleSetPlayerTexturesPreset(packet.presetSkinId, packet.presetCapeId, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCDisplayWebViewURLV2 packet) {
		rpcContext.handleDisplayWebViewURL(packet.embedTitle, packet.embedURL, EnumWebViewPerms.fromBits(packet.flags));
	}

	public void handleClient(CPacketRPCDisplayWebViewBlobV2 packet) {
		rpcContext.handleDisplayWebViewBlob(packet.embedTitle, SHA1Sum.create(packet.embedHash), EnumWebViewPerms.fromBits(packet.flags));
	}

	public void handleClient(CPacketRPCDisplayWebViewAliasV2 packet) {
		rpcContext.handleDisplayWebViewAlias(packet.embedTitle, packet.embedName, EnumWebViewPerms.fromBits(packet.flags));
	}

	public void handleClient(CPacketRPCGetSkinByURLV2 packet) {
		rpcContext.handleRequestGetSkinByURL(packet.requestID, packet.skinURL);
	}

	public void handleClient(CPacketRPCGetCapeByURLV2 packet) {
		rpcContext.handleRequestGetCapeByURL(packet.requestID, packet.capeURL);
	}

}
