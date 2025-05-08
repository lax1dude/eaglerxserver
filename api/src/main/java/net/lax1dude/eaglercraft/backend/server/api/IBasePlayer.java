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

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;

public interface IBasePlayer<PlayerObject> extends IBaseLoginConnection {

	/**
	 * Gets the server API instance associated with this connection.
	 * 
	 * @return the server API instance.
	 */
	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	/**
	 * Gets the underlying platform's player object associated with this connection.
	 * 
	 * <p>The player object type is dependent on what platform the plugin is running on
	 * (Bukkit, BungeeCord, Velocity, etc).
	 * 
	 * @return The underlying platform's player object.
	 */
	@Nonnull
	PlayerObject getPlayerObject();

	/**
	 * Gets the {@code MC|Brand} string of this player.
	 * 
	 * @return The brand string, or {@code null} if the client has not sent one.
	 */
	@Nullable
	String getMinecraftBrand();

	/**
	 * Gets the eagler brand UUID of this player.
	 * 
	 * <p>Vanilla players will ret
	 * 
	 * @return
	 */
	@Nonnull
	UUID getEaglerBrandUUID();

	@Nullable
	default IBrandRegistration getEaglerBrandDesc() {
		return getServerAPI().getBrandService().lookupRegisteredBrand(getEaglerBrandUUID());
	}

	@Nullable
	@Override
	IEaglerPlayer<PlayerObject> asEaglerPlayer();

	@Nonnull
	ISkinManagerBase<PlayerObject> getSkinManager();

	void sendChatMessage(@Nonnull String message);

	<ComponentObject> void sendChatMessage(@Nonnull ComponentObject message);

	void disconnect(@Nonnull String kickMessage);

	<ComponentObject> void disconnect(@Nonnull ComponentObject kickMessage);

}
