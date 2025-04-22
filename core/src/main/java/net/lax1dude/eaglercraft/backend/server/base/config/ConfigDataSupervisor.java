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

package net.lax1dude.eaglercraft.backend.server.base.config;

import java.net.SocketAddress;

public class ConfigDataSupervisor {

	private final boolean enableSupervisor;
	private final SocketAddress supervisorAddress;
	private final String supervisorSecret;
	private final int supervisorConnectTimeout;
	private final int supervisorReadTimeout;
	private final String supervisorUnavailableMessage;
	private final int supervisorSkinAntagonistsRatelimit;
	private final int supervisorBrandAntagonistsRatelimit;
	private final boolean supervisorLookupIgnoreV2UUID;

	public ConfigDataSupervisor(boolean enableSupervisor, SocketAddress supervisorAddress, String supervisorSecret,
			int supervisorConnectTimeout, int supervisorReadTimeout, String supervisorUnavailableMessage,
			int supervisorSkinAntagonistsRatelimit, int supervisorBrandAntagonistsRatelimit,
			boolean supervisorLookupIgnoreV2UUID) {
		this.enableSupervisor = enableSupervisor;
		this.supervisorAddress = supervisorAddress;
		this.supervisorSecret = supervisorSecret;
		this.supervisorConnectTimeout = supervisorConnectTimeout;
		this.supervisorReadTimeout = supervisorReadTimeout;
		this.supervisorUnavailableMessage = supervisorUnavailableMessage;
		this.supervisorSkinAntagonistsRatelimit = supervisorSkinAntagonistsRatelimit;
		this.supervisorBrandAntagonistsRatelimit = supervisorBrandAntagonistsRatelimit;
		this.supervisorLookupIgnoreV2UUID = supervisorLookupIgnoreV2UUID;
	}

	public boolean isEnableSupervisor() {
		return enableSupervisor;
	}

	public SocketAddress getSupervisorAddress() {
		return supervisorAddress;
	}

	public String getSupervisorSecret() {
		return supervisorSecret;
	}

	public int getSupervisorConnectTimeout() {
		return supervisorConnectTimeout;
	}

	public int getSupervisorReadTimeout() {
		return supervisorReadTimeout;
	}

	public String getSupervisorUnavailableMessage() {
		return supervisorUnavailableMessage;
	}

	public int getSupervisorSkinAntagonistsRatelimit() {
		return supervisorSkinAntagonistsRatelimit;
	}

	public int getSupervisorBrandAntagonistsRatelimit() {
		return supervisorBrandAntagonistsRatelimit;
	}

	public boolean isSupervisorLookupIgnoreV2UUID() {
		return supervisorLookupIgnoreV2UUID;
	}

}
