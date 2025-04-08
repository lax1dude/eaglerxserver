package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.EnumSet;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;

class WebViewHelper {

	static Set<EnumWebViewPerms> wrap(Set<net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms> set) {
		if(set == null) return null;
		Set<EnumWebViewPerms> ret = EnumSet.noneOf(EnumWebViewPerms.class);
		if(set.contains(net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms.JAVASCRIPT)) ret.add(EnumWebViewPerms.JAVASCRIPT);
		if(set.contains(net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms.MESSAGE_API)) ret.add(EnumWebViewPerms.MESSAGE_API);
		if(set.contains(net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms.STRICT_CSP)) ret.add(EnumWebViewPerms.STRICT_CSP);
		return ret;
	}

	static Set<net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms> unwrap(
			Set<EnumWebViewPerms> set) {
		if(set == null) return null;
		Set<net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms> ret = EnumSet
				.noneOf(net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms.class);
		if(set.contains(EnumWebViewPerms.JAVASCRIPT)) ret.add(net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms.JAVASCRIPT);
		if(set.contains(EnumWebViewPerms.MESSAGE_API)) ret.add(net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms.MESSAGE_API);
		if(set.contains(EnumWebViewPerms.STRICT_CSP)) ret.add(net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms.STRICT_CSP);
		return ret;
	}

	static SHA1Sum wrap(net.lax1dude.eaglercraft.backend.server.api.SHA1Sum sum) {
		return SHA1Sum.create(sum.getBitsA(), sum.getBitsB(), sum.getBitsC(), sum.getBitsD(), sum.getBitsE());
	}

	static net.lax1dude.eaglercraft.backend.server.api.SHA1Sum unwrap(SHA1Sum sum) {
		return net.lax1dude.eaglercraft.backend.server.api.SHA1Sum.create(sum.getBitsA(), sum.getBitsB(),
				sum.getBitsC(), sum.getBitsD(), sum.getBitsE());
	}

}
