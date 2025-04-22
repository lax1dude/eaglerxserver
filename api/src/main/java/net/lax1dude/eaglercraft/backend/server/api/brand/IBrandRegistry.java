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

package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IBrandRegistry {

	@Nonnull
	static final UUID BRAND_NULL = new UUID(0L, 0L);

	@Nonnull
	static final UUID BRAND_VANILLA = new UUID(0x1DCE015CD384374EL, 0x85030A4DE95E5736L);

	@Nonnull
	static final UUID BRAND_EAGLERCRAFTX_V4 = new UUID(0x4448369E4E873621L, 0x94F5E28EEB160524L);

	@Nonnull
	static final UUID BRAND_EAGLERCRAFTX_LEGACY =  new UUID(0x71D0C81201C2366AL, 0xA0D23D9AA10846EBL);

	@Nonnull
	static final UUID BRAND_EAGLERCRAFT_1_12 =  new UUID(0x522B2CE5C9B936CFL, 0xBE7C5D90F55E631AL);

	@Nonnull
	UUID getBrandUUIDClient(@Nonnull String brandString);

	@Nonnull
	UUID getBrandUUIDClientLegacy(@Nonnull String brandString);

	default void registerBrand(@Nonnull UUID brandUUID, @Nonnull String brandDesc) {
		registerBrand(brandUUID, brandDesc, false, false);
	}

	default void registerBrand(@Nonnull UUID brandUUID, @Nonnull String brandDesc, boolean hacked) {
		registerBrand(brandUUID, brandDesc, hacked, false);
	}

	void registerBrand(@Nonnull UUID brandUUID, @Nonnull String brandDesc, boolean hacked, boolean legacy);

	void unregisterBrand(@Nonnull UUID brandUUID);

	@Nullable
	IBrandRegistration lookupRegisteredBrand(@Nonnull UUID brandUUID);

}
