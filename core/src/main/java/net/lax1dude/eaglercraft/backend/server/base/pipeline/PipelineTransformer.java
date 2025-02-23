package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.List;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;

public class PipelineTransformer {

	private final EaglerXServer<?> server;

	public PipelineTransformer(EaglerXServer<?> server) {
		this.server = server;
	}

	public void injectSingleStack(List<IPipelineComponent> components, Channel channel, NettyPipelineData pipelineData) {
		
	}

	public void injectDualStack(List<IPipelineComponent> components, Channel channel, NettyPipelineData pipelineData) {
		
	}

}
