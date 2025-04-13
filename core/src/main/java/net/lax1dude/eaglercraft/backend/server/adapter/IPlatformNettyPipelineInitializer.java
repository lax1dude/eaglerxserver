package net.lax1dude.eaglercraft.backend.server.adapter;

import java.net.SocketAddress;
import java.util.List;
import java.util.function.Consumer;

import io.netty.channel.Channel;

public interface IPlatformNettyPipelineInitializer<PipelineAttachment extends IPipelineData> {

	List<IPipelineComponent> getPipeline();

	IEaglerXServerListener getListener();

	Channel getChannel();

	Consumer<SocketAddress> realAddressHandle();

	void setAttachment(PipelineAttachment object);

}
