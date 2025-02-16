package net.lax1dude.eaglercraft.backend.server.adapter;

import java.util.List;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public class PipelineComponent {

	public final String name;
	public final ChannelHandler object;

	public PipelineComponent(String name, ChannelHandler object) {
		this.name = name;
		this.object = object;
	}

	public static void restorePipeline(ChannelPipeline channel, List<PipelineComponent> pipelineComponents) {
		for(PipelineComponent comp : pipelineComponents) {
			channel.addFirst(comp.name, comp.object);
		}
	}

}
