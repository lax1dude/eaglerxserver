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

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableMap;

import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.webview.ITemplateLoader;
import net.lax1dude.eaglercraft.backend.server.api.webview.ITranslationProvider;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlobBuilder;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewProvider;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketServerInfoDataChunkV4EAG;

public class WebViewService<PlayerObject> implements IWebViewService<PlayerObject> {

	static final byte[] zeroBytesCompressed = new byte[4];
	static final byte[] sha1ZeroBytesArr = Util.sha1(zeroBytesCompressed);
	static final SHA1Sum sha1ZeroBytes = SHA1Sum.create(sha1ZeroBytesArr);
	static final WebViewBlob zeroByteBlob = new WebViewBlob(sha1ZeroBytes,
			Collections.singletonList(new SPacketServerInfoDataChunkV4EAG(true, 0, sha1ZeroBytesArr,
					zeroBytesCompressed.length, zeroBytesCompressed)));

	private final EaglerXServer<PlayerObject> server;
	private final ConcurrentMap<SHA1Sum, IWebViewBlob> globalBlobs;
	private final ConcurrentMap<String, SHA1Sum> blobAliases;

	private Map<String, String> templateGlobals = Collections.emptyMap();

	public WebViewService(EaglerXServer<PlayerObject> server) {
		this.server = server;
		this.globalBlobs = new ConcurrentHashMap<>();
		this.blobAliases = new ConcurrentHashMap<>();
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	public EaglerXServer<PlayerObject> getEaglerXServer() {
		return server;
	}

	@Override
	public IPauseMenuService<PlayerObject> getPauseMenuService() {
		return server.getPauseMenuService();
	}

	@Override
	public IWebViewProvider<PlayerObject> getDefaultProvider() {
		return WebViewDefaultProvider.instance();
	}

	@Override
	public IWebViewBlobBuilder<OutputStream> createWebViewBlobBuilderStream() {
		return new WebViewBlobBuilder<>(server.getConfig().getPauseMenu().getServerInfoButtonEmbedSendChunkSize()) {
			@Override
			protected OutputStream wrap(OutputStream os) {
				return os;
			}
		};
	}

	@Override
	public IWebViewBlobBuilder<Writer> createWebViewBlobBuilderWriter() {
		return new WebViewBlobBuilder<>(server.getConfig().getPauseMenu().getServerInfoButtonEmbedSendChunkSize()) {
			@Override
			protected Writer wrap(OutputStream os) {
				return new OutputStreamWriter(os, StandardCharsets.UTF_8);
			}
		};
	}

	@Override
	public SHA1Sum registerGlobalBlob(IWebViewBlob blob) {
		if(blob == null) {
			throw new NullPointerException("blob");
		}
		SHA1Sum sum = blob.getHash();
		globalBlobs.put(sum, blob);
		return sum;
	}

	@Override
	public void unregisterGlobalBlob(SHA1Sum sum) {
		if(sum == null) {
			throw new NullPointerException("sum");
		}
		globalBlobs.remove(sum);
	}

	public IWebViewBlob getGlobalBlob(SHA1Sum hash) {
		return globalBlobs.get(hash);
	}

	@Override
	public void registerBlobAlias(String name, SHA1Sum blob) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		if(blob == null) {
			throw new NullPointerException("blob");
		}
		blobAliases.put(name, blob);
	}

	@Override
	public void unregisterBlobAlias(String name) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		blobAliases.remove(name);
	}

	@Override
	public SHA1Sum getBlobFromAlias(String name) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		return blobAliases.get(name);
	}

	@Override
	public Map<String, String> getTemplateGlobals() {
		return templateGlobals;
	}

	@Override
	public void setTemplateGlobal(String key, String value) {
		if(key == null) {
			throw new NullPointerException("key");
		}
		if(value == null) {
			removeTemplateGlobal(key);
			return;
		}
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for(Entry<String, String> etr : templateGlobals.entrySet()) {
			if(!key.equals(etr.getKey())) {
				builder.put(etr);
			}
		}
		builder.put(key, value);
		templateGlobals = builder.build();
	}

	@Override
	public void removeTemplateGlobal(String key) {
		if(key == null) {
			throw new NullPointerException("key");
		}
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for(Entry<String, String> etr : templateGlobals.entrySet()) {
			if(!key.equals(etr.getKey())) {
				builder.put(etr);
			}
		}
		templateGlobals = builder.build();
	}

	@Override
	public ITemplateLoader createTemplateLoader(File baseDir, Map<String, String> variables,
			ITranslationProvider translations, boolean allowEvalMacro) {
		return new TemplateLoader(this, baseDir, variables, translations, allowEvalMacro);
	}

	public WebViewManager<PlayerObject> createWebViewManager(EaglerPlayerInstance<PlayerObject> player) {
		return player.hasCapability(EnumCapabilitySpec.WEBVIEW_V0) ? new WebViewManager<>(player, this) : null;
	}

}
