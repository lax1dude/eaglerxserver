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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.config;

public class ConfigDataSettings {

	public static class ConfigDataBackendRPC {

		private final int baseRequestTimeoutSec;
		private final double timeoutResolutionSec;

		ConfigDataBackendRPC(int baseRequestTimeoutSec, double timeoutResolutionSec) {
			this.baseRequestTimeoutSec = baseRequestTimeoutSec;
			this.timeoutResolutionSec = timeoutResolutionSec;
		}

		public int getBaseRequestTimeoutSec() {
			return baseRequestTimeoutSec;
		}

		public double getTimeoutResolutionSec() {
			return timeoutResolutionSec;
		}

	}

	public static class ConfigDataBackendVoice {

		private final boolean enableBackendVoiceService;

		ConfigDataBackendVoice(boolean enableBackendVoiceService) {
			this.enableBackendVoiceService = enableBackendVoiceService;
		}

		public boolean isEnableBackendVoiceService() {
			return enableBackendVoiceService;
		}

	}

	private final boolean forceModernizedChannelNames;
	private final ConfigDataBackendRPC configBackendRPC;
	private final ConfigDataBackendVoice configBackendVoice;

	ConfigDataSettings(boolean forceModernizedChannelNames, ConfigDataBackendRPC configBackendRPC,
			ConfigDataBackendVoice configBackendVoice) {
		this.forceModernizedChannelNames = forceModernizedChannelNames;
		this.configBackendRPC = configBackendRPC;
		this.configBackendVoice = configBackendVoice;
	}

	public boolean isForceModernizedChannelNames() {
		return forceModernizedChannelNames;
	}

	public ConfigDataBackendRPC getConfigBackendRPC() {
		return configBackendRPC;
	}

	public ConfigDataBackendVoice getConfigBackendVoice() {
		return configBackendVoice;
	}

}
