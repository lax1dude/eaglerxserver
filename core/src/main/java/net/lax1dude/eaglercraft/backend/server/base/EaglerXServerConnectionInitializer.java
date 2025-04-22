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

package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesProperty;

class EaglerXServerConnectionInitializer<PlayerObject>
		implements IEaglerXServerConnectionInitializer<NettyPipelineData, BaseConnectionInstance> {

	private final EaglerXServer<PlayerObject> server;

	EaglerXServerConnectionInitializer(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initializeConnection(IPlatformConnectionInitializer<NettyPipelineData, BaseConnectionInstance> initializer) {
		NettyPipelineData nettyData = initializer.getPipelineAttachment();
		if(nettyData != null) {
			if (nettyData.isEaglerPlayer()) {
				initializer.setConnectionAttachment(new EaglerConnectionInstance(initializer.getConnection(), nettyData));
				if(server.isEaglerPlayerPropertyEnabled()) {
					initializer.setEaglerPlayerProperty(true);
				}
				TexturesProperty eaglerPlayersSkin = server.getEaglerPlayersVanillaSkin();
				if(eaglerPlayersSkin != null) {
					initializer.setTexturesProperty(eaglerPlayersSkin.getValue(), eaglerPlayersSkin.getSignature());
				}
				if(server.getPlatformType() != EnumPlatformType.BUKKIT) {
					initializer.setUniqueId(nettyData.uuid);
				}
			} else {
				initializer.setConnectionAttachment(new BaseConnectionInstance(initializer.getConnection(),
						nettyData.attributeHolder));
				if(server.isEaglerPlayerPropertyEnabled()) {
					initializer.setEaglerPlayerProperty(false);
				}
			}
		} else {
			initializer.setConnectionAttachment(new BaseConnectionInstance(initializer.getConnection(),
					server.getEaglerAttribManager().createEaglerHolder()));
			if(server.isEaglerPlayerPropertyEnabled()) {
				initializer.setEaglerPlayerProperty(false);
			}
		}
	}

}
