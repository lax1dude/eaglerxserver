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
