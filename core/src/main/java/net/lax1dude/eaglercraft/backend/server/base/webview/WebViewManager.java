package net.lax1dude.eaglercraft.backend.server.base.webview;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent.EnumEventType;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent.EnumMessageType;
import net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewProvider;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.pause_menu.PauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.base.rpc.EaglerPlayerRPCManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketDisplayWebViewBlobV5EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketDisplayWebViewURLV5EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketServerInfoDataChunkV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketWebViewMessageV4EAG;

public class WebViewManager<PlayerObject> implements IWebViewManager<PlayerObject> {

	private static final VarHandle CHANNEL_NAME_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			CHANNEL_NAME_HANDLE = l.findVarHandle(WebViewManager.class, "channelName", String.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final EaglerPlayerInstance<PlayerObject> player;
	private final WebViewService<PlayerObject> service;

	private IWebViewProvider<PlayerObject> provider = null;
	private volatile String channelName = null;

	public WebViewManager(EaglerPlayerInstance<PlayerObject> player, WebViewService<PlayerObject> service) {
		this.player = player;
		this.service = service;
		this.provider = service.getDefaultProvider();
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IWebViewService<PlayerObject> getWebViewService() {
		return service;
	}

	@Override
	public boolean isChannelAllowed() {
		IWebViewProvider<PlayerObject> provider = this.provider;
		return provider != null && provider.isChannelAllowed(this);
	}

	@Override
	public boolean isRequestAllowed() {
		IWebViewProvider<PlayerObject> provider = this.provider;
		return provider != null && provider.isRequestAllowed(this);
	}

	public boolean isChannelAllowedDefault() {
		PauseMenuManager<PlayerObject> mgr = player.getPauseMenuManager();
		return mgr != null && mgr.isWebViewChannelAllowedDefault();
	}

	public boolean isRequestAllowedDefault() {
		PauseMenuManager<PlayerObject> mgr = player.getPauseMenuManager();
		return mgr != null && mgr.isWebViewRequestAllowedDefault();
	}

	public void handleRequestDefault(SHA1Sum hash, Consumer<IWebViewBlob> callback) {
		PauseMenuManager<PlayerObject> mgr = player.getPauseMenuManager();
		if(mgr != null) {
			IWebViewBlob tmp = mgr.getWebViewBlobDefault();
			if(tmp != null && hash.equals(tmp.getHash())) {
				callback.accept(tmp);
				return;
			}
		}
		callback.accept(service.getGlobalBlob(hash));
	}

	public SHA1Sum handleAliasDefault(String alias) {
		return service.getBlobFromAlias(alias);
	}

	@Override
	public IWebViewProvider<PlayerObject> getProvider() {
		return provider;
	}

	@Override
	public void setProvider(IWebViewProvider<PlayerObject> func) {
		provider = func;
	}

	public boolean isChannelOpen() {
		return getOpenChannel() != null;
	}

	@Override
	public boolean isChannelOpen(String channelName) {
		String str = getOpenChannel();
		return str != null && channelName.equals(str);
	}

	@Override
	public Set<String> getOpenChannels() {
		String str = getOpenChannel();
		if(str != null) {
			return Collections.singleton(str);
		}else {
			return Collections.emptySet();
		}
	}

	public final String getOpenChannel() {
		return (String)CHANNEL_NAME_HANDLE.getOpaque(this);
	}

	private boolean validateChannel(String channelName) {
		String str = getOpenChannel();
		if(str != null && channelName.equals(str)) {
			return true;
		}else {
			player.logger().warn("Attempted to send web view message on closed channel: " + channelName);
			return false;
		}
	}

	@Override
	public void sendMessageString(String channelName, String contents) {
		if(validateChannel(channelName)) {
			player.sendEaglerMessage(new SPacketWebViewMessageV4EAG(contents));
		}
	}

	@Override
	public void sendMessageString(String channelName, byte[] contents) {
		if(validateChannel(channelName)) {
			player.sendEaglerMessage(new SPacketWebViewMessageV4EAG(SPacketWebViewMessageV4EAG.TYPE_STRING, contents));
		}
	}

	@Override
	public void sendMessageBinary(String channelName, byte[] contents) {
		if(validateChannel(channelName)) {
			player.sendEaglerMessage(new SPacketWebViewMessageV4EAG(contents));
		}
	}

	@Override
	public boolean isDisplayWebViewSupported() {
		return player.getEaglerProtocol().ver >= 5;
	}

	@Override
	public void displayWebViewURL(String title, String url, Set<EnumWebViewPerms> permissions) {
		if(player.getEaglerProtocol().ver >= 5) {
			player.sendEaglerMessage(new SPacketDisplayWebViewURLV5EAG(
					permissions != null ? EnumWebViewPerms.toBits(permissions) : 0, title, url));
		}else {
			player.logger().warn("Attempted to display web view on an unsupported client");
		}
	}

	@Override
	public void displayWebViewBlob(String title, SHA1Sum hash, Set<EnumWebViewPerms> permissions) {
		if(player.getEaglerProtocol().ver >= 5) {
			player.sendEaglerMessage(new SPacketDisplayWebViewBlobV5EAG(
					permissions != null ? EnumWebViewPerms.toBits(permissions) : 0, title, hash.asBytes()));
		}else {
			player.logger().warn("Attempted to display web view on an unsupported client");
		}
	}

	public void handlePacketRequestData(byte[] hash) {
		if(!player.getRateLimits().ratelimitWebViewData()) {
			player.disconnect(service.getEaglerXServer().componentBuilder().buildTextComponent().beginStyle()
					.color(EnumChatColor.RED).end().text("Too many WebView data requests!").end());
			return;
		}
		IWebViewProvider<PlayerObject> provider = this.provider;
		if(provider != null && provider.isRequestAllowed(this)) {
			SHA1Sum sum = SHA1Sum.create(hash);
			try {
				provider.handleRequest(this, sum, (data) -> {
					if(data != null) {
						sendDataToPlayer(((WebViewBlob)data).list);
					}else {
						try {
							player.disconnect(service.getEaglerXServer().componentBuilder().buildTextComponent().beginStyle()
									.color(EnumChatColor.RED).end().text("WebView content could not be found!").end());
						}catch(Exception ex) {
						}
					}
				});
			}catch(Exception ex) {
				player.logger().error("Could not handle WebView data request for: " + sum, ex);
				player.disconnect(service.getEaglerXServer().componentBuilder().buildTextComponent().beginStyle()
						.color(EnumChatColor.RED).end().text("Error handling webview data request!").end());
			}
		}else {
			player.disconnect(service.getEaglerXServer().componentBuilder().buildTextComponent().beginStyle()
					.color(EnumChatColor.RED).end().text("Unexpected WebView data request!").end());
		}
	}

	private void sendDataToPlayer(List<SPacketServerInfoDataChunkV4EAG> list) {
		long rate = 250l / service.getEaglerXServer().getConfig().getPauseMenu().getServerInfoButtonEmbedSendChunkRate();
		if(rate < 20l) {
			rate = 20l;
		}
		Channel ch = player.getChannel();
		if(ch.isActive()) {
			ch.eventLoop().execute(new DataRunnable(list, rate, ch));
		}
	}

	private class DataRunnable implements Runnable {

		private final List<SPacketServerInfoDataChunkV4EAG> list;
		private final long rate;
		private final Channel chRef;
		private int chunk;

		protected DataRunnable(List<SPacketServerInfoDataChunkV4EAG> list, long rate, Channel chRef) {
			this.list = list;
			this.rate = rate;
			this.chRef = chRef;
		}

		@Override
		public void run() {
			if(!chRef.isActive()) {
				return;
			}
			int c = chunk++;
			player.sendEaglerMessage(list.get(c));
			if(c + 1 < list.size()) {
				chRef.eventLoop().schedule(this, rate, TimeUnit.MILLISECONDS);
			}
		}

	}

	public void handlePacketChannel(String channel, boolean open) {
		if(!player.getRateLimits().ratelimitWebViewMsg()) {
			player.disconnect(service.getEaglerXServer().componentBuilder().buildTextComponent().beginStyle()
					.color(EnumChatColor.RED).end().text("Too many WebView messages!").end());
			return;
		}
		String nextChannel;
		String prevChannel;
		boolean allowed = open && isChannelAllowed();
		for(;;) {
			nextChannel = null;
			prevChannel = (String)CHANNEL_NAME_HANDLE.getOpaque(this);
			if(channel.equals(prevChannel)) {
				if(open) {
					prevChannel = null;
					break;
				}else {
					if(CHANNEL_NAME_HANDLE.compareAndExchange(this, prevChannel, null) == prevChannel) {
						break;
					}
				}
			}else {
				if(open) {
					nextChannel = channel;
					if(allowed) {
						if(CHANNEL_NAME_HANDLE.compareAndExchange(this, prevChannel, channel) == prevChannel) {
							break;
						}
					}else {
						break;
					}
				}else {
					prevChannel = null;
					break;
				}
			}
		}
		if(prevChannel != null) {
			service.getEaglerXServer().eventDispatcher().dispatchWebViewChannelEvent(player,
					EnumEventType.CHANNEL_CLOSE, prevChannel, null);
			EaglerPlayerRPCManager<PlayerObject> rpcMgr = player.getPlayerRPCManager();
			if(rpcMgr != null) {
				rpcMgr.fireWebViewOpenClose(false, prevChannel);
			}
		}
		if(nextChannel != null) {
			if(allowed) {
				service.getEaglerXServer().eventDispatcher().dispatchWebViewChannelEvent(player,
						EnumEventType.CHANNEL_OPEN, nextChannel, null);
				EaglerPlayerRPCManager<PlayerObject> rpcMgr = player.getPlayerRPCManager();
				if(rpcMgr != null) {
					rpcMgr.fireWebViewOpenClose(true, nextChannel);
				}
			}else {
				player.disconnect(service.getEaglerXServer().componentBuilder().buildTextComponent().beginStyle()
						.color(EnumChatColor.RED).end().text("Unexpected WebView channel opened!").end());
			}
		}
	}

	public void handlePacketMessage(byte[] data, boolean binary) {
		if(!player.getRateLimits().ratelimitWebViewMsg()) {
			player.disconnect(service.getEaglerXServer().componentBuilder().buildTextComponent().beginStyle()
					.color(EnumChatColor.RED).end().text("Too many WebView messages!").end());
			return;
		}
		String channel = getOpenChannel();
		if(channel != null) {
			service.getEaglerXServer().eventDispatcher().dispatchWebViewMessageEvent(player, channel,
					binary ? EnumMessageType.BINARY : EnumMessageType.STRING, data, null);
			EaglerPlayerRPCManager<PlayerObject> rpcMgr = player.getPlayerRPCManager();
			if(rpcMgr != null) {
				rpcMgr.fireWebViewMessage(channel, binary, data);
			}
		}else {
			player.disconnect(service.getEaglerXServer().componentBuilder().buildTextComponent().beginStyle()
					.color(EnumChatColor.RED).end().text("Unexpected WebView packet!").end());
		}
	}

}
