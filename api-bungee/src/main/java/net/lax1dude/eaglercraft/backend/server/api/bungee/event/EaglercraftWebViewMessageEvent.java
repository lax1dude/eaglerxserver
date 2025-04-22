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

package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public final class EaglercraftWebViewMessageEvent extends Event
		implements IEaglercraftWebViewMessageEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final String channel;
	private final EnumMessageType type;
	private final byte[] data;
	private String asString;

	public EaglercraftWebViewMessageEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerPlayer<ProxiedPlayer> player, @Nonnull String channel, @Nonnull EnumMessageType type,
			@Nonnull byte[] data) {
		this.api = api;
		this.player = player;
		this.channel = channel;
		this.type = type;
		this.data = data;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IEaglerPlayer<ProxiedPlayer> getPlayer() {
		return player;
	}

	@Nonnull
	@Override
	public String getChannel() {
		return channel;
	}

	@Nonnull
	@Override
	public EnumMessageType getType() {
		return type;
	}

	@Nonnull
	@Override
	public String getAsString() {
		if(asString == null) {
			asString = new String(data, StandardCharsets.UTF_8);
		}
		return asString;
	}

	@Nonnull
	@Override
	public byte[] getAsBinary() {
		return data;
	}

}
