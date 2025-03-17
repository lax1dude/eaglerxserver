package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebConfig.ConfigDataSettings;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.PathProcessor.RedirectDirException;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.ResponseCache.ResponseLoader;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext.IContextPromise;

public class EaglerWebHandler {

	private final ResponseCache responseCache;
	private final Map<IEaglerListenerInfo, ListenerContext> listeners;
	private final ListenerContext defaultListener;
	private final boolean enableCORS;
	private final int totalIndexed;

	private static class ListenerContext {

		protected final IndexNode root;
		protected final List<String> pageIndexNames;
		protected final ResponseCacheKey page404;
		protected final ResponseCacheKey page429;
		protected final ResponseCacheKey page500;
		protected final boolean autoindex;

		protected ListenerContext(IndexNode root, List<String> pageIndexNames, ResponseCacheKey page404,
				ResponseCacheKey page429, ResponseCacheKey page500, boolean autoindex) {
			this.root = root;
			this.pageIndexNames = pageIndexNames;
			this.page404 = page404;
			this.page429 = page429;
			this.page500 = page500;
			this.autoindex = autoindex;
		}

	}

	public static EaglerWebHandler build(EaglerWeb<?> eaglerWeb) throws IOException {
		EaglerWebConfig config = eaglerWeb.getConfig();
		ResponseCacheBuilder cacheBuilder = new ResponseCacheBuilder(config.getMemoryCacheExpiresAfter(),
				config.getMemoryCacheMaxFiles(), config.getFileIOThreadCount(), eaglerWeb.logger());
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
		return new EaglerWebHandler(cacheBuilder.build(), builder.build(), defaultListener, config.getEnableCORS(), counter[0]);
	}

	private static ListenerContext buildContext(ConfigDataSettings settings, ResponseCacheBuilder cacheBuilder,
			Map<File, IndexNodeFolder> documentRoots, int[] counter) throws IOException {
		return new ListenerContext(
				index(documentRoots, settings.getRootFolder(), cacheBuilder, counter),
				settings.getPageIndexNames(),
				settings.getPage404NotFound() != null ? cacheBuilder.createEntry(settings.getPage404NotFound()) : null,
				settings.getPage429RateLimit() != null ? cacheBuilder.createEntry(settings.getPage429RateLimit()) : null,
				settings.getPage500InternalError() != null ? cacheBuilder.createEntry(settings.getPage500InternalError()) : null,
				settings.isEnableAutoIndex());
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
				IndexNodeFolder folder = indexDir(file, cacheBuilder, counter);
				if(!folder.isEmpty()) {
					directoryIndex.put(child.getName(), folder);
				}
			}else if(child.isFile()) {
				directoryIndex.put(child.getName(), new IndexNodeFile(cacheBuilder.createEntry(file)));
				++counter[0];
			}
		}
		return new IndexNodeFolder(directoryIndex.build());
	}

	private EaglerWebHandler(ResponseCache responseCache, Map<IEaglerListenerInfo, ListenerContext> listeners,
			ListenerContext defaultListener, boolean enableCORS, int totalIndexed) {
		this.responseCache = responseCache;
		this.listeners = listeners;
		this.defaultListener = defaultListener;
		this.enableCORS = enableCORS;
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
					handleAutoIndex(requestContext, e.autoIndex);
					return;
				}
				requestContext.setResponseCode(308);
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
				completeRequest(requestContext, code, data);
			}else {
				IContextPromise promise = requestContext.suspendContext();
				loader.loadResponse((data0) -> {
					if(data0 != ResponseCache.ERROR) {
						completeRequest(requestContext, code, data0);
						promise.complete();
					}else {
						try {
							promise.context().getServer().get404Handler().handleRequest(requestContext);
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
		requestContext.getServer().getDefault404Handler().handleRequest(requestContext);
	}

	private void handleAutoIndex(IRequestContext requestContext, IndexNodeFolder autoIndex) {
		//TODO
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
				completeRequest(requestContext, 429, data);
			}else {
				IContextPromise promise = requestContext.suspendContext();
				loader.loadResponse((data0) -> {
					if(data0 != ResponseCache.ERROR) {
						completeRequest(requestContext, 429, data0);
						promise.complete();
					}else {
						try {
							promise.context().getServer().get429Handler().handleRequest(requestContext);
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
		requestContext.getServer().getDefault429Handler().handleRequest(requestContext);
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
				completeRequest(requestContext, 500, data);
			}else {
				IContextPromise promise = requestContext.suspendContext();
				loader.loadResponse((data0) -> {
					if(data0 != ResponseCache.ERROR) {
						completeRequest(requestContext, 500, data0);
						promise.complete();
					}else {
						try {
							promise.context().getServer().get500Handler().handleRequest(requestContext);
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
		requestContext.getServer().getDefault500Handler().handleRequest(requestContext);
	}

	private void completeRequest(IRequestContext context, int code, byte[] data) {
		context.setResponseCode(code);
		context.setResponseBody(data);
	}

	public boolean isEnableCORS() {
		return enableCORS;
	}

	public boolean handleCORSAllowOrigin(String origin, EnumRequestMethod method, String path, String query) {
		return enableCORS;
	}

	public void release() {
		responseCache.dispose();
	}

	public int size() {
		return totalIndexed;
	}

}
