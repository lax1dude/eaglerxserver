package net.lax1dude.eaglercraft.backend.server.base;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandService;

public class BrandService<PlayerObject> implements IBrandService<PlayerObject> {

	private static class BrandRegistrationVanilla implements IBrandRegistration {

		@Override
		public UUID getBrandUUID() {
			return BRAND_VANILLA;
		}

		@Override
		public String getBrandDesc() {
			return "Vanilla";
		}

		@Override
		public boolean isVanillaMinecraft() {
			return true;
		}

		@Override
		public boolean isVanillaEagler() {
			return false;
		}

		@Override
		public boolean isLegacyClient() {
			return false;
		}

		@Override
		public boolean isHackedClient() {
			return false;
		}

	}

	private static class BrandRegistrationEaglerV4 implements IBrandRegistration {

		@Override
		public UUID getBrandUUID() {
			return BRAND_EAGLERCRAFTX_V4;
		}

		@Override
		public String getBrandDesc() {
			return "EaglercraftX";
		}

		@Override
		public boolean isVanillaMinecraft() {
			return false;
		}

		@Override
		public boolean isVanillaEagler() {
			return true;
		}

		@Override
		public boolean isLegacyClient() {
			return false;
		}

		@Override
		public boolean isHackedClient() {
			return false;
		}

	}

	private static class BrandRegistrationEaglerOld implements IBrandRegistration {

		@Override
		public UUID getBrandUUID() {
			return BRAND_EAGLERCRAFTX_LEGACY;
		}

		@Override
		public String getBrandDesc() {
			return "EaglercraftX-pre-u37";
		}

		@Override
		public boolean isVanillaMinecraft() {
			return false;
		}

		@Override
		public boolean isVanillaEagler() {
			return true;
		}

		@Override
		public boolean isLegacyClient() {
			return true;
		}

		@Override
		public boolean isHackedClient() {
			return false;
		}

	}

	private static class BrandRegistration implements IBrandRegistration {

		protected final UUID uuid;
		protected final String desc;
		protected final boolean hacked;
		protected final boolean legacy;
	
		protected BrandRegistration(UUID uuid, String desc, boolean hacked, boolean legacy) {
			this.uuid = uuid;
			this.desc = desc;
			this.hacked = hacked;
			this.legacy = legacy;
		}

		@Override
		public UUID getBrandUUID() {
			return uuid;
		}

		@Override
		public String getBrandDesc() {
			return desc;
		}

		@Override
		public boolean isVanillaMinecraft() {
			return false;
		}

		@Override
		public boolean isVanillaEagler() {
			return false;
		}

		@Override
		public boolean isLegacyClient() {
			return legacy;
		}

		@Override
		public boolean isHackedClient() {
			return hacked;
		}

	}

	private final EaglerXServer<PlayerObject> server;
	private final ReadWriteLock mapLock;
	private final Map<UUID, IBrandRegistration> map;

	private static final Set<UUID> builtinUUIDs = ImmutableSet.<UUID>builder()
			.add(BRAND_VANILLA)
			.add(BRAND_EAGLERCRAFTX_V4)
			.add(BRAND_EAGLERCRAFTX_LEGACY)
			.build();

	private static final Set<UUID> invalidUUIDs = ImmutableSet.<UUID>builder()
			.add(BRAND_VANILLA)
			.add(new UUID(0x6969696969696969L, 0x6969696969696969L))
			.add(new UUID(0xEEEEA64771094C4EL, 0x86E55B81D17E67EBL))
			.build();

	public BrandService(EaglerXServer<PlayerObject> serverIn) {
		server = serverIn;
		mapLock = new ReentrantReadWriteLock();
		map = new HashMap<>();
		map.put(BRAND_VANILLA, new BrandRegistrationVanilla());
		map.put(BRAND_EAGLERCRAFTX_V4, new BrandRegistrationEaglerV4());
		map.put(BRAND_EAGLERCRAFTX_LEGACY, new BrandRegistrationEaglerOld());
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public void resolvePlayerBrand(UUID playerUUID, Consumer<UUID> callback) {
		BasePlayerInstance<PlayerObject> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			callback.accept(player.getEaglerBrandUUID());
		}else {
			callback.accept(null);
			//TODO: supervisor
		}
	}

	@Override
	public void resolvePlayerRegisteredBrand(UUID playerUUID, BiConsumer<UUID, IBrandRegistration> callback) {
		BasePlayerInstance<PlayerObject> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			UUID uuid = player.getEaglerBrandUUID();
			callback.accept(uuid, lookupRegisteredBrand(uuid));
		}else {
			callback.accept(null, null);
			//TODO: supervisor
		}
	}

	@Override
	public UUID getBrandUUIDClient(String brandString) {
		return UUID.nameUUIDFromBytes(("EaglercraftXClient:" + brandString).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public UUID getBrandUUIDClientLegacy(String brandString) {
		return UUID.nameUUIDFromBytes(("EaglercraftXClientOld:" + brandString).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void registerBrand(UUID brandUUID, String brandDesc, boolean hacked, boolean legacy) {
		if(!builtinUUIDs.contains(brandUUID)) {
			IBrandRegistration reg = new BrandRegistration(brandUUID, brandDesc, hacked, legacy);
			mapLock.writeLock().lock();
			try {
				map.put(brandUUID, reg);
			}finally {
				mapLock.writeLock().unlock();
			}
		}
	}

	@Override
	public void unregisterBrand(UUID brandUUID) {
		if(!builtinUUIDs.contains(brandUUID)) {
			mapLock.writeLock().lock();
			try {
				map.remove(brandUUID);
			}finally {
				mapLock.writeLock().unlock();
			}
		}
	}

	@Override
	public IBrandRegistration lookupRegisteredBrand(UUID brandUUID) {
		mapLock.readLock().lock();
		try {
			return map.get(brandUUID);
		}finally {
			mapLock.readLock().unlock();
		}
	}

	public boolean sanitizeUUID(UUID uuid) {
		return !invalidUUIDs.contains(uuid);
	}

}
