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

package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.InternUtils;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.PresetSkinPlayer;

public class SkinManagerVanillaOffline<PlayerObject> implements ISkinManagerBase<PlayerObject>, ISkinManagerImpl {

	private final BasePlayerInstance<PlayerObject> player;
	private final IEaglerPlayerSkin oldSkin;
	private IEaglerPlayerSkin skin;
	private IEaglerPlayerCape cape;

	SkinManagerVanillaOffline(BasePlayerInstance<PlayerObject> player) {
		this(player, initDefaultSkin(player.getUniqueId()));
	}

	SkinManagerVanillaOffline(BasePlayerInstance<PlayerObject> player, IEaglerPlayerSkin skin) {
		this.player = player;
		this.oldSkin = skin;
		this.skin = skin;
		this.cape = InternUtils.getPresetCape(0);
	}

	private static IEaglerPlayerSkin initDefaultSkin(UUID uuid) {
		return new PresetSkinPlayer(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(),
				(uuid.hashCode() & 1) != 0 ? 1 : 0);
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public ISkinService<PlayerObject> getSkinService() {
		return player.getEaglerXServer().getSkinService();
	}

	@Override
	public boolean isEaglerPlayer() {
		return false;
	}

	@Override
	public ISkinManagerEagler<PlayerObject> asEaglerPlayer() {
		return null;
	}

	@Override
	public IEaglerPlayerSkin getPlayerSkinIfLoaded() {
		return skin;
	}

	@Override
	public IEaglerPlayerCape getPlayerCapeIfLoaded() {
		return cape;
	}

	@Override
	public void resolvePlayerSkin(Consumer<IEaglerPlayerSkin> callback) {
		callback.accept(skin);
	}

	@Override
	public void resolvePlayerCape(Consumer<IEaglerPlayerCape> callback) {
		callback.accept(cape);
	}

	@Override
	public void resolvePlayerTextures(BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> callback) {
		callback.accept(skin, cape);
	}

	@Override
	public void resolvePlayerSkinKeyed(UUID requester, Consumer<IEaglerPlayerSkin> callback) {
		callback.accept(skin);
	}

	@Override
	public void resolvePlayerCapeKeyed(UUID requester, Consumer<IEaglerPlayerCape> callback) {
		callback.accept(cape);
	}

	@Override
	public void resolvePlayerTexturesKeyed(UUID requester, BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> callback) {
		callback.accept(skin, cape);
	}

	@Override
	public void changePlayerSkin(IEaglerPlayerSkin newSkin, boolean notifyOthers) {
		if (!newSkin.equals(skin)) {
			skin = newSkin;
			if (notifyOthers) {
				SkinManagerHelper.notifyOthers(player, true, false);
			}
		}
	}

	@Override
	public void changePlayerSkin(EnumPresetSkins newSkin, boolean notifyOthers) {
		changePlayerSkin(InternUtils.getPresetSkin(newSkin.getId()), notifyOthers);
	}

	@Override
	public void changePlayerCape(IEaglerPlayerCape newCape, boolean notifyOthers) {
		if (!newCape.equals(cape)) {
			cape = newCape;
			if (notifyOthers) {
				SkinManagerHelper.notifyOthers(player, false, true);
			}
		}
	}

	@Override
	public void changePlayerCape(EnumPresetCapes newCape, boolean notifyOthers) {
		changePlayerCape(InternUtils.getPresetCape(newCape.getId()), notifyOthers);
	}

	@Override
	public void changePlayerTextures(IEaglerPlayerSkin newSkin, IEaglerPlayerCape newCape, boolean notifyOthers) {
		boolean s = !newSkin.equals(skin), c = !newCape.equals(cape);
		if (s || c) {
			if (s) {
				skin = newSkin;
			}
			if (c) {
				cape = newCape;
			}
			if (notifyOthers) {
				SkinManagerHelper.notifyOthers(player, s, c);
			}
		}
	}

	@Override
	public void changePlayerTextures(EnumPresetSkins newSkin, EnumPresetCapes newCape, boolean notifyOthers) {
		changePlayerTextures(InternUtils.getPresetSkin(newSkin.getId()), InternUtils.getPresetCape(newCape.getId()),
				notifyOthers);
	}

	@Override
	public void resetPlayerSkin(boolean notifyOthers) {
		changePlayerSkin(oldSkin, notifyOthers);
	}

	@Override
	public void resetPlayerCape(boolean notifyOthers) {
		changePlayerCape(InternUtils.getPresetCape(0), notifyOthers);
	}

	@Override
	public void resetPlayerTextures(boolean notifyOthers) {
		IEaglerPlayerCape oldCape = InternUtils.getPresetCape(0);
		boolean s = !oldSkin.equals(skin), c = !oldCape.equals(cape);
		if (s || c) {
			if (s) {
				skin = oldSkin;
			}
			if (c) {
				cape = oldCape;
			}
			if (notifyOthers) {
				SkinManagerHelper.notifyOthers(player, s, c);
			}
		}
	}

	@Override
	public void handleSRSkinApply(String value, String signature) {
	}

}
