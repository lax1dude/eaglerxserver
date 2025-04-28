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

package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntProcedure;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorResolver;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class SupervisorResolverAll implements ISupervisorResolver {

	private final SupervisorResolver resolver;
	private final EaglerXServer<?> server;

	public SupervisorResolverAll(SupervisorResolver resolver, EaglerXServer<?> server) {
		this.resolver = resolver;
		this.server = server;
	}

	@Override
	public boolean isPlayerKnown(UUID playerUUID) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		return server.getPlayerByUUID(playerUUID) != null || resolver.isPlayerKnown(playerUUID);
	}

	@Override
	public int getCachedNodeId(UUID playerUUID) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(server.getPlayerByUUID(playerUUID) != null) {
			return server.getSupervisorService().getNodeId();
		}else {
			return resolver.getCachedNodeId(playerUUID);
		}
	}

	@Override
	public void resolvePlayerNodeId(UUID playerUUID, IntProcedure callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		if(server.getPlayerByUUID(playerUUID) != null) {
			callback.apply(server.getSupervisorService().getNodeId());
		}else {
			resolver.resolvePlayerNodeId(playerUUID, callback);
		}
	}

	@Override
	public void resolvePlayerBrand(UUID playerUUID, Consumer<UUID> callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		BasePlayerInstance<?> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			callback.accept(player.getEaglerBrandUUID());
		}else {
			resolver.resolvePlayerBrand(playerUUID, callback);
		}
	}

	@Override
	public void resolvePlayerRegisteredBrand(UUID playerUUID, BiConsumer<UUID, IBrandRegistration> callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		BasePlayerInstance<?> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			callback.accept(player.getEaglerBrandUUID(), player.getEaglerBrandDesc());
		}else {
			resolver.resolvePlayerRegisteredBrand(playerUUID, callback);
		}
	}

	@Override
	public boolean isSkinDownloadEnabled() {
		return true;
	}

	@Override
	public IEaglerPlayerSkin getSkinNotFound(UUID playerUUID) {
		return resolver.getSkinNotFound(playerUUID);
	}

	@Override
	public IEaglerPlayerCape getCapeNotFound() {
		return resolver.getCapeNotFound();
	}

	@Override
	public void resolvePlayerSkin(UUID playerUUID, Consumer<IEaglerPlayerSkin> callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		BasePlayerInstance<?> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			player.getSkinManager().resolvePlayerSkin(callback);
		}else {
			resolver.resolvePlayerSkin(playerUUID, callback);
		}
	}

	@Override
	public void resolvePlayerCape(UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		BasePlayerInstance<?> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			player.getSkinManager().resolvePlayerCape(callback);
		}else {
			resolver.resolvePlayerCape(playerUUID, callback);
		}
	}

	@Override
	public void loadCacheSkinFromURL(String skinURL, EnumSkinModel modelId, Consumer<IEaglerPlayerSkin> callback) {
		resolver.loadCacheSkinFromURL(skinURL, modelId, callback);
	}

	@Override
	public void loadCacheSkinFromURL(String skinURL, int modelIdRaw, Consumer<IEaglerPlayerSkin> callback) {
		resolver.loadCacheSkinFromURL(skinURL, modelIdRaw, callback);
	}

	@Override
	public void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback) {
		resolver.loadCacheCapeFromURL(capeURL, callback);
	}

}
