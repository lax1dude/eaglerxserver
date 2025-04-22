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
