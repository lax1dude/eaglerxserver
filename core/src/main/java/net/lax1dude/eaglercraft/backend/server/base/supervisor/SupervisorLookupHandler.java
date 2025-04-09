package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;

public class SupervisorLookupHandler<PlayerObject> {

	private final SupervisorService<PlayerObject> service;
	private final SupervisorConnection connection;

	SupervisorLookupHandler(SupervisorService<PlayerObject> service, SupervisorConnection connection) {
		this.service = service;
		this.connection = connection;
	}

	void handleSupervisorSkinLookup(UUID uuid) {
		// TODO Auto-generated method stub
		
	}

	void handleSupervisorCapeLookup(UUID uuid) {
		// TODO Auto-generated method stub
		
	}

}
