package net.lax1dude.eaglercraft.backend.server.base.supervisor;

public interface ISupervisorExpiring {

	long expiresAt();

	void expire();

}
