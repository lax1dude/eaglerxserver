package net.lax1dude.eaglercraft.backend.server.adapter;

import io.netty.channel.ChannelHandler;

public interface IPipelineComponent {

	public static enum EnumPipelineComponent {
		FRAME_DECODER,
		VIA_DECODER,
		MINECRAFT_DECODER,
		FRAME_ENCODER,
		VIA_ENCODER,
		MINECRAFT_ENCODER,
		READ_TIMEOUT_HANDLER,
		HAPROXY_HANDLER,
		BUKKIT_LEGACY_HANDLER,
		BUNGEE_LEGACY_HANDLER,
		BUNGEE_LEGACY_KICK_ENCODER,
		VELOCITY_LEGACY_PING_ENCODER,
		PROTOCOLLIB_INBOUND_INTERCEPTOR,
		PROTOCOLLIB_PROTOCOL_GETTER_NAME,
		PROTOCOLLIB_WIRE_PACKET_ENCODER,
		PROTOCOLIZE_DECODER,
		PROTOCOLIZE_ENCODER,
		INBOUND_PACKET_HANDLER,
		UNIDENTIFIED;
	}

	EnumPipelineComponent getIdentifiedType();

	String getName();

	ChannelHandler getHandle();

}
