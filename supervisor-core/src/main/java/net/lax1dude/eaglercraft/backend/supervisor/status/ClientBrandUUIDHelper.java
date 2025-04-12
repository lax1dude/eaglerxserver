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

package net.lax1dude.eaglercraft.backend.supervisor.status;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientBrandUUIDHelper {

	private static final Map<UUID, String> uuidMap;

	static {
		uuidMap = new HashMap<>();
		uuidMap.put(new UUID(0x1DCE015CD384374El, 0x85030A4DE95E5736l), "Vanilla");
		uuidMap.put(UUID.fromString("4448369e-4e87-3621-94f5-e28eeb160524"), "EaglercraftX");
		uuidMap.put(UUID.fromString("71d0c812-01c2-366a-a0d2-3d9aa10846eb"), "EaglercraftX-pre-u37");
		uuidMap.put(UUID.fromString("75d26b8e-d380-37e7-8df8-eeddda3bc4ae"), "Resent");
		uuidMap.put(UUID.fromString("d63c389f-be3b-3a63-b65a-f50df6c35d51"), "Resent-5.0");
		uuidMap.put(UUID.fromString("ba234c7c-48e6-3fe3-92fb-05f3ec92578e"), "Astra Client");
		uuidMap.put(UUID.fromString("905d41c7-6823-3339-a2f5-324a1e8fcb66"), "Astra Client (Old)");
		uuidMap.put(UUID.fromString("219f599b-9d15-3c05-8489-d32fd4d10134"), "Eaglercraft Zeta");
		uuidMap.put(UUID.fromString("10c97b2a-ef57-35d1-9ed5-04eefa41e096"), "Eaglercraft Zeta (Old)");
		uuidMap.put(UUID.fromString("dae42dd9-8ef3-37d2-9b29-7ccde3268464"), "Shadow");
		uuidMap.put(UUID.fromString("5afcc189-5025-3cd4-b42b-1bb94d290f8e"), "Shadow (Old)");
		uuidMap.put(UUID.fromString("dc4b2e43-7c90-38a5-831c-17aed726660c"), "MesscraftX");
		uuidMap.put(UUID.fromString("f2a37ea0-2e2d-3975-9613-4dbd4dad55fb"), "MesscraftX (Old)");
		uuidMap.put(UUID.fromString("6981200a-3d88-32ff-b7b8-050926037d4a"), "PiClient");
		uuidMap.put(UUID.fromString("816ab8a0-4df2-3d5f-9b0f-43e0382f4005"), "PiClient (New)");
		uuidMap.put(UUID.fromString("ffb0b0da-24a7-3a61-9585-9f575105167b"), "EaglercraftLambda");
		uuidMap.put(UUID.fromString("15466f41-a003-330d-94e7-faaff1187272"), "EaglercraftLambda (Old)");
		uuidMap.put(UUID.fromString("7122cfae-548c-36ff-9458-f5c39799d0ff"), "DragonX V2");
		uuidMap.put(UUID.fromString("1ea3c968-98a8-3c5e-b4df-8a0ce993057c"), "WurstX");
		uuidMap.put(UUID.fromString("c49b095c-2b61-3ce4-8c87-eb5d53cb7e90"), "Starlike");
		uuidMap.put(UUID.fromString("0b2de049-0340-32c2-a8eb-2aefbd931828"), "EaglyMC");
		uuidMap.put(UUID.fromString("e214f7b7-17b6-3e74-96b7-93ab32ced079"), "EaglerForge");
		uuidMap.put(UUID.fromString("92e0deda-3827-355a-83cb-275fe4e1b104"), "EaglerForge (Old)");
		uuidMap.put(UUID.fromString("5c2557c7-3dab-327a-bd10-ff374a409431"), "Kone Client");
		uuidMap.put(UUID.fromString("522b2ce5-c9b9-36cf-be7c-5d90f55e631a"), "Eaglercraft 1.12");
		uuidMap.put(UUID.fromString("879f3d8d-d17f-3fb5-b173-8794b1987153"), "Pixel Client");
	}

	public static String toString(UUID uuid) {
		String str = uuidMap.get(uuid);
		return str != null ? str : uuid.toString();
	}

}