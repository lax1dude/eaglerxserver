package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;
import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketQueryHandler;

public class MOTDConnectionWrapper implements IMOTDConnection {

	private final WebSocketQueryHandler queryConnection;

	public MOTDConnectionWrapper(WebSocketQueryHandler queryConnection) {
		this.queryConnection = queryConnection;
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
	public String getHeader(EnumWebSocketHeader header) {
		return queryConnection.getHeader(header);
	}

	@Override
	public void sendToUser() {
		
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
	public int[] getBitmap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBitmap(int[] bitmap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLine1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLine1(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLine2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLine2(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPlayerTotal() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPlayerTotal(int total) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPlayerMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPlayerMax(int total) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getPlayerList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPlayerList(List<String> list) {
		// TODO Auto-generated method stub
		
	}

}
