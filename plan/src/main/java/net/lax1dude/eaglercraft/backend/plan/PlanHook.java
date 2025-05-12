/*
 * Copyright (c) 2025 ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.plan;

import com.djrapitops.plan.capability.CapabilityService;
import com.djrapitops.plan.extension.Caller;
import com.djrapitops.plan.extension.ExtensionService;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

import java.util.UUID;
import java.util.function.BiConsumer;

public class PlanHook {
	private static Caller extCaller;

	public static BiConsumer<UUID, String> hookIntoPlan(IEaglerXServerAPI<?> serverAPI) {
		if (!CapabilityService.getInstance().hasCapability("DATA_EXTENSION_VALUES"))
			return (uuid, name) -> {};
		extCaller = ExtensionService.getInstance().register(new EaglerXDataExtension(serverAPI)).orElse(null);
		CapabilityService.getInstance().registerEnableListener(isPlanEnabled -> {
			if (isPlanEnabled) {
				extCaller = ExtensionService.getInstance().register(new EaglerXDataExtension(serverAPI)).orElse(null);
			}
		});
		return (uuid, name) -> {
			if (extCaller != null) {
				extCaller.updatePlayerData(uuid, name);
			}
		};
	}
}
