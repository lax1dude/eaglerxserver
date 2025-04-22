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

package net.lax1dude.eaglercraft.backend.server.base.webview;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewProvider;

class WebViewDefaultProvider implements IWebViewProvider<Object> {

	private static final WebViewDefaultProvider INSTANCE = new WebViewDefaultProvider();

	@SuppressWarnings("unchecked")
	static <PlayerObject> IWebViewProvider<PlayerObject> instance() {
		return (IWebViewProvider<PlayerObject>) INSTANCE;
	}

	private WebViewDefaultProvider() {
	}

	@Override
	public boolean isChannelAllowed(IWebViewManager<Object> manager) {
		return ((WebViewManager<?>)manager).isChannelAllowedDefault();
	}

	@Override
	public boolean isRequestAllowed(IWebViewManager<Object> manager) {
		return ((WebViewManager<?>)manager).isRequestAllowedDefault();
	}

	@Override
	public void handleRequest(IWebViewManager<Object> manager, SHA1Sum hash, Consumer<IWebViewBlob> callback) {
		((WebViewManager<?>)manager).handleRequestDefault(hash, callback);
	}

	@Override
	public SHA1Sum handleAlias(IWebViewManager<Object> manager, String aliasName) {
		return ((WebViewManager<?>)manager).handleAliasDefault(aliasName);
	}

}
