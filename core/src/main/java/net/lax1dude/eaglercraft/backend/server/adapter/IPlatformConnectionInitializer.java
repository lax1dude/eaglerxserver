package net.lax1dude.eaglercraft.backend.server.adapter;

import java.util.UUID;

public interface IPlatformConnectionInitializer<PipelineAttachment extends IPipelineData, ConnectionAttachment> {

	IPlatformConnection getConnection();

	PipelineAttachment getPipelineAttachment();

	void setConnectionAttachment(ConnectionAttachment attachment);

	void setUniqueId(UUID uuid);

	void setTexturesProperty(String propertyValue, String propertySignature);

	void setEaglerPlayerProperty(boolean enable);

}
