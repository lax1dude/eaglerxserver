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

package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IBrandService<PlayerObject> extends IBrandResolver, IBrandRegistry {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	@Nonnull
	default UUID getPlayerBrand(@Nonnull PlayerObject player) {
		IBasePlayer<PlayerObject> basePlayer = getServerAPI().getPlayer(player);
		return basePlayer != null ? basePlayer.getEaglerBrandUUID() : BRAND_VANILLA;
	}

	@Nonnull
	default UUID getPlayerBrand(@Nonnull IBasePlayer<PlayerObject> player) {
		return player.getEaglerBrandUUID();
	}

	@Nullable
	default IBrandRegistration getPlayerRegisteredBrand(@Nonnull PlayerObject player) {
		IBasePlayer<PlayerObject> basePlayer = getServerAPI().getPlayer(player);
		return lookupRegisteredBrand(basePlayer != null ? basePlayer.getEaglerBrandUUID() : BRAND_VANILLA);
	}

}
