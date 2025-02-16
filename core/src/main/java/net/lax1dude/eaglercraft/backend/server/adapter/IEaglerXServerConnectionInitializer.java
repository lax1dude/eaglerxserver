package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerConnectionInitializer<PipelineAttachment, ConnectionAttachment> {

	void initializeConnection(IPlatformConnectionInitializer<PipelineAttachment, ConnectionAttachment> initializer);

	void destroyConnection(IPlatformConnection connection);

}
