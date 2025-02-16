package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerNettyPipelineInitializer<PipelineAttachment> {

	void initialize(IPlatformNettyPipelineInitializer<PipelineAttachment> initializer);

}
