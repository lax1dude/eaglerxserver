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

import java.util.Collection;
import java.util.Collections;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class VoiceServiceDisabled<PlayerObject> implements IVoiceServiceImpl<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;

	public VoiceServiceDisabled(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isBackendRelayMode() {
		return false;
	}

	@Override
	public boolean isVoiceEnabled() {
		return false;
	}

	@Override
	public boolean isVoiceEnabledAllServers() {
		return false;
	}

	@Override
	public boolean isVoiceEnabledOnServer(String serverName) {
		if(serverName == null) {
			throw new NullPointerException("serverName");
		}
		return false;
	}

	@Override
	public boolean isSeparateServerChannels() {
		return false;
	}

	@Override
	public Collection<ICEServerEntry> getICEServers() {
		return Collections.emptyList();
	}

	@Override
	public void setICEServers(Collection<ICEServerEntry> servers) {
		if(servers == null) {
			throw new NullPointerException("servers");
		}
	}

	@Override
	public IVoiceChannel createVoiceChannel() {
		throw disabledError();
	}

	@Override
	public IVoiceChannel getGlobalVoiceChannel() {
		throw disabledError();
	}

	@Override
	public IVoiceChannel getServerVoiceChannel(String serverName) {
		if(serverName == null) {
			throw new NullPointerException("serverName");
		}
		throw disabledError();
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		throw disabledError();
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		if(channel == null) {
			throw new NullPointerException("channel");
		}
		throw disabledError();
	}

	@Override
	public IVoiceManagerImpl<PlayerObject> createVoiceManager(EaglerPlayerInstance<PlayerObject> player) {
		return null;
	}

	private static RuntimeException disabledError() {
		return new IllegalStateException("Voice service is disabled!");
	}

}
