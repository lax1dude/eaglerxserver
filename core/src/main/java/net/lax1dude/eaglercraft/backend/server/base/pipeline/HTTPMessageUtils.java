package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

@SuppressWarnings("deprecation")
public class HTTPMessageUtils {

	private static final boolean IS_NEW_NAMES;

	static {
		boolean b = false;
		try {
			HttpMessage.class.getMethod("protocolVersion");
			b = true;
		}catch(ReflectiveOperationException ex) {
		}
		IS_NEW_NAMES = b;
	}

	public static HttpVersion getProtocolVersion(HttpMessage msg) {
		if(IS_NEW_NAMES) {
			return msg.protocolVersion();
		}else {
			return msg.getProtocolVersion();
		}
	}

	public static String getURI(HttpRequest req) {
		if(IS_NEW_NAMES) {
			return req.uri();
		}else {
			return req.getUri();
		}
	}

	public static HttpMethod getMethod(HttpRequest req) {
		if(IS_NEW_NAMES) {
			return req.method();
		}else {
			return req.getMethod();
		}
	}

}
