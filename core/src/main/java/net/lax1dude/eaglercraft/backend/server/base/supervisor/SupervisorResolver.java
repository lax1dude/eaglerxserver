package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

public class SupervisorResolver<PlayerObject> implements ISupervisorResolverImpl {

	private final SupervisorService<PlayerObject> service;

	SupervisorResolver(SupervisorService<PlayerObject> service) {
		this.service = service;
	}

	@Override
	public boolean isPlayerKnown(UUID playerUUID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCachedNodeId(UUID playerUUID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void resolvePlayerNodeId(UUID playerUUID, Consumer<Integer> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolvePlayerBrand(UUID playerUUID, Consumer<UUID> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolvePlayerRegisteredBrand(UUID playerUUID, BiConsumer<UUID, IBrandRegistration> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSkinDownloadEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IEaglerPlayerSkin getSkinNotFound(UUID playerUUID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEaglerPlayerCape getCapeNotFound() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resolvePlayerSkin(UUID playerUUID, Consumer<IEaglerPlayerSkin> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolvePlayerCape(UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadCacheSkinFromURL(String skinURL, EnumSkinModel modelId, Consumer<IEaglerPlayerSkin> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolvePlayerSkinKeyed(UUID requester, UUID playerUUID, Consumer<IEaglerPlayerSkin> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolvePlayerCapeKeyed(UUID requester, UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolvePlayerBrandKeyed(UUID requester, Consumer<UUID> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onForeignSkinReceivedPreset(UUID playerUUID, int presetId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onForeignSkinReceivedCustom(UUID playerUUID, int modelId, byte[] pixels) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onForeignSkinReceivedError(UUID playerUUID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onForeignCapeReceivedPreset(UUID playerUUID, int presetId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onForeignCapeReceivedCustom(UUID playerUUID, byte[] pixels) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onForeignCapeReceivedError(UUID playerUUID) {
		// TODO Auto-generated method stub
		return false;
	}

}
