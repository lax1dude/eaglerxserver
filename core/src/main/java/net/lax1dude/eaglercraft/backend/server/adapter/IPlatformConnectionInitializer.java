package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformConnectionInitializer<PipelineAttachment, ConnectionAttachment> {

	IPlatformConnection getConnection();

	PipelineAttachment getPipelineAttachment();

	void setConnectionAttachment(ConnectionAttachment attachment);

}
