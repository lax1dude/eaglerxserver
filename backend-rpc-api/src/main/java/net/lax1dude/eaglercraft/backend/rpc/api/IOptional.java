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

package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IOptional<T> {

	boolean isSuccess();

	@Nullable
	@SuppressWarnings("unchecked")
	default T orNull() {
		return isSuccess() ? (T) this : null;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default T orDefault(@Nullable T defaultValue) {
		if (isSuccess()) {
			return (T) this;
		} else {
			return defaultValue;
		}
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default T orDefault(@Nonnull Supplier<T> defaultValue) {
		if (isSuccess()) {
			return (T) this;
		} else {
			return defaultValue.get();
		}
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	default T orThrow() throws IllegalStateException {
		if (isSuccess()) {
			return (T) this;
		} else {
			throw new IllegalStateException("Resulting value is not successful");
		}
	}

}
