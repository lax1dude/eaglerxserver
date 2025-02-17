package net.lax1dude.eaglercraft.backend.server.adapter;

import java.util.List;

import io.netty.channel.Channel;

public interface IPlatformNettyPipelineInitializer<PipelineAttachment> {

	List<IPipelineComponent> getPipeline();

	IEaglerXServerListener getListener();

	Channel getChannel();

	void setAttachment(PipelineAttachment object);

}
