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

package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebConfig.ConfigDataMIMEType;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebConfig.ConfigDataSettings;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.PathProcessor.RedirectDirException;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.ResponseCache.ResponseLoader;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreflightContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext.IContextPromise;

public class EaglerWebHandler {

	private final ResponseCache responseCache;
	private final Map<IEaglerListenerInfo, ListenerContext> listeners;
	private final ListenerContext defaultListener;
	private final boolean enableCORS;
	private final DefaultHandlers defaults;
	private final int totalIndexed;

	private static class ListenerContext {

		protected final IndexNode root;
		protected final List<String> pageIndexNames;
		protected final ResponseCacheKey page404;
		protected final ResponseCacheKey page429;
		protected final ResponseCacheKey page500;
		protected final boolean autoindex;
		protected final DateFormat dateformat;

		protected ListenerContext(IndexNode root, List<String> pageIndexNames, ResponseCacheKey page404,
				ResponseCacheKey page429, ResponseCacheKey page500, boolean autoindex, DateFormat dateformat) {
			this.root = root;
			this.pageIndexNames = pageIndexNames;
			this.page404 = page404;
			this.page429 = page429;
			this.page500 = page500;
			this.autoindex = autoindex;
			this.dateformat = dateformat;
		}

	}

	public static EaglerWebHandler build(EaglerWeb<?> eaglerWeb) throws IOException {
		EaglerWebConfig config = eaglerWeb.getConfig();
		ResponseCacheBuilder cacheBuilder = new ResponseCacheBuilder(config.getMemoryCacheExpiresAfter(),
				config.getMemoryCacheMaxFiles(), config.getFileIOThreadCount(), eaglerWeb.logger(), (f) -> {
			String name = f.getName();
			int i = name.lastIndexOf('.');
			if (i != -1) {
				return config.getMimeTypes().getOrDefault(name.substring(i + 1).toLowerCase(Locale.US),
						EaglerWebConfig.DEFAULT_MIME);
			}else {
				return EaglerWebConfig.DEFAULT_MIME;
			}
		});
		ImmutableMap.Builder<IEaglerListenerInfo, ListenerContext> builder = ImmutableMap.builder();
		ListenerContext defaultListener = null;
		Map<File, IndexNodeFolder> documentRoots = new HashMap<>();
		int[] counter = new int[] { 0 };
		ConfigDataSettings defaultSettings = config.getDefaultSettings();
		if(defaultSettings != null) {
			defaultListener = buildContext(defaultSettings, cacheBuilder, documentRoots, counter);
		}
		for(Map.Entry<String, ConfigDataSettings> etr : config.getSettings().entrySet()) {
			IEaglerListenerInfo listenerInfo = eaglerWeb.getServer().getListenerByName(etr.getKey());
			if(listenerInfo != null) {
				builder.put(listenerInfo, buildContext(etr.getValue(), cacheBuilder, documentRoots, counter));
			}else {
				eaglerWeb.logger().error("Listener does not exist: " + etr.getKey());
			}
		}
		return new EaglerWebHandler(cacheBuilder.build(), builder.build(), defaultListener, config.getEnableCORS(),
				eaglerWeb.getDefaultHandlers(), counter[0]);
	}

	private static ListenerContext buildContext(ConfigDataSettings settings, ResponseCacheBuilder cacheBuilder,
			Map<File, IndexNodeFolder> documentRoots, int[] counter) throws IOException {
		return new ListenerContext(index(documentRoots, settings.getRootFolder(), cacheBuilder, counter),
				settings.getPageIndexNames(),
				settings.getPage404NotFound() != null ? cacheBuilder.createEntry(settings.getPage404NotFound()) : null,
				settings.getPage429RateLimit() != null ? cacheBuilder.createEntry(settings.getPage429RateLimit()) : null,
				settings.getPage500InternalError() != null ? cacheBuilder.createEntry(settings.getPage500InternalError()) : null,
				settings.isEnableAutoIndex(),
				settings.getDateFormat() != null ? new SimpleDateFormat(settings.getDateFormat()) : null);
	}

	private static IndexNodeFolder index(Map<File, IndexNodeFolder> documentRoots, File file,
			ResponseCacheBuilder cacheBuilder, int[] counter) throws IOException {
		IndexNodeFolder ret = documentRoots.get(file);
		if(ret == null) {
			documentRoots.put(file, ret = indexDir(file, cacheBuilder, counter));
		}
		return ret;
	}

	private static IndexNodeFolder indexDir(File file, ResponseCacheBuilder cacheBuilder, int[] counter) throws IOException {
		ImmutableMap.Builder<String, IndexNode> directoryIndex = ImmutableMap.builder();
		File[] files = file.listFiles();
		if(files == null) {
			throw new IOException("Could not list directory: " + file.getAbsolutePath());
		}
		for(File child : files) {
			if(child.isDirectory()) {
				IndexNodeFolder folder = indexDir(child, cacheBuilder, counter);
				if(!folder.isEmpty()) {
					directoryIndex.put(child.getName(), folder);
				}
			}else if(child.isFile()) {
				directoryIndex.put(child.getName(), new IndexNodeFile(cacheBuilder.createEntry(child)));
				++counter[0];
			}
		}
		Map<String, IndexNode> map = directoryIndex.build();
		IndexNodeFolder ret = new IndexNodeFolder(file.lastModified(), file.getName(), map);
		for(IndexNode node : map.values()) {
			node.parent = ret;
		}
		return ret;
	}

	private EaglerWebHandler(ResponseCache responseCache, Map<IEaglerListenerInfo, ListenerContext> listeners,
			ListenerContext defaultListener, boolean enableCORS, DefaultHandlers defaults, int totalIndexed) {
		this.responseCache = responseCache;
		this.listeners = listeners;
		this.defaultListener = defaultListener;
		this.enableCORS = enableCORS;
		this.defaults = defaults;
		this.totalIndexed = totalIndexed;
	}

	private ListenerContext getListenerContext(IRequestContext requestContext) {
		IEaglerListenerInfo listener = requestContext.getListener();
		ListenerContext ctx = listeners.get(listener);
		if(ctx == null) {
			ctx = defaultListener;
		}
		return ctx;
	}

	private ResponseCacheKey resolvePath(IndexNode root, List<String> indexName, boolean autoIndex, String path) throws RedirectDirException {
		return (new PathProcessor()).find(path, indexName, autoIndex, root);
	}

	public void handleRequest(IRequestContext requestContext) {
		ListenerContext ctx = getListenerContext(requestContext);
		eagler: if(ctx != null) {
			ResponseCacheKey cacheKey;
			try {
				cacheKey = resolvePath(ctx.root, ctx.pageIndexNames, ctx.autoindex, requestContext.getPath());
			} catch (RedirectDirException e) {
				if(e.autoIndex != null) {
					defaults.handleAutoIndex(requestContext, ctx.dateformat, e.autoIndex);
					return;
				}
				requestContext.setResponseCode(308);
				addCORSHeader(requestContext);
				requestContext.setResponseBodyEmpty();
				String path = requestContext.getPath();
				if(path.endsWith("/")) {
					int i = path.length();
					while(i > 0 && path.charAt(i - 1) == '/') {
						--i;
					}
					requestContext.addResponseHeader("location", path.substring(0, i));
				}else {
					requestContext.addResponseHeader("location", path + "/");
				}
				return;
			}
			final int code;
			if(cacheKey == null) {
				cacheKey = ctx.page404;
				if(cacheKey == null) {
					break eagler;
				}
				code = 404;
			}else {
				code = 200;
			}
			ResponseLoader loader = responseCache.loadResponse(cacheKey);
			byte[] data = loader.tryGetResponse();
			if(data != null) {
				if(data == ResponseCache.ERROR) {
					break eagler;
				}
				completeRequest(requestContext, code, cacheKey.getType(), data);
			}else {
				IContextPromise promise = requestContext.suspendContext();
				ConfigDataMIMEType cacheKeyType = cacheKey.getType();
				loader.loadResponse((data0) -> {
					if(data0 != ResponseCache.ERROR) {
						completeRequest(requestContext, code, cacheKeyType, data0);
						promise.complete();
					}else {
						try {
							defaults.handle404(requestContext);
						}catch(Exception ex) {
							promise.complete(ex);
							return;
						}
						promise.complete();
					}
				});
			}
			return;
		}
		defaults.handle404(requestContext);
	}

	public void handle429(IRequestContext requestContext) {
		ListenerContext ctx = getListenerContext(requestContext);
		eagler: if(ctx != null && ctx.page429 != null) {
			ResponseLoader loader = responseCache.loadResponse(ctx.page429);
			byte[] data = loader.tryGetResponse();
			if(data != null) {
				if(data == ResponseCache.ERROR) {
					break eagler;
				}
				completeRequest(requestContext, 429, ctx.page429.getType(), data);
			}else {
				IContextPromise promise = requestContext.suspendContext();
				loader.loadResponse((data0) -> {
					if(data0 != ResponseCache.ERROR) {
						completeRequest(requestContext, 429, ctx.page429.getType(), data0);
						promise.complete();
					}else {
						try {
							defaults.handle429(requestContext);
						}catch(Exception ex) {
							promise.complete(ex);
							return;
						}
						promise.complete();
					}
				});
			}
			return;
		}
		defaults.handle429(requestContext);
	}

	public void handle500(IRequestContext requestContext) {
		ListenerContext ctx = getListenerContext(requestContext);
		eagler: if(ctx != null && ctx.page500 != null) {
			ResponseLoader loader = responseCache.loadResponse(ctx.page500);
			byte[] data = loader.tryGetResponse();
			if(data != null) {
				if(data == ResponseCache.ERROR) {
					break eagler;
				}
				completeRequest(requestContext, 500, ctx.page500.getType(), data);
			}else {
				IContextPromise promise = requestContext.suspendContext();
				loader.loadResponse((data0) -> {
					if(data0 != ResponseCache.ERROR) {
						completeRequest(requestContext, 500, ctx.page500.getType(), data0);
						promise.complete();
					}else {
						try {
							defaults.handle500(requestContext);
						}catch(Exception ex) {
							promise.complete(ex);
							return;
						}
						promise.complete();
					}
				});
			}
			return;
		}
		defaults.handle500(requestContext);
	}

	private void addCORSHeader(IRequestContext context) {
		if(enableCORS) {
			context.addResponseHeader("access-control-allow-origin", "*");
		}
	}

	private void completeRequest(IRequestContext context, int code, ConfigDataMIMEType contentType, byte[] data) {
		addCORSHeader(context);
		if(contentType != null) {
			context.addResponseHeader("content-type", contentType.getContentTypeHeader());
			context.addResponseHeader("cache-control", contentType.getCacheControlHeader());
		}
		context.setResponseCode(code);
		context.setResponseBody(data);
	}

	private static final List<EnumRequestMethod> allowMethods = ImmutableList.of(EnumRequestMethod.GET,
			EnumRequestMethod.HEAD, EnumRequestMethod.OPTIONS);

	public boolean enablePreflight() {
		return enableCORS;
	}

	public void handlePreflight(IPreflightContext context) {
		if(enableCORS) {
			context.setResponseCode(200);
			context.addResponseHeader("access-control-allow-origin", "*");
			context.addResponseHeaders("access-control-allow-methods", allowMethods);
			context.addResponseHeaders("access-control-allow-headers",
					context.getHeaders("access-control-request-headers"));
			context.setResponseBodyEmpty();
		}else {
			context.setResponseCode(403);
			context.setResponseBodyEmpty();
		}
	}

	public void release() {
		responseCache.dispose();
	}

	public int size() {
		return totalIndexed;
	}

}
