package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerConnectionInitializer<PipelineAttachment extends IPipelineData, ConnectionAttachment> {

	void initializeConnection(IPlatformConnectionInitializer<PipelineAttachment, ConnectionAttachment> initializer);

}
