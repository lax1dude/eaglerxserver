package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEventHandler;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.rpc.api.data.BrandData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.CookieData;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewStateData;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.IconDef;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumEnableFNAW;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCRequestFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.DataSerializationContext;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.Util;
import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCInjectRawBinaryFrameV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCRequestPlayerInfo;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccessEaglerV2;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccessEaglerV2.ExtCapability;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public class EaglerPlayerRPC<PlayerObject> extends BasePlayerRPC<PlayerObject>
		implements IEaglerPlayerRPC<PlayerObject> {

	protected final int eaglerHandshake;
	protected final int eaglerProtocol;
	protected final int eaglerRewindProtocol;
	protected final int eaglerStandardCaps;
	protected final byte[] eaglerStandardCapsVersions;
	protected final Map<UUID, Byte> eaglerExtendedCapsVersions;

	public EaglerPlayerRPC(PlayerInstanceRemote<PlayerObject> player, EaglerBackendRPCProtocol protocol,
			DataSerializationContext serializeCtx, SPacketRPCEnabledSuccessEaglerV2 enablePacket) {
		super(player, protocol, serializeCtx, enablePacket.minecraftProtocol, enablePacket.supervisorNode);
		this.eaglerHandshake = enablePacket.eaglerHandshake;
		this.eaglerProtocol = enablePacket.eaglerProtocol;
		this.eaglerRewindProtocol = enablePacket.eaglerRewindProtocol;
		this.eaglerStandardCaps = enablePacket.eaglerStandardCaps;
		this.eaglerStandardCapsVersions = enablePacket.eaglerStandardCapsVersions != null
				? enablePacket.eaglerStandardCapsVersions : Util.ZERO_BYTES;
		if(enablePacket.eaglerExtendedCaps != null && !enablePacket.eaglerExtendedCaps.isEmpty()) {
			ImmutableMap.Builder<UUID, Byte> builder = ImmutableMap.builder();
			for(ExtCapability cap : enablePacket.eaglerExtendedCaps) {
				builder.put(cap.uuid, (byte) cap.version);
			}
			this.eaglerExtendedCapsVersions = builder.build();
		}else {
			this.eaglerExtendedCapsVersions = Collections.emptyMap();
		}
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	public IEaglerPlayerRPC<PlayerObject> asEaglerPlayer() {
		return this;
	}

	@Override
	public int getEaglerHandshakeVersion() {
		return eaglerHandshake;
	}

	@Override
	public int getEaglerProtocolVersion() {
		return eaglerProtocol;
	}

	@Override
	public boolean isEaglerXRewindPlayer() {
		return eaglerRewindProtocol != -1;
	}

	@Override
	public int getRewindProtocolVersion() {
		return eaglerRewindProtocol;
	}

	@Override
	public boolean hasCapability(EnumCapabilitySpec capability) {
		return CapabilityBits.hasCapability(eaglerStandardCaps, eaglerStandardCapsVersions, capability.getId(), capability.getVer());
	}

	@Override
	public int getCapability(EnumCapabilityType capability) {
		return CapabilityBits.getCapability(eaglerStandardCaps, eaglerStandardCapsVersions, capability.getId());
	}

	@Override
	public boolean hasExtendedCapability(UUID extendedCapability, int version) {
		Byte b = eaglerExtendedCapsVersions.get(extendedCapability);
		return b != null && (b.byteValue() & 0xFF) >= version;
	}

	@Override
	public int getExtendedCapability(UUID extendedCapability) {
		Byte b = eaglerExtendedCapsVersions.get(extendedCapability);
		return b != null ? (b.byteValue() & 0xFF) : -1;
	}

	@Override
	public IRPCFuture<String> getRealIP(int timeoutSec) {
		RPCRequestFuture<String> ret = createRequest(timeoutSec);
		sendRPCPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
				CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_REAL_IP));
		return ret;
	}

	@Override
	public IRPCFuture<String> getOrigin(int timeoutSec) {
		RPCRequestFuture<String> ret = createRequest(timeoutSec);
		sendRPCPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
				CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_ORIGIN));
		return ret;
	}

	@Override
	public IRPCFuture<String> getUserAgent(int timeoutSec) {
		RPCRequestFuture<String> ret = createRequest(timeoutSec);
		sendRPCPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
				CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_USER_AGENT));
		return ret;
	}

	@Override
	public IRPCFuture<CookieData> getCookieData(int timeoutSec) {
		RPCRequestFuture<CookieData> ret = createRequest(timeoutSec);
		sendRPCPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
				CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_COOKIE));
		return ret;
	}

	@Override
	public IRPCFuture<BrandData> getBrandData(int timeoutSec) {
		RPCRequestFuture<BrandData> ret = createRequest(timeoutSec);
		sendRPCPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
				CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_DATA));
		return ret;
	}

	@Override
	public IRPCFuture<byte[]> getAuthUsername(int timeoutSec) {
		RPCRequestFuture<byte[]> ret = createRequest(timeoutSec);
		sendRPCPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
				CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_AUTH_USERNAME));
		return ret;
	}

	@Override
	public IRPCFuture<EnumVoiceState> getVoiceState(int timeoutSec) {
		RPCRequestFuture<EnumVoiceState> ret = createRequest(timeoutSec);
		sendRPCPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
				CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_VOICE_STATUS));
		return ret;
	}

	@Override
	public IRPCFuture<WebViewStateData> getWebViewState(int timeoutSec) {
		RPCRequestFuture<WebViewStateData> ret = createRequest(timeoutSec);
		sendRPCPacket(new CPacketRPCRequestPlayerInfo(ret.getRequestId(),
				CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS_V2));
		return ret;
	}

	@Override
	public void injectRawBinaryFrame(byte[] data) {
		sendRPCPacket(new CPacketRPCInjectRawBinaryFrameV2(data));
	}

	@Override
	public int getSubscribedEventsBits() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addGenericEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeGenericEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRedirectPlayerSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void redirectPlayerToWebSocket(String webSocketURI) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPauseMenuCustomizationSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPauseMenuCustomizationState(ICustomPauseMenu packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendWebViewMessageString(String channelName, String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendWebViewMessageString(String channelName, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendWebViewMessageBytes(String channelName, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCookieSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCookieData(byte[] cookieData, long expiresAfterSec, boolean revokeQuerySupported,
			boolean saveToDisk) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnableFNAWSkins(EnumEnableFNAW state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetEnableFNAWSkins() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isNotificationSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerNotificationIcon(UUID iconUUID, IPacketImageData icon) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerNotificationIcons(Collection<IconDef> icons) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releaseNotificationIcon(UUID iconUUID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releaseNotificationIcons(Collection<UUID> iconUUIDs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showNotificationBadge(INotificationBadge badge) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideNotificationBadge(UUID badgeUUID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDisplayWebViewSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void displayWebViewURL(String title, String url, Set<EnumWebViewPerms> permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayWebViewBlob(String title, SHA1Sum hash, Set<EnumWebViewPerms> permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayWebViewBlob(String title, String alias, Set<EnumWebViewPerms> permissions) {
		// TODO Auto-generated method stub
		
	}

}
