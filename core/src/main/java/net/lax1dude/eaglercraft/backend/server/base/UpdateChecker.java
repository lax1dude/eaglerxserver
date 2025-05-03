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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumClickAction;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings.ConfigDataUpdateChecker;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.BufferUtils;
import net.lax1dude.eaglercraft.backend.server.util.SemanticVersion;

public class UpdateChecker {

	private static final VarHandle AVAILABLE_HANDLE;

	static {
		try {
			AVAILABLE_HANDLE = MethodHandles.lookup().findVarHandle(UpdateChecker.class, "available", String.class);
		} catch (ReflectiveOperationException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private final EaglerXServer<?> server;
	private final ConfigDataUpdateChecker config;
	private volatile String available;
	private IPlatformTask checkTask;

	public UpdateChecker(EaglerXServer<?> server, ConfigDataUpdateChecker config) {
		this.server = server;
		this.config = config;
	}

	public void handleEnable() {
		if (EaglerXServerVersion.UPDATE_CHECK != null && config.isEnableUpdateChecker()) {
			check();
			int period = config.getCheckForServerUpdateEvery();
			if (period > 0) {
				checkTask = server.getPlatform().getScheduler().executeAsyncRepeatingTask(this::check, period * 1000l,
						period * 1000l);
			}
		}
	}

	public void handleDisable() {
		if (EaglerXServerVersion.UPDATE_CHECK != null && config.isEnableUpdateChecker()) {
			if (checkTask != null) {
				checkTask.cancel();
				checkTask = null;
			}
		}
	}

	private void check() {
		server.getInternalHTTPClient().asyncRequest("GET", URI.create(EaglerXServerVersion.UPDATE_CHECK), (res) -> {
			try {
				if (res.exception != null) {
					server.logger().error("Could not check for server updates: " + res.exception.toString());
				} else if (res.code != 200) {
					server.logger().error("Could not check for server updates, response code " + res.code);
				} else if (res.data == null || res.data.readableBytes() == 0) {
					server.logger().error("Could not check for server updates, received empty response");
				} else {
					String str = BufferUtils.readCharSequence(res.data, res.data.readableBytes(),
							StandardCharsets.UTF_8).toString().trim();
					SemanticVersion ver;
					try {
						ver = SemanticVersion.parse(str);
					} catch (IllegalArgumentException ex) {
						server.logger().error("Could not check for server updates, response invalid");
						return;
					}
					if (ver.greaterThan(EaglerXServerVersion.UPDATE_VERSION)) {
						AVAILABLE_HANDLE.setRelease(this, ver.toString());
						server.logger().warn("=============================================");
						server.logger().warn("=============================================");
						server.logger().warn("");
						server.logger().warn(" Updates are available for EaglerXServer");
						server.logger().warn(" ---------------------------------------");
						server.logger().warn("");
						server.logger().warn("   :< Active: " + EaglerXServerVersion.UPDATE_VERSION);
						server.logger().warn("");
						server.logger().warn("   :> Latest: " + ver);
						server.logger().warn("");
						server.logger().warn(" " + EaglerXServerVersion.UPDATE_LINK);
						server.logger().warn(" " + EaglerXServerVersion.UPDATE_LINE);
						server.logger().warn("");
						server.logger().warn("=============================================");
						server.logger().warn("=============================================");
					} else {
						AVAILABLE_HANDLE.setRelease(this, null);
						server.logger().info("You are running the latest version of EaglerXServer");
					}
				}
			} finally {
				if (res.data != null) {
					res.data.release();
				}
			}
		});
	}

	public void sendUpdateMessage(IPlatformPlayer<?> playerObj) {
		if (EaglerXServerVersion.UPDATE_CHECK != null && config.isUpdateChatMessages()) {
			String avail = (String) AVAILABLE_HANDLE.getAcquire(this);
			if (avail != null) {
				server.getPlatform().getScheduler().executeAsyncDelayed(() -> {
					if (playerObj.isConnected()) {
						playerObj.sendMessage(server.componentBuilder().buildTextComponent().beginStyle()
								.color(EnumChatColor.GOLD).end().text("[EaglerXServer]").appendTextComponent()
								.beginStyle().color(EnumChatColor.AQUA).end().text(" An updated version is available: ")
								.end().appendTextComponent().beginStyle().color(EnumChatColor.YELLOW).underline(true)
								.end().beginClickEvent().clickAction(EnumClickAction.OPEN_URL)
								.clickValue(EaglerXServerVersion.UPDATE_LINK).end().text(avail).end().end());
					}
				}, 2000l);
			}
		}
	}

}
