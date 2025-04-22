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

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingSkin;
import net.lax1dude.eaglercraft.backend.server.util.KeyedConcurrentLazyLoader;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetSkinByURL;

class ForeignSkin extends KeyedConcurrentLazyLoader<UUID, IEaglerPlayerSkin> {

	protected final SupervisorResolver owner;
	protected int skinModel = -1;
	protected final String url;

	protected ForeignSkin(SupervisorResolver owner, String url) {
		this.owner = owner;
		this.url = url;
	}

	protected ForeignSkin(SupervisorResolver owner, IEaglerPlayerSkin data, int modelId) {
		this.owner = owner;
		this.url = null;
		this.skinModel = modelId;
		this.result = data;
	}

	@Override
	protected void loadImpl(Consumer<IEaglerPlayerSkin> callback) {
		SupervisorConnection handler = owner.getConnection();
		if(handler != null) {
			UUID lookupUUID = UUID.randomUUID();
			owner.addWaitingForeignURLSkinLookup(lookupUUID, callback);
			handler.sendSupervisorPacket(new CPacketSvGetSkinByURL(lookupUUID, skinModel, url));
		}else {
			owner.addDeferred((fail) -> {
				if(fail) {
					callback.accept(MissingSkin.UNAVAILABLE_SKIN);
				}else {
					loadImpl(callback);
				}
			});
		}
	}

	void load(int modelId, UUID key, Consumer<IEaglerPlayerSkin> callback) {
		this.skinModel = modelId;
		cmpXchgRelease(MissingSkin.UNAVAILABLE_SKIN, null);
		this.load(key, callback);
	}

}
