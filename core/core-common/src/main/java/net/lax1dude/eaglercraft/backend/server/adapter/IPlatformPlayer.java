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

package net.lax1dude.eaglercraft.backend.server.adapter;

import java.net.SocketAddress;
import java.util.UUID;

import io.netty.channel.Channel;

public interface IPlatformPlayer<PlayerObject> extends IPlatformCommandSender<PlayerObject> {

	PlayerObject getPlayerObject();

	Channel getChannel();

	IPlatformServer<PlayerObject> getServer();

	String getUsername();

	UUID getUniqueId();

	SocketAddress getSocketAddress();

	int getMinecraftProtocol();

	boolean isConnected();

	boolean isOnlineMode();

	String getMinecraftBrand();

	String getTexturesProperty();

	void sendDataClient(String channel, byte[] message);

	void sendDataBackend(String channel, byte[] message);

	boolean isSetViewDistanceSupportedPaper();

	void setViewDistancePaper(int distance);

	void sendMessage(String message);

	void disconnect();

	void disconnect(String kickMessage);

	<ComponentObject> void disconnect(ComponentObject kickMessage);

	<T> T getPlayerAttachment();

}
