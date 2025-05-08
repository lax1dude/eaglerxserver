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

package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The base type for inbound player connections in pre-login state.
 */
public interface IBasePendingConnection extends IBaseConnection {

	/**
	 * Gets the Minecraft Java Edition PVN of this player.
	 * 
	 * <p>If the connection is an EaglerXRewind connection, this returns the modern
	 * protocol version being emulated by the packet translator module (usually 47).
	 * 
	 * @return The Minecraft Java Edition protocol version as an integer.
	 */
	int getMinecraftProtocol();

	/**
	 * Gets the socket address of this player, as seen by the underlying server and
	 * other non-eagler plugins.
	 * 
	 * @return The socket address of the player.
	 */
	@Nonnull
	SocketAddress getPlayerAddress();

	/**
	 * Checks if this is an Eaglercraft connection.
	 * 
	 * <p>Calling this function is rarely appropriate, you should call
	 * {@link #asEaglerPlayer()} instead and check the return value for
	 * {@code null}, unless you aren't going to perform some action on the eagler
	 * player instance conditionally.
	 * 
	 * <p>This should be preferred over {@code instanceof} or casting, assume that
	 * {@code instanceof} is unreliable and may break at any time if you use it.
	 * 
	 * @return {@code true} if this is an eagler connection.
	 */
	boolean isEaglerPlayer();

	/**
	 * Gets this connection as an Eaglercraft connection.
	 * 
	 * <p>This should be preferred over {@code instanceof} or casting, assume that
	 * {@code instanceof} is unreliable and may break at any time if you use it.
	 * 
	 * @return This connection as an eagler connection, or {@code null} if it is not
	 *         an Eaglercraft connection.
	 */
	@Nullable
	IEaglerPendingConnection asEaglerPlayer();

}
