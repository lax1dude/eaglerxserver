package net.lax1dude.eaglercraft.backend.server.base.webview;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.webview.ITemplateLoader;
import net.lax1dude.eaglercraft.backend.server.api.webview.ITranslationProvider;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewProvider;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketServerInfoDataChunkV4EAG;

public class WebViewService<PlayerObject> implements IWebViewService<PlayerObject> {

	private static final byte[] sha1ZeroBytesArr = Util.sha1(Util.ZERO_BYTES);
	private static final SHA1Sum sha1ZeroBytes = SHA1Sum.create(sha1ZeroBytesArr);
	private static final WebViewBlob zeroByteBlob = new WebViewBlob(sha1ZeroBytes, Collections
			.singletonList(new SPacketServerInfoDataChunkV4EAG(true, 0, sha1ZeroBytesArr, 0, Util.ZERO_BYTES)));

	private final EaglerXServer<PlayerObject> server;
	private final ConcurrentMap<SHA1Sum, IWebViewBlob> globalBlobs;

	private Map<String, String> templateGlobals = Collections.emptyMap();

	public WebViewService(EaglerXServer<PlayerObject> server) {
		this.server = server;
		this.globalBlobs = new ConcurrentHashMap<>();
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
	public IWebViewBlob createWebViewBlob(byte[] bytesIn) {
		int len = bytesIn.length;
		if (bytesIn.length == 0) {
			return zeroByteBlob;
		}
		if(bytesIn.length > 0x2000000) {
			throw new IndexOutOfBoundsException("Blob is too large: <" + len + " bytes>");
		}
		byte[] hash = Util.sha1(bytesIn);
		SHA1Sum sumObj = SHA1Sum.create(hash);
		int chunkSize = server.getConfig().getPauseMenu().getServerInfoButtonEmbedSendChunkSize();
		int cnt = (bytesIn.length + (chunkSize - 1)) / chunkSize;
		ImmutableList.Builder<SPacketServerInfoDataChunkV4EAG> builder = ImmutableList.builderWithExpectedSize(cnt);
		int i = 0;
		int k = 0;
		while(i < len) {
			int j = Math.min(len - i, chunkSize);
			byte[] bytes = new byte[j];
			System.arraycopy(bytesIn, i, bytes, 0, bytes.length);
			builder.add(new SPacketServerInfoDataChunkV4EAG(false, k++, hash, len, bytes));
			i += j;
		}
		List<SPacketServerInfoDataChunkV4EAG> ret = builder.build();
		ret.get(ret.size() - 1).lastChunk = true;
		return new WebViewBlob(sumObj, ret);
	}

	@Override
	public IWebViewBlob createWebViewBlob(InputStream inputStream) throws IOException {
		ImmutableList.Builder<SPacketServerInfoDataChunkV4EAG> builder = ImmutableList.builder();
		int chunkSize = server.getConfig().getPauseMenu().getServerInfoButtonEmbedSendChunkSize();
		MessageDigest digest = Util.sha1();
		int totalLen = 0;
		int i = 0;
		int k = 0;
		byte[] tmp;
		do {
			tmp = new byte[chunkSize];
			i = ByteStreams.read(inputStream, tmp, 0, chunkSize);
			if(i > 0) {
				builder.add(new SPacketServerInfoDataChunkV4EAG(false, k++, null, 0, i == chunkSize ? tmp : Arrays.copyOf(tmp, i)));
				digest.update(tmp, 0, i);
				totalLen += i;
			}
		}while(i == chunkSize);
		byte[] hash = digest.digest();
		List<SPacketServerInfoDataChunkV4EAG> ret = builder.build();
		int chunkCount = ret.size();
		if(chunkCount == 0) {
			return zeroByteBlob;
		}
		for(int l = 0; l < chunkCount; ++l) {
			SPacketServerInfoDataChunkV4EAG pkt = ret.get(l);
			pkt.finalHash = hash;
			pkt.finalSize = totalLen;
			if(l == chunkCount - 1) {
				pkt.lastChunk = true;
			}
		}
		return new WebViewBlob(SHA1Sum.create(hash), ret);
	}

	@Override
	public SHA1Sum registerGlobalBlob(IWebViewBlob blob) {
		SHA1Sum sum = blob.getHash();
		globalBlobs.put(sum, blob);
		return sum;
	}

	@Override
	public void unregisterGlobalBlob(SHA1Sum sum) {
		globalBlobs.remove(sum);
	}

	public IWebViewBlob getGlobalBlob(SHA1Sum hash) {
		return globalBlobs.get(hash);
	}

	@Override
	public Map<String, String> getTemplateGlobals() {
		return templateGlobals;
	}

	@Override
	public void setTemplateGlobal(String key, String value) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		builder.putAll(templateGlobals);
		builder.put(key, value);
		templateGlobals = builder.buildKeepingLast();
	}

	@Override
	public void removeTemplateGlobal(String key) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for(Entry<String, String> etr : templateGlobals.entrySet()) {
			if(!key.equals(etr.getKey())) {
				builder.put(etr);
			}
		}
		templateGlobals = builder.buildKeepingLast();
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
