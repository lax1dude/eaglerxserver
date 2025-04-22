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

package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public class VoiceManagerLocal<PlayerObject> implements IVoiceManager<PlayerObject> {

	private final VoiceServiceLocal<PlayerObject> service;
	private final PlayerInstanceLocal<PlayerObject> player;
	private final net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager<PlayerObject> delegate;

	VoiceManagerLocal(VoiceServiceLocal<PlayerObject> service, PlayerInstanceLocal<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager<PlayerObject> delegate) {
		this.service = service;
		this.player = player;
		this.delegate = delegate;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IVoiceService<PlayerObject> getVoiceService() {
		return service;
	}

	@Override
	public EnumVoiceState getVoiceState() {
		return VoiceChannelHelper.wrap(delegate.getVoiceState());
	}

	@Override
	public IVoiceChannel getVoiceChannel() {
		return service.wrapConst(delegate.getVoiceChannel());
	}

	@Override
	public void setVoiceChannel(IVoiceChannel channel) {
		delegate.setVoiceChannel(VoiceChannelHelper.unwrap(channel));
	}

	@Override
	public boolean isWorldManaged() {
		return delegate.isServerManaged();
	}

	@Override
	public void setWorldManaged(boolean managed) {
		delegate.setServerManaged(managed);
	}

}
