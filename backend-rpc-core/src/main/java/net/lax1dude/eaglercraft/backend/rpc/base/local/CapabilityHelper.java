package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilityType;

class CapabilityHelper {

	static EnumCapabilityType wrap(net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType cap) {
		return EnumCapabilityType.getById(cap.getId());
	}

	static net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType unwrap(EnumCapabilityType cap) {
		return net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType.getById(cap.getId());
	}

	static EnumCapabilitySpec wrap(net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec cap) {
		return EnumCapabilitySpec.fromId(cap.getId(), cap.getVer());
	}

	static net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec unwrap(EnumCapabilitySpec cap) {
		return net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec.fromId(cap.getId(), cap.getVer());
	}

}
