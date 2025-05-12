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

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.annotation.*;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import net.lax1dude.eaglercraft.backend.server.api.*;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;

import java.util.UUID;

@PluginInfo(
		name = "EaglerXServer",
		iconName = "feather-pointed",
		iconFamily = Family.SOLID,
		color = Color.YELLOW
)
public class EaglerXDataExtension implements DataExtension {
	private final IEaglerXServerAPI<?> serverAPI;

	public EaglerXDataExtension(IEaglerXServerAPI<?> serverAPI) {
		this.serverAPI = serverAPI;
	}

	@Override
	public CallEvents[] callExtensionMethodsOn() {
		return new CallEvents[]{
				CallEvents.MANUAL
		};
	}

	@BooleanProvider(
			text = "Is Eagler Player",
			description = "Whether or not the player is an Eaglercraft player",
			conditionName = "isEaglerPlayer",
			hidden = true
	)
	public boolean isEaglerPlayer(UUID playerUUID) {
		return serverAPI.isEaglerPlayerByUUID(playerUUID);
	}

	@Conditional("isEaglerPlayer")
	@StringProvider(
			text = "User Agent",
			description = "The user agent of the Eaglercraft player",
			priority = 14,
			iconName = "id-card",
			iconFamily = Family.SOLID,
			iconColor = Color.CYAN
	)
	public String userAgent(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getWebSocketHeader(EnumWebSocketHeader.HEADER_USER_AGENT);
	}

	@Conditional("isEaglerPlayer")
	@StringProvider(
			text = "Origin",
			description = "The origin of the Eaglercraft player",
			priority = 15,
			iconName = "i-cursor",
			iconFamily = Family.SOLID,
			iconColor = Color.CYAN
	)
	public String origin(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getWebSocketHeader(EnumWebSocketHeader.HEADER_ORIGIN);
	}

	@Conditional("isEaglerPlayer")
	@StringProvider(
			text = "Host",
			description = "The host of the Eaglercraft player",
			priority = 13,
			iconName = "server",
			iconFamily = Family.SOLID,
			iconColor = Color.CYAN
	)
	public String host(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getWebSocketHeader(EnumWebSocketHeader.HEADER_HOST);
	}

	@Conditional("isEaglerPlayer")
	@StringProvider(
			text = "Brand String",
			description = "The brand string of the Eaglercraft player",
			priority = 20,
			iconName = "code-fork",
			iconFamily = Family.SOLID,
			iconColor = Color.GREEN
	)
	public String brandString(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getEaglerBrandString();
	}

	@Conditional("isEaglerPlayer")
	@StringProvider(
			text = "Brand UUID",
			description = "The brand UUID of the Eaglercraft player",
			priority = 11,
			iconName = "fingerprint",
			iconFamily = Family.SOLID,
			iconColor = Color.GREEN
	)
	public String brandUUID(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getEaglerBrandUUID().toString();
	}

	@Conditional("isEaglerPlayer")
	@StringProvider(
			text = "Brand Description",
			description = "The brand description of the Eaglercraft player",
			priority = 12,
			iconName = "circle-info",
			iconFamily = Family.SOLID,
			iconColor = Color.GREEN
	)
	public String brandDesc(UUID playerUUID) {
		IBrandRegistration brand = serverAPI.getEaglerPlayerByUUID(playerUUID).getEaglerBrandDesc();
		if (brand == null) return "";
		return brand.getBrandDesc();
	}

	@Conditional("isEaglerPlayer")
	@StringProvider(
			text = "Client Version",
			description = "The client version of the Eaglercraft player",
			priority = 17,
			iconName = "code-commit",
			iconFamily = Family.SOLID,
			iconColor = Color.BLUE_GREY
	)
	public String clientVersion(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getEaglerVersionString();
	}

	@Conditional("isEaglerPlayer")
	@StringProvider(
			text = "Protocol Version",
			description = "The protocol version of the Eaglercraft player",
			priority = 18,
			iconName = "code-commit",
			iconFamily = Family.SOLID,
			iconColor = Color.BLUE_GREY
	)
	public String eaglerProtocol(UUID playerUUID) {
		return "" + serverAPI.getEaglerPlayerByUUID(playerUUID).getHandshakeEaglerProtocol();
	}

	@Conditional("isEaglerPlayer")
	@BooleanProvider(
			text = "Rewind",
			description = "Whether the Eaglercraft player is using EaglerXRewind",
			conditionName = "rewind",
			hidden = true
	)
	public boolean rewind(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).isEaglerXRewindPlayer();
	}

	@Conditional("isEaglerPlayer")
	@BooleanProvider(
			text = "Has Brand Desc",
			description = "Whether the Eaglercraft player has a brand description",
			conditionName = "hasBrandDesc",
			hidden = true
	)
	public boolean hasBrandDesc(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getEaglerBrandDesc() != null;
	}

	@Conditional("rewind")
	@StringProvider(
			text = "Rewind Version",
			description = "The rewind version of the Eaglercraft player",
			priority = 19,
			iconName = "code-commit",
			iconFamily = Family.SOLID,
			iconColor = Color.BLUE_GREY
	)
	public String rewindVersion(UUID playerUUID) {
		return "" + serverAPI.getEaglerPlayerByUUID(playerUUID).getRewindProtocolVersion();
	}

	@Conditional("hasBrandDesc")
	@BooleanProvider(
			text = "Hacked Client",
			description = "Whether the Eaglercraft player is using a hacked client",
			priority = 10,
			iconName = "face-angry",
			iconFamily = Family.SOLID,
			iconColor = Color.RED
	)
	public boolean hackedClient(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getEaglerBrandDesc().isHackedClient();
	}

	@Conditional("hasBrandDesc")
	@BooleanProvider(
			text = "Vanilla Eagler",
			description = "Whether the Eaglercraft player is using a vanilla Eaglercraft client",
			priority = 9,
			iconName = "egg",
			iconFamily = Family.SOLID,
			iconColor = Color.GREY
	)
	public boolean vanillaEagler(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getEaglerBrandDesc().isVanillaEagler();
	}

	@Conditional("hasBrandDesc")
	@BooleanProvider(
			text = "Vanilla Minecraft",
			description = "Whether the Eaglercraft player is using vanilla Minecraft",
			priority = 8,
			iconName = "ice-cream",
			iconFamily = Family.SOLID,
			iconColor = Color.GREY
	)
	public boolean vanillaMinecraft(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getEaglerBrandDesc().isVanillaMinecraft();
	}

	@Conditional("hasBrandDesc")
	@BooleanProvider(
			text = "Legacy Client",
			description = "Whether the Eaglercraft player is using a legacy client",
			priority = 7,
			iconName = "dumpster-fire",
			iconFamily = Family.SOLID,
			iconColor = Color.AMBER
	)
	public boolean legacyClient(UUID playerUUID) {
		return serverAPI.getEaglerPlayerByUUID(playerUUID).getEaglerBrandDesc().isLegacyClient();
	}

	private static final String superscript = "\u2070\u00B9\u00B2\u00B3\u2074\u2075\u2076\u2077\u2078\u2079";

	@Conditional("isEaglerPlayer")
	@StringProvider(
			text = "Capabilities",
			description = "The capabilities of the Eaglercraft player",
			priority = 16,
			iconName = "question",
			iconFamily = Family.SOLID,
			iconColor = Color.BLUE_GREY
	)
	public String capabilities(UUID playerUUID) {
		StringBuilder sb = new StringBuilder();

		IEaglerPlayer<?> player = serverAPI.getEaglerPlayerByUUID(playerUUID);

		for (EnumCapabilityType capabilityType : EnumCapabilityType.values()) {
			int cap = player.getCapability(capabilityType);
			if (cap == -1) continue;
			switch (capabilityType) {
				case UPDATE -> sb.append("\u23EB");
				case VOICE -> sb.append("\uD83C\uDF99️");
				case REDIRECT -> sb.append("\uD83D\uDD17");
				case NOTIFICATION -> sb.append("\uD83D\uDD14");
				case PAUSE_MENU -> sb.append("\u23F8\uFE0F");
				case WEBVIEW -> sb.append("\uD83D\uDCC4");
				case COOKIE -> sb.append("\uD83C\uDF6A");
				case EAGLER_IP -> sb.append("\uD83C\uDF10");
				default -> sb.append("\u2754");
			}

			sb.append("\u2C7D");

			char[] capChars = ("" + cap).toCharArray();
			for (char c : capChars) {
				sb.append(superscript.charAt(Character.getNumericValue(c)));
			}
		}


		return sb.toString();
	}
}
