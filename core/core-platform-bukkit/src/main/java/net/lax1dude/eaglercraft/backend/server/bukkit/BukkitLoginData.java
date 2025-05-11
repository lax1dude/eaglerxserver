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

package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineData;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLoginInitializer;

class BukkitLoginData implements IPlatformLoginInitializer<IPipelineData> {

	private final IPipelineData pipelineData;
	protected String texturesPropertyValue;
	protected String texturesPropertySignature;
	protected byte eaglerPlayerProperty;

	protected BukkitLoginData(IPipelineData pipelineData) {
		this.pipelineData = pipelineData;
	}

	@Override
	public IPipelineData getPipelineAttachment() {
		return pipelineData;
	}

	@Override
	public void setUniqueId(UUID uuid) {
	}

	@Override
	public void setTexturesProperty(String propertyValue, String propertySignature) {
		texturesPropertyValue = propertyValue;
		texturesPropertySignature = propertySignature;
	}

	@Override
	public void setEaglerPlayerProperty(boolean enable) {
		eaglerPlayerProperty = enable ? (byte) 2 : (byte) 1;
	}

}
