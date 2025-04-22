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

package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;

public interface ISkinManagerBase<PlayerObject> {

	@Nonnull
	IBasePlayer<PlayerObject> getPlayer();

	@Nonnull
	ISkinService<PlayerObject> getSkinService();

	boolean isEaglerPlayer();

	@Nullable
	ISkinManagerEagler<PlayerObject> asEaglerPlayer();

	@Nullable
	IEaglerPlayerSkin getPlayerSkinIfLoaded();

	@Nullable
	IEaglerPlayerCape getPlayerCapeIfLoaded();

	void resolvePlayerSkin(@Nonnull Consumer<IEaglerPlayerSkin> callback);

	void resolvePlayerCape(@Nonnull Consumer<IEaglerPlayerCape> callback);

	void resolvePlayerTextures(@Nonnull BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> callback);

	default void changePlayerSkin(@Nonnull IEaglerPlayerSkin newSkin) {
		changePlayerSkin(newSkin, true);
	}

	void changePlayerSkin(@Nonnull IEaglerPlayerSkin newSkin, boolean notifyOthers);

	default void changePlayerSkin(@Nonnull EnumPresetSkins newSkin) {
		changePlayerSkin(newSkin, true);
	}

	void changePlayerSkin(@Nonnull EnumPresetSkins newSkin, boolean notifyOthers);

	default void changePlayerCape(@Nonnull IEaglerPlayerCape newCape) {
		changePlayerCape(newCape, true);
	}

	void changePlayerCape(@Nonnull IEaglerPlayerCape newCape, boolean notifyOthers);

	default void changePlayerCape(@Nonnull EnumPresetCapes newCape) {
		changePlayerCape(newCape, true);
	}

	void changePlayerCape(@Nonnull EnumPresetCapes newCape, boolean notifyOthers);

	default void changePlayerTextures(@Nonnull IEaglerPlayerSkin newSkin, @Nonnull IEaglerPlayerCape newCape) {
		changePlayerTextures(newSkin, newCape, true);
	}

	void changePlayerTextures(@Nonnull IEaglerPlayerSkin newSkin, @Nonnull IEaglerPlayerCape newCape, boolean notifyOthers);

	default void changePlayerTextures(@Nonnull EnumPresetSkins newSkin, @Nonnull EnumPresetCapes newCape) {
		changePlayerTextures(newSkin, newCape, true);
	}

	void changePlayerTextures(@Nonnull EnumPresetSkins newSkin, @Nonnull EnumPresetCapes newCape, boolean notifyOthers);

	default void resetPlayerSkin() {
		resetPlayerSkin(true);
	}

	void resetPlayerSkin(boolean notifyOthers);

	default void resetPlayerCape() {
		resetPlayerCape(true);
	}

	void resetPlayerCape(boolean notifyOthers);

	default void resetPlayerTextures() {
		resetPlayerTextures(true);
	}

	void resetPlayerTextures(boolean notifyOthers);

}
