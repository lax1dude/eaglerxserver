/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

@SuppressWarnings("deprecation")
public class HTTPMessageUtils {

	private static final boolean IS_NEW_NAMES;
	private static final boolean IS_CONTAINS_VALUE;

	static {
		boolean b = false;
		try {
			HttpMessage.class.getMethod("protocolVersion");
			b = true;
		} catch (ReflectiveOperationException ex) {
		}
		IS_NEW_NAMES = b;
		try {
			HttpHeaders.class.getMethod("containsValue", CharSequence.class, CharSequence.class, boolean.class);
			b = true;
		} catch (ReflectiveOperationException ex) {
		}
		IS_CONTAINS_VALUE = b;
	}

	public static HttpVersion getProtocolVersion(HttpMessage msg) {
		if (IS_NEW_NAMES) {
			return msg.protocolVersion();
		} else {
			return msg.getProtocolVersion();
		}
	}

	public static String getURI(HttpRequest req) {
		if (IS_NEW_NAMES) {
			return req.uri();
		} else {
			return req.getUri();
		}
	}

	public static HttpMethod getMethod(HttpRequest req) {
		if (IS_NEW_NAMES) {
			return req.method();
		} else {
			return req.getMethod();
		}
	}

	public static String getFirstValue(HttpHeaders headers, CharSequence name) {
		String ret = headers.get(name);
		if (ret != null) {
			int i = ret.indexOf(',');
			if (i != -1) {
				while (i > 0 && Character.isWhitespace(ret.charAt(i - 1))) {
					--i;
				}
				return ret.substring(0, i);
			} else {
				return ret;
			}
		} else {
			return null;
		}
	}

	private static final Splitter SPLITTER_COMMA = Splitter.onPattern("\\s*,\\s*");

	private static final Function<String, Iterable<String>> APPLY_SPLITTER = (str) -> {
		return SPLITTER_COMMA.split(str);
	};

	public static Iterable<String> getAllValues(HttpHeaders headers, CharSequence name) {
		List<String> ret = headers.getAll(name);
		if (!ret.isEmpty()) {
			return Iterables.concat(Iterables.transform(ret, APPLY_SPLITTER));
		} else {
			return ret;
		}
	}

	public static boolean containsValue(HttpHeaders headers, CharSequence name, CharSequence value,
			boolean ignoreCase) {
		if (IS_CONTAINS_VALUE) {
			return headers.containsValue(name, value, ignoreCase);
		} else {
			// Will probably only do this on Spigot 1.8
			String val = value.toString();
			Iterable<String> itr = getAllValues(headers, name);
			if (ignoreCase) {
				for (String str : itr) {
					if (str.equalsIgnoreCase(val)) {
						return true;
					}
				}
			} else {
				for (String str : itr) {
					if (str.equals(val)) {
						return true;
					}
				}
			}
			return false;
		}
	}

}
