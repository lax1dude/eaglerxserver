package net.lax1dude.eaglercraft.backend.server.adapter;

import io.netty.util.AttributeKey;

public class PipelineAttributes {

	public static final AttributeKey<Object> EAGLER_LISTENER_DATA = AttributeKey.valueOf("$eagler0");
	public static final AttributeKey<Object> EAGLER_PIPELINE_DATA = AttributeKey.valueOf("$eagler1");
	public static final AttributeKey<Object> EAGLER_CONNECTION_DATA = AttributeKey.valueOf("$eagler2");

	public static <T> AttributeKey<T> listenerData() {
		return (AttributeKey<T>) EAGLER_LISTENER_DATA;
	}

	public static <T> AttributeKey<T> pipelineData() {
		return (AttributeKey<T>) EAGLER_PIPELINE_DATA;
	}

	public static <T> AttributeKey<T> connectionData() {
		return (AttributeKey<T>) EAGLER_CONNECTION_DATA;
	}

}
