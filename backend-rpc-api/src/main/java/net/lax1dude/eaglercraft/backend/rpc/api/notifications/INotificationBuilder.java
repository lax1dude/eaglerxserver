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

package net.lax1dude.eaglercraft.backend.rpc.api.notifications;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface INotificationBuilder<ComponentObject> {

	@Nonnull
	INotificationBuilder<ComponentObject> copyFrom(@Nonnull INotificationBuilder<ComponentObject> input);

	@Nonnull
	INotificationBuilder<ComponentObject> copyFrom(@Nonnull INotificationBadge input);

	@Nonnull
	UUID getBadgeUUID();

	@Nonnull
	INotificationBuilder<ComponentObject> setBadgeUUID(@Nonnull UUID uuid);

	@Nonnull
	INotificationBuilder<ComponentObject> setBadgeUUIDRandom();

	@Nullable
	ComponentObject getBodyComponent();

	@Nonnull
	INotificationBuilder<ComponentObject> setBodyComponent(@Nullable ComponentObject component);

	@Nonnull
	INotificationBuilder<ComponentObject> setBodyComponent(@Nullable String text);

	@Nullable
	ComponentObject getTitleComponent();

	@Nonnull
	INotificationBuilder<ComponentObject> setTitleComponent(@Nullable ComponentObject component);

	@Nonnull
	INotificationBuilder<ComponentObject> setTitleComponent(@Nullable String text);

	@Nullable
	ComponentObject getSourceComponent();

	@Nonnull
	INotificationBuilder<ComponentObject> setSourceComponent(@Nullable ComponentObject component);

	@Nonnull
	INotificationBuilder<ComponentObject> setSourceComponent(@Nullable String text);

	long getOriginalTimestampSec();

	@Nonnull
	INotificationBuilder<ComponentObject> setOriginalTimestampSec(long timestamp);

	boolean getSilent();

	@Nonnull
	INotificationBuilder<ComponentObject> setSilent(boolean silent);

	boolean getManaged();

	@Nonnull
	INotificationBuilder<ComponentObject> setManaged(boolean managed);

	@Nonnull
	EnumBadgePriority getPriority();

	@Nonnull
	INotificationBuilder<ComponentObject> setPriority(@Nonnull EnumBadgePriority priority);

	@Nullable
	UUID getMainIconUUID();

	@Nonnull
	INotificationBuilder<ComponentObject> setMainIconUUID(@Nullable UUID uuid);

	@Nullable
	UUID getTitleIconUUID();

	@Nonnull
	INotificationBuilder<ComponentObject> setTitleIconUUID(@Nullable UUID uuid);

	int getHideAfterSec();

	@Nonnull
	INotificationBuilder<ComponentObject> setHideAfterSec(int seconds);

	int getExpireAfterSec();

	@Nonnull
	INotificationBuilder<ComponentObject> setExpireAfterSec(int seconds);

	int getBackgroundColor();

	@Nonnull
	INotificationBuilder<ComponentObject> setBackgroundColor(int color);

	@Nonnull
	default INotificationBuilder<ComponentObject> setBackgroundColor(int red, int green, int blue) {
		return setBackgroundColor((red << 16) | (green << 8) | blue);
	}

	int getBodyTxtColorRGB();

	@Nonnull
	INotificationBuilder<ComponentObject> setBodyTxtColorRGB(int color);

	@Nonnull
	default INotificationBuilder<ComponentObject> setBodyTxtColorRGB(int red, int green, int blue) {
		return setBodyTxtColorRGB((red << 16) | (green << 8) | blue);
	}

	int getTitleTxtColorRGB();

	@Nonnull
	INotificationBuilder<ComponentObject> setTitleTxtColorRGB(int color);

	@Nonnull
	default INotificationBuilder<ComponentObject> setTitleTxtColorRGB(int red, int green, int blue) {
		return setTitleTxtColorRGB((red << 16) | (green << 8) | blue);
	}

	int getSourceTxtColorRGB();

	@Nonnull
	INotificationBuilder<ComponentObject> setSourceTxtColorRGB(int color);

	@Nonnull
	default INotificationBuilder<ComponentObject> setSourceTxtColorRGB(int red, int green, int blue) {
		return setSourceTxtColorRGB((red << 16) | (green << 8) | blue);
	}

	@Nonnull
	INotificationBadge buildPacket();

}
