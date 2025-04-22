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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;

public class MissingCape extends BasePresetCape {

	public static final IEaglerPlayerCape MISSING_CAPE = new MissingCape();

	private MissingCape() {
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public boolean isCapeEnabled() {
		return false;
	}

	@Override
	public boolean isCapePreset() {
		return true;
	}

	@Override
	public int getPresetCapeId() {
		return 0;
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		return EnumPresetCapes.NO_CAPE;
	}

	@Override
	public boolean isCapeCustom() {
		return false;
	}

	@Override
	public void getCustomCapePixels_ABGR8_32x32(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
	}

	@Override
	public void getCustomCapePixels_eagler(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
	}

	@Override
	protected int presetId() {
		return 0;
	}

}
