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

package net.lax1dude.eaglercraft.backend.server.base.query;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;
import net.lax1dude.eaglercraft.backend.server.base.EaglerListener;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.IIdentifiedConnection;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketQueryHandler;

public class MOTDConnectionWrapper extends IIdentifiedConnection.Base implements IMOTDConnection {

	private final WebSocketQueryHandler queryConnection;
	private String subType;
	private String returnType;
	private byte[] defaultIcon;
	private boolean defaultIconCloned;
	private byte[] icon;
	private boolean iconCloned;
	private boolean hasIcon;
	private boolean iconDirty;
	private List<String> motd;
	private List<String> defaultMotd;
	private int defaultPlayerTotal;
	private int playerTotal;
	private int defaultPlayerMax;
	private int playerMax;
	private List<String> playerList;
	private List<String> defaultPlayerList;

	public MOTDConnectionWrapper(WebSocketQueryHandler queryConnection) {
		this.queryConnection = queryConnection;
	}

	private static class EaglerPlayerListException extends RuntimeException {
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
				throw new EaglerPlayerListException();
			}
		}

	}

	public void setDefaults(EaglerXServer<?> server) {
		EaglerListener listener = queryConnection.getListenerInfo();
		motd = defaultMotd = listener.getServerMOTD();
		playerTotal = defaultPlayerTotal = server.getSupervisorService().getPlayerTotal();
		playerMax = defaultPlayerMax = server.getSupervisorService().getPlayerMax();
		if(listener.isShowMOTDPlayerList()) {
			playerList = defaultPlayerList = new EaglerArrayList(10);
			try {
				((EaglerXServer<Object>)server).forEachEaglerPlayer((EaglerArrayList)playerList);
			}catch(EaglerPlayerListException ex) {
			}
			int more = playerTotal - playerList.size();
			if(more > 0) {
				playerList.add("\u00A77\u00A7o(" + more + " more)");
			}
		}else {
			playerList = defaultPlayerList = Collections.emptyList();
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
			icon = defaultIcon = listener.getServerIcon();
		}else {
			icon = defaultIcon = null;
		}
		iconCloned = defaultIconCloned = icon == null;
		hasIcon = iconDirty = icon != null;
		returnType = "motd";
	}

	private static final JsonArray EMPTY_LIST = new JsonArray();

	@Override
	public void sendToUser() {
		if(queryConnection.isConnected()) {
			JsonObject obj = new JsonObject();
			if(subType != null && subType.startsWith("cache.anim")) {
				obj.addProperty("unsupported", true);
				queryConnection.sendResponse(returnType, obj);
				return;
			}else if(subType != null && subType.startsWith("cache")) {
				JsonArray cacheControl = new JsonArray();
				ConfigDataListener cc = queryConnection.getListenerInfo().getConfigData();
				if(cc.isMotdCacheAnimation()) {
					cacheControl.add(new JsonPrimitive("animation"));
				}
				if(cc.isMotdCacheResults()) {
					cacheControl.add(new JsonPrimitive("results"));
				}
				if(cc.isMotdCacheTrending()) {
					cacheControl.add(new JsonPrimitive("trending"));
				}
				if(cc.isMotdCachePortfolios()) {
					cacheControl.add(new JsonPrimitive("portfolio"));
				}
				obj.add("cache", cacheControl);
				obj.addProperty("ttl", cc.getMotdCacheTTL());
			}else {
				obj.addProperty("cache", queryConnection.getListenerInfo().getConfigData().isMotdCacheAny());
			}
			boolean noIcon = subType != null && (subType.startsWith("noicon") || subType.startsWith("cache.noicon"));
			JsonArray motd = new JsonArray();
			for(int i = 0; i < 2; ++i) {
				if(i >= this.motd.size()) break;
				motd.add(new JsonPrimitive(this.motd.get(i)));
			}
			obj.add("motd", motd);
			obj.addProperty("icon", hasIcon && !noIcon);
			obj.addProperty("online", playerTotal);
			obj.addProperty("max", playerMax);
			int i = playerList.size();
			JsonArray playerz;
			if(i > 0) {
				playerz = new JsonArray();
				for(String s : playerList) {
					playerz.add(new JsonPrimitive(s));
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
	public Object getIdentityToken() {
		return queryConnection.getIdentityToken();
	}

	@Override
	public boolean isConnected() {
		return queryConnection.isConnected();
	}

	@Override
	public void disconnect() {
		queryConnection.disconnect();
	}

	@Override
	public SocketAddress getSocketAddress() {
		return queryConnection.getSocketAddress();
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
		if(type == null) {
			throw new NullPointerException("type");
		}
		returnType = type;
	}

	@Override
	public boolean isWebSocketSecure() {
		return queryConnection.isWebSocketSecure();
	}

	@Override
	public String getWebSocketHeader(EnumWebSocketHeader header) {
		return queryConnection.getWebSocketHeader(header);
	}

	@Override
	public String getWebSocketPath() {
		return queryConnection.getWebSocketPath();
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
	public byte[] getDefaultServerIcon() {
		if(defaultIcon == null) return null;
		if(!defaultIconCloned) {
			defaultIconCloned = true;
			if(defaultIcon == icon) {
				iconCloned = true;
				return defaultIcon = icon = defaultIcon.clone();
			}else {
				return defaultIcon = defaultIcon.clone();
			}
		}else {
			return defaultIcon;
		}
	}

	@Override
	public byte[] getServerIcon() {
		if(icon == null) return null;
		if(!iconCloned) {
			iconCloned = true;
			if(defaultIcon == icon) {
				defaultIconCloned = true;
				return defaultIcon = icon = icon.clone();
			}else {
				return icon = icon.clone();
			}
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
	public List<String> getDefaultServerMOTD() {
		return defaultMotd;
	}

	@Override
	public List<String> getServerMOTD() {
		return motd;
	}

	@Override
	public void setServerMOTD(List<String> motd) {
		if(motd == null) {
			throw new NullPointerException("motd");
		}
		this.motd = motd;
	}

	@Override
	public int getDefaultPlayerTotal() {
		return defaultPlayerTotal;
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
	public int getDefaultPlayerMax() {
		return defaultPlayerMax;
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
	public List<String> getDefaultPlayerList() {
		return defaultPlayerList;
	}

	@Override
	public List<String> getPlayerList() {
		return playerList;
	}

	@Override
	public void setPlayerList(List<String> list) {
		if(list == null) {
			throw new NullPointerException("list");
		}
		playerList = list;
	}

	@Override
	public NettyUnsafe netty() {
		return queryConnection.netty();
	}

}
