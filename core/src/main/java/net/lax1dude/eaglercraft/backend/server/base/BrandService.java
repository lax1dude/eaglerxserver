package net.lax1dude.eaglercraft.backend.server.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandService;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.ISupervisorServiceImpl;

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

	private static class BrandRegistrationEagler112 implements IBrandRegistration {

		@Override
		public UUID getBrandUUID() {
			return BRAND_EAGLERCRAFT_1_12;
		}

		@Override
		public String getBrandDesc() {
			return "Eaglercraft 1.12";
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
			return "EaglercraftX (pre-u37)";
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
			.add(BRAND_EAGLERCRAFT_1_12)
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
		map.put(BRAND_EAGLERCRAFT_1_12, new BrandRegistrationEagler112());
		InputStream brands = BrandService.class.getResourceAsStream("brands.json");
		if(brands != null) {
			JsonObject brandsFile = null;
			try(Reader reader = new InputStreamReader(brands, StandardCharsets.UTF_8)) {
				brandsFile = EaglerXServer.GSON_PRETTY.fromJson(reader, JsonObject.class);
			}catch(IOException | JsonParseException ex) {
				serverIn.logger().error("Could not read brands.json", ex);
				return;
			}
			try {
				for(Entry<String, JsonElement> etr : brandsFile.asMap().entrySet()) {
					UUID uuid = UUID.fromString(etr.getKey());
					JsonObject val = etr.getValue().getAsJsonObject();
					map.put(uuid, new BrandRegistration(uuid, val.get("desc").getAsString(),
							val.get("legacy").getAsBoolean(), val.get("hacked").getAsBoolean()));
				}
			}catch(Exception ex) {
				serverIn.logger().error("brands.json is invalid", ex);
			}
		}
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
			ISupervisorServiceImpl<PlayerObject> supervisorService = server.getSupervisorService();
			if(supervisorService.isSupervisorEnabled() && !supervisorService.shouldIgnoreUUID(playerUUID)) {
				supervisorService.getRemoteOnlyResolver().resolvePlayerBrand(playerUUID, callback);
			}else {
				callback.accept(null);
			}
		}
	}

	@Override
	public void resolvePlayerRegisteredBrand(UUID playerUUID, BiConsumer<UUID, IBrandRegistration> callback) {
		BasePlayerInstance<PlayerObject> player = server.getPlayerByUUID(playerUUID);
		if(player != null) {
			UUID uuid = player.getEaglerBrandUUID();
			callback.accept(uuid, lookupRegisteredBrand(uuid));
		}else {
			ISupervisorServiceImpl<PlayerObject> supervisorService = server.getSupervisorService();
			if(supervisorService.isSupervisorEnabled() && !supervisorService.shouldIgnoreUUID(playerUUID)) {
				supervisorService.getRemoteOnlyResolver().resolvePlayerRegisteredBrand(playerUUID, callback);
			}else {
				callback.accept(null, null);
			}
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
