package net.lax1dude.eaglercraft.backend.server.base.webview;

import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketServerInfoDataChunkV4EAG;

public class WebViewBlob implements IWebViewBlob {

	final SHA1Sum hash;
	final List<SPacketServerInfoDataChunkV4EAG> list;

	WebViewBlob(SHA1Sum hash, List<SPacketServerInfoDataChunkV4EAG> list) {
		this.hash = hash;
		this.list = list;
	}

	@Override
	public int getLength() {
		return list.get(0).finalSize;
	}

	@Override
	public SHA1Sum getHash() {
		return hash;
	}

	public int hashCode() {
		return hash.hashCode();
	}

	public boolean equals(Object o) {
		return this == o || ((o instanceof WebViewBlob o2) && o2.hash.equals(hash));
	}

}
