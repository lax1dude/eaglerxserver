package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerNettyPipelineInitializer<PipelineAttachment extends IPipelineData> {

	void initialize(IPlatformNettyPipelineInitializer<PipelineAttachment> initializer);

}
