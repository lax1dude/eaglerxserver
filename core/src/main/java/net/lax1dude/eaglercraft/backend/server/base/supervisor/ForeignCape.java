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

package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.server.util.KeyedConcurrentLazyLoader;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetCapeByURL;

class ForeignCape extends KeyedConcurrentLazyLoader<UUID, IEaglerPlayerCape> {

	protected final SupervisorResolver owner;
	protected final String url;

	protected ForeignCape(SupervisorResolver owner, String url) {
		this.owner = owner;
		this.url = url;
	}

	@Override
	protected void loadImpl(Consumer<IEaglerPlayerCape> callback) {
		SupervisorConnection handler = owner.getConnection();
		if (handler != null) {
			UUID lookupUUID = UUID.randomUUID();
			owner.addWaitingForeignURLCapeLookup(lookupUUID, callback);
			handler.sendSupervisorPacket(new CPacketSvGetCapeByURL(lookupUUID, url));
		} else {
			owner.addDeferred((fail) -> {
				if (fail) {
					callback.accept(MissingCape.UNAVAILABLE_CAPE);
				} else {
					loadImpl(callback);
				}
			});
		}
	}

	@Override
	public void load(UUID key, Consumer<IEaglerPlayerCape> callback) {
		cmpXchgRelease(MissingCape.UNAVAILABLE_CAPE, null);
		super.load(key, callback);
	}

	@Override
	protected IPlatformLogger getLogger() {
		return owner.logger();
	}

}
