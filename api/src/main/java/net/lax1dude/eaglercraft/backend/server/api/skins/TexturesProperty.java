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

import javax.annotation.Nonnull;

public final class TexturesProperty {

	public static TexturesProperty create(@Nonnull String value, @Nonnull String signature) {
		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		if (signature == null) {
			throw new IllegalArgumentException("signature cannot be null");
		}
		return new TexturesProperty(value, signature);
	}

	private final String value;
	private final String signature;

	private TexturesProperty(String value, String signature) {
		this.value = value;
		this.signature = signature;
	}

	@Nonnull
	public String getValue() {
		return value;
	}

	@Nonnull
	public String getSignature() {
		return signature;
	}

	public int hashCode() {
		return value.hashCode() * 31 + signature.hashCode();
	}

	public boolean equals(Object o) {
		return this == o || ((o instanceof TexturesProperty t) && t.value.equals(value)
				&& t.signature.equals(signature));
	}

}
