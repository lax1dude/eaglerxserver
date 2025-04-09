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

package net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class CPacketSvProxyBrand implements EaglerSupervisorPacket {

	public static final int PROXY_TYPE_3RD_PARTY = 0;
	public static final int PROXY_TYPE_BUNGEE = 1;
	public static final int PROXY_TYPE_VELOCITY = 2;
	public static final int PROXY_TYPE_EAGLER_STANDALONE = 3;

	public static final int PLUGIN_TYPE_3RD_PARTY = 0;
	public static final int PLUGIN_TYPE_EAGLERXBUNGEE = 1;
	public static final int PLUGIN_TYPE_EAGLERXVELOCITY = 2;
	public static final int PLUGIN_TYPE_EAGLERXSERVER = 3;

	public int proxyType;
	public String proxyVersion;
	public int pluginType;
	public String pluginBrand;
	public String pluginVersion;

	public CPacketSvProxyBrand() {
	}

	public CPacketSvProxyBrand(int proxyType, String proxyVersion, int pluginType, String pluginBrand,
			String pluginVersion) {
		this.proxyType = proxyType;
		this.proxyVersion = proxyVersion;
		this.pluginType = pluginType;
		this.pluginBrand = pluginBrand;
		this.pluginVersion = pluginVersion;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		proxyType = buffer.readUnsignedByte();
		pluginType = buffer.readUnsignedByte();
		proxyVersion = buffer.readCharSequence(buffer.readShort(), StandardCharsets.UTF_8).toString();
		pluginBrand = buffer.readCharSequence(buffer.readShort(), StandardCharsets.UTF_8).toString();
		pluginVersion = buffer.readCharSequence(buffer.readShort(), StandardCharsets.UTF_8).toString();
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		buffer.writeByte(proxyType);
		buffer.writeByte(pluginType);
		byte[] b = proxyVersion.getBytes(StandardCharsets.UTF_8);
		buffer.writeShort(b.length);
		buffer.writeBytes(b);
		b = pluginBrand.getBytes(StandardCharsets.UTF_8);
		buffer.writeShort(b.length);
		buffer.writeBytes(b);
		b = pluginVersion.getBytes(StandardCharsets.UTF_8);
		buffer.writeShort(b.length);
		buffer.writeBytes(b);
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleClient(this);
	}

}