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

package net.lax1dude.eaglercraft.backend.server.api.skins;

import javax.annotation.Nullable;

import com.google.common.base.Objects;

public final class TexturesResult {

	public static TexturesResult create(@Nullable String skinURL, @Nullable EnumSkinModel skinModel,
			@Nullable String capeURL) {
		return new TexturesResult(skinURL, skinModel, capeURL);
	}

	private final String skinURL;
	private final EnumSkinModel skinModel;
	private final String capeURL;

	private TexturesResult(String skinURL, EnumSkinModel skinModel, String capeURL) {
		this.skinURL = skinURL;
		this.skinModel = skinModel;
		this.capeURL = capeURL;
	}

	@Nullable
	public String getSkinURL() {
		return skinURL;
	}

	@Nullable
	public EnumSkinModel getSkinModel() {
		return skinModel;
	}

	@Nullable
	public String getCapeURL() {
		return capeURL;
	}

	public int hashCode() {
		int code = 0;
		if(skinURL != null) code += skinURL.hashCode();
		code *= 31;
		if(skinModel != null) code += skinModel.hashCode();
		code *= 31;
		if(capeURL != null) code += capeURL.hashCode();
		return code;
	}

	public boolean equals(Object o) {
		return this == o || ((o instanceof TexturesResult t) && Objects.equal(t.skinURL, skinURL)
				&& Objects.equal(t.skinModel, skinModel) && Objects.equal(t.capeURL, capeURL));
	}

}
