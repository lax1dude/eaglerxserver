package net.lax1dude.eaglercraft.backend.server.base;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;

public class EaglerLoginStateAdapter extends EaglerPendingStateAdapter implements IEaglerLoginConnection {

	EaglerLoginStateAdapter(NettyPipelineData pipelineData) {
		super(pipelineData);
	}

	@Override
	public IEaglerLoginConnection asEaglerPlayer() {
		return this;
	}

	@Override
	public UUID getUniqueId() {
		return pipelineData.uuid;
	}

	@Override
	public String getUsername() {
		return pipelineData.username;
	}

	@Override
	public boolean isOnlineMode() {
		return false;
	}

	@Override
	public boolean isCookieSupported() {
		return pipelineData.cookieSupport;
	}

	@Override
	public boolean isCookieEnabled() {
		return pipelineData.cookieEnabled;
	}

	@Override
	public byte[] getCookieData() {
		return pipelineData.cookieData;
	}

	@Override
	public boolean hasCapability(EnumCapabilitySpec capability) {
		return CapabilityBits.hasCapability(pipelineData.acceptedCapabilitiesMask,
				pipelineData.acceptedCapabilitiesVers, capability.getId(), capability.getVer());
	}

	@Override
	public int getCapability(EnumCapabilityType capability) {
		return CapabilityBits.getCapability(pipelineData.acceptedCapabilitiesMask,
				pipelineData.acceptedCapabilitiesVers, capability.getId());
	}

	@Override
	public boolean hasExtendedCapability(UUID extendedCapability, int version) {
		if(extendedCapability == null) {
			throw new NullPointerException("extendedCapability");
		}
		Byte b = pipelineData.acceptedExtendedCapabilities.get(extendedCapability);
		return b != null && (b.byteValue() & 0xFF) >= version;
	}

	@Override
	public int getExtendedCapability(UUID extendedCapability) {
		if(extendedCapability == null) {
			throw new NullPointerException("extendedCapability");
		}
		Byte b = pipelineData.acceptedExtendedCapabilities.get(extendedCapability);
		return b != null ? (b.byteValue() & 0xFF) : -1;
	}

}
