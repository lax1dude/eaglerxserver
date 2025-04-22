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

import java.util.List;
import java.util.Map;

import net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry;

public class ConfigDataRoot {

	private final ConfigDataSettings settings;
	private final Map<String, ConfigDataListener> listeners;
	private final ConfigDataSupervisor supervisor;
	private final List<ICEServerEntry> iceServers;
	private final ConfigDataPauseMenu pauseMenu;

	public ConfigDataRoot(ConfigDataSettings settings, Map<String, ConfigDataListener> listeners,
			ConfigDataSupervisor supervisor, List<ICEServerEntry> iceServers,
			ConfigDataPauseMenu pauseMenu) {
		this.settings = settings;
		this.listeners = listeners;
		this.supervisor = supervisor;
		this.iceServers = iceServers;
		this.pauseMenu = pauseMenu;
	}

	public ConfigDataSettings getSettings() {
		return settings;
	}

	public Map<String, ConfigDataListener> getListeners() {
		return listeners;
	}

	public ConfigDataSupervisor getSupervisor() {
		return supervisor;
	}

	public List<ICEServerEntry> getICEServers() {
		return iceServers;
	}

	public ConfigDataPauseMenu getPauseMenu() {
		return pauseMenu;
	}

}
