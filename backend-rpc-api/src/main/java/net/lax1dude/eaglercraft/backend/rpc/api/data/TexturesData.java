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

package net.lax1dude.eaglercraft.backend.rpc.api.data;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

public final class TexturesData {

	@Nonnull
	public static TexturesData create(@Nonnull IEaglerPlayerSkin skin, @Nonnull IEaglerPlayerCape cape) {
		if(skin == null) {
			throw new NullPointerException("skin");
		}
		if(cape == null) {
			throw new NullPointerException("cape");
		}
		return new TexturesData(skin, cape);
	}

	private final IEaglerPlayerSkin skin;
	private final IEaglerPlayerCape cape;

	private TexturesData(IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
		this.skin = skin;
		this.cape = cape;
	}

	@Nonnull
	public IEaglerPlayerSkin getSkin() {
		return skin;
	}

	@Nonnull
	public IEaglerPlayerCape getCape() {
		return cape;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + cape.hashCode();
		result = 31 * result + skin.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TexturesData other))
			return false;
		if (!cape.equals(other.cape))
			return false;
		if (!skin.equals(other.skin))
			return false;
		return true;
	}

}
