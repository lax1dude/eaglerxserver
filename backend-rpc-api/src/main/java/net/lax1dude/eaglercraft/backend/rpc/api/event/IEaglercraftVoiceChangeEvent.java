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

package net.lax1dude.eaglercraft.backend.rpc.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public interface IEaglercraftVoiceChangeEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	@Nonnull
	default IVoiceManager<PlayerObject> getVoiceManager() {
		return getPlayer().getVoiceManager();
	}

	@Nonnull
	default IVoiceService<PlayerObject> getVoiceService() {
		return getServerAPI().getVoiceService();
	}

	@Nonnull
	EnumVoiceState getVoiceStateOld();

	@Nonnull
	EnumVoiceState getVoiceStateNew();

}
