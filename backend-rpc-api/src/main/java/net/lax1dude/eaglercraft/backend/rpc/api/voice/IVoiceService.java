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

package net.lax1dude.eaglercraft.backend.rpc.api.voice;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;

public interface IVoiceService<PlayerObject> {

	@Nonnull
	IEaglerXBackendRPC<PlayerObject> getServerAPI();

	@Nullable
	default IVoiceManager<PlayerObject> getVoiceManager(@Nonnull PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getVoiceManager() : null;
	}

	boolean isVoiceEnabled();

	boolean isVoiceEnabledAllWorlds();

	boolean isVoiceEnabledOnWorld(@Nonnull String worldName);

	boolean isSeparateWorldChannels();

	@Nullable
	IVoiceChannel getWorldVoiceChannel(@Nonnull String worldName);

	@Nonnull
	Collection<ICEServerEntry> getICEServers();

	void setICEServers(@Nonnull Collection<ICEServerEntry> servers);

	boolean getOverrideICEServers();

	void setOverrideICEServers(boolean enable);

	@Nonnull
	IVoiceChannel createVoiceChannel();

	@Nonnull
	IVoiceChannel getGlobalVoiceChannel();

	@Nonnull
	IVoiceChannel getDisabledVoiceChannel();

	@Nonnull
	Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(@Nonnull IVoiceChannel channel);

}
