package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketQueryHandler;

public class MOTDConnectionWrapper implements IMOTDConnection {

	private final WebSocketQueryHandler queryConnection;
	private String subType;
	private String returnType;
	private byte[] icon;
	private boolean iconCloned;
	private boolean hasIcon;
	private boolean iconDirty;
	private List<String> motd;
	private int playerTotal;
	private int playerMax;
	private List<String> playerList;

	public MOTDConnectionWrapper(WebSocketQueryHandler queryConnection) {
		this.queryConnection = queryConnection;
	}

	private static class EaglerArrayList extends ArrayList<String> implements Consumer<IEaglerPlayer<Object>> {

		private int maxLen;

		protected EaglerArrayList(int len) {
			super(len + 1);
			this.maxLen = len;
		}

		@Override
		public void accept(IEaglerPlayer<Object> t) {
			add(t.getUsername());
			if(--maxLen == 0) {
				throw new RuntimeException();
			}
		}

	}

	public void setDefaults(EaglerXServer<?> server) {
		EaglerListener listener = queryConnection.getListenerInfo();
		motd = listener.getServerMOTD();
		IPlatform<?> platform = server.getPlatform();
		playerTotal = platform.getPlayerTotal();
		playerMax = platform.getPlayerMax();
		if(listener.isShowMOTDPlayerList()) {
			playerList = new EaglerArrayList(10);
			try {
				((EaglerXServer<Object>)server).forEachEaglerPlayer((EaglerArrayList)playerList);
			}catch(RuntimeException ex) {
			}
			int more = playerTotal - playerList.size();
			if(more > 0) {
				playerList.add("\u00A77\u00A7o(" + more + " more)");
			}
		}else {
			playerList = Collections.emptyList();
		}
		String queryType = queryConnection.getAccept();
		int i = queryType.indexOf('.');
		if(i > 0) {
			subType = queryType.substring(i + 1);
			if(subType.length() == 0) {
				subType = null;
			}
		}else {
			subType = null;
		}
		if(subType == null || (!subType.startsWith("noicon") && !subType.startsWith("cache.noicon"))) {
			icon = listener.getServerIcon();
		}else {
			icon = null;
		}
		iconCloned = icon == null;
		hasIcon = iconDirty = icon != null;
	}

	private static final JsonArray EMPTY_LIST = new JsonArray();

	@Override
	public void sendToUser() {
		if(!queryConnection.isClosed()) {
			JsonObject obj = new JsonObject();
			if(subType.startsWith("cache.anim")) {
				obj.addProperty("unsupported", true);
				queryConnection.sendResponse(returnType, obj);
				return;
			}else if(subType.startsWith("cache")) {
				JsonArray cacheControl = new JsonArray();
				ConfigDataListener cc = queryConnection.getListenerInfo().getConfigData();
				if(cc.isMotdCacheAnimation()) {
					cacheControl.add("animation");
				}
				if(cc.isMotdCacheResults()) {
					cacheControl.add("results");
				}
				if(cc.isMotdCacheTrending()) {
					cacheControl.add("trending");
				}
				if(cc.isMotdCachePortfolios()) {
					cacheControl.add("portfolio");
				}
				obj.add("cache", cacheControl);
				obj.addProperty("ttl", cc.getMotdCacheTTL());
			}else {
				obj.addProperty("cache", queryConnection.getListenerInfo().getConfigData().isMotdCacheAny());
			}
			boolean noIcon = subType.startsWith("noicon") || subType.startsWith("cache.noicon");
			JsonArray motd = new JsonArray(this.motd.size());
			for(int i = 0; i < 2; ++i) {
				if(i >= this.motd.size()) break;
				motd.add(this.motd.get(i));
			}
			obj.add("motd", motd);
			obj.addProperty("icon", hasIcon && !noIcon);
			obj.addProperty("online", playerTotal);
			obj.addProperty("max", playerMax);
			int i = playerList.size();
			JsonArray playerz;
			if(i > 0) {
				playerz = new JsonArray(playerList.size());
				for(String s : playerList) {
					playerz.add(s);
				}
			}else {
				playerz = EMPTY_LIST;
			}
			obj.add("players", playerz);
			queryConnection.sendResponse(returnType, obj);
			if(hasIcon && !noIcon && iconDirty && icon != null) {
				queryConnection.send(icon);
				iconDirty = false;
			}
			if(subType.startsWith("cache")) {
				close();
			}
		}
	}

	@Override
	public <T> T get(IAttributeKey<T> key) {
		return queryConnection.get(key);
	}

	@Override
	public <T> void set(IAttributeKey<T> key, T value) {
		queryConnection.set(key, value);
	}

	@Override
	public boolean isClosed() {
		return queryConnection.isClosed();
	}

	@Override
	public void close() {
		queryConnection.close();
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return queryConnection.getRemoteAddress();
	}

	@Override
	public String getRealAddress() {
		return queryConnection.getRealAddress();
	}

	@Override
	public IEaglerListenerInfo getListenerInfo() {
		return queryConnection.getListenerInfo();
	}

	@Override
	public String getAccept() {
		return queryConnection.getAccept();
	}

	@Override
	public String getSubType() {
		return subType;
	}

	@Override
	public String getResponseType() {
		return returnType;
	}

	@Override
	public void setResponseType(String type) {
		returnType = type;
	}

	@Override
	public String getHeader(EnumWebSocketHeader header) {
		return queryConnection.getHeader(header);
	}

	@Override
	public long getAge() {
		return queryConnection.getAge();
	}

	@Override
	public void setMaxAge(long millis) {
		queryConnection.setMaxAge(millis);
	}

	@Override
	public long getMaxAge() {
		return queryConnection.getMaxAge();
	}

	@Override
	public byte[] getServerIcon() {
		if(icon == null) return null;
		if(!iconCloned) {
			iconCloned = true;
			return icon = icon.clone();
		}else {
			return icon;
		}
	}

	@Override
	public void setServerIcon(byte[] icon) {
		if(icon != null && icon.length != 16384) {
			throw new IllegalArgumentException("Server icon is the wrong length, should be 16384");
		}
		this.iconCloned = this.iconDirty = this.hasIcon = true;
		this.icon = icon;
	}

	@Override
	public List<String> getServerMOTD() {
		return motd;
	}

	@Override
	public void setServerMOTD(List<String> motd) {
		this.motd = motd;
	}

	@Override
	public int getPlayerTotal() {
		return playerTotal;
	}

	@Override
	public void setPlayerTotal(int total) {
		playerTotal = total;
	}

	@Override
	public int getPlayerMax() {
		return playerMax;
	}

	@Override
	public void setPlayerMax(int total) {
		playerMax = total;
	}

	@Override
	public List<String> getPlayerList() {
		return playerList;
	}

	@Override
	public void setPlayerList(List<String> list) {
		playerList = list;
	}

	@Override
	public NettyUnsafe getNettyUnsafe() {
		return queryConnection.getNettyUnsafe();
	}

}
