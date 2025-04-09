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

package net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt;

import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.*;

public interface EaglerSupervisorHandler {

	default void handleClient(CPacketSvHandshake pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvPing pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvPong pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvProxyBrand pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvProxyStatus pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvRegisterPlayer pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvDropPlayer pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvDropPlayerPartial pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvGetOtherSkin pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvGetSkinByURL pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvOtherSkinPreset pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvOtherSkinCustom pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvOtherSkinURL pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvGetOtherCape pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvGetCapeByURL pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvOtherCapePreset pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvOtherCapeCustom pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvOtherCapeURL pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvGetClientBrandUUID pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvRPCExecuteAll pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvRPCExecuteNode pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvRPCExecutePlayerName pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvRPCExecutePlayerUUID pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvRPCResultSuccess pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleClient(CPacketSvRPCResultFail pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvHandshakeSuccess pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvHandshakeFailure pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvPing pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvPong pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvTotalPlayerCount pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvDropPlayer pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvDropPlayerPartial pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvGetOtherSkin pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvOtherSkinPreset pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvOtherSkinCustom pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvOtherSkinError pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvGetOtherCape pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvOtherCapePreset pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvOtherCapeCustom pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvOtherCapeError pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvPlayerNodeID pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvDropAllPlayers pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvAcceptPlayer pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvRejectPlayer pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvClientBrandError pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvRPCExecute pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvRPCExecuteVoid pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvRPCResultSuccess pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvRPCResultFail pkt) {
		throw new WrongSupervisorPacketException();
	}

	default void handleServer(SPacketSvRPCResultMulti pkt) {
		throw new WrongSupervisorPacketException();
	}

	void handleDisconnected();

}