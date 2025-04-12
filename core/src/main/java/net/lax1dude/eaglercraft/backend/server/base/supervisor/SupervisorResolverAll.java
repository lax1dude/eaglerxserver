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
		return server.getPlayerByUUID(playerUUID) != null || resolver.isPlayerKnown(playerUUID);
	}

	@Override
	public int getCachedNodeId(UUID playerUUID) {
		if(server.getPlayerByUUID(playerUUID) != null) {
			return server.getSupervisorService().getNodeId();
		}else {
			return resolver.getCachedNodeId(playerUUID);
		}
	}

	@Override
	public void resolvePlayerNodeId(UUID playerUUID, IntProcedure callback) {
		if(server.getPlayerByUUID(playerUUID) != null) {
			callback.apply(server.getSupervisorService().getNodeId());
		}else {
			resolver.resolvePlayerNodeId(playerUUID, callback);
		}
	}

	@Override
	public void resolvePlayerBrand(UUID playerUUID, Consumer<UUID> callback) {
		BasePlayerInstance<?> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			callback.accept(player.getEaglerBrandUUID());
		}else {
			resolver.resolvePlayerBrand(playerUUID, callback);
		}
	}

	@Override
	public void resolvePlayerRegisteredBrand(UUID playerUUID, BiConsumer<UUID, IBrandRegistration> callback) {
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
		BasePlayerInstance<?> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			player.getSkinManager().resolvePlayerSkin(callback);
		}else {
			resolver.resolvePlayerSkin(playerUUID, callback);
		}
	}

	@Override
	public void resolvePlayerCape(UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
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
	public void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback) {
		resolver.loadCacheCapeFromURL(capeURL, callback);
	}

}
