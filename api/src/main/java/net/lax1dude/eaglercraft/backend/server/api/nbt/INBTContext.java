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

package net.lax1dude.eaglercraft.backend.server.api.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.Nonnull;

public interface INBTContext {

	void accept(@Nonnull DataInput dataInput, @Nonnull INBTVisitor visitor) throws IOException;

	@Nonnull
	INBTVisitor createWriter(@Nonnull DataOutput dataOutput);

	@Nonnull
	INBTValue<String> wrapValue(@Nonnull String value);

	@Nonnull
	INBTValue<byte[]> wrapValue(@Nonnull byte[] value);

	@Nonnull
	INBTValue<int[]> wrapValue(@Nonnull int[] value);

	@Nonnull
	INBTValue<long[]> wrapValue(@Nonnull long[] value);

}
