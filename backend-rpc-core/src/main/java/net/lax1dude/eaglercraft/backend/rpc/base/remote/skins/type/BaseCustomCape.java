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

import java.util.Arrays;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;

abstract class BaseCustomCape implements IEaglerPlayerCape {

	private int hashCode;
	private boolean hashZero;

	protected abstract byte[] textureData();

	public int hashCode() {
		if (hashCode == 0 && !hashZero) {
			hashCode = Arrays.hashCode(textureData());
			if (hashCode == 0) {
				hashZero = true;
			}
		}
		return hashCode;
	}

	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof BaseCustomCape other) && (hashCode() == other.hashCode())
				&& Arrays.equals(textureData(), other.textureData()));
	}

}
