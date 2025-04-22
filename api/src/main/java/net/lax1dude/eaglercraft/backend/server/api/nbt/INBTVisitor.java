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

import java.io.IOException;

import javax.annotation.Nonnull;

public interface INBTVisitor {

	@Nonnull
	public static final INBTVisitor NOP = new NOPVisitor();

	@Nonnull
	INBTVisitor parent();

	@Nonnull
	default INBTVisitor visitRootTag(@Nonnull EnumDataType tagType) throws IOException {
		return parent().visitRootTag(tagType);
	}

	@Nonnull
	default INBTVisitor visitTag(@Nonnull EnumDataType tagType, @Nonnull INBTValue<String> tagName) throws IOException {
		return parent().visitTag(tagType, tagName);
	}

	@Nonnull
	default INBTVisitor visitTagList(@Nonnull EnumDataType itemType, int length) throws IOException {
		return parent().visitTagList(itemType, length);
	}

	default void visitTagByte(byte value) throws IOException {
		parent().visitTagByte(value);
	}

	default void visitTagShort(short value) throws IOException {
		parent().visitTagShort(value);
	}

	default void visitTagInt(int value) throws IOException {
		parent().visitTagInt(value);
	}

	default void visitTagLong(long value) throws IOException {
		parent().visitTagLong(value);
	}

	default void visitTagFloat(float value) throws IOException {
		parent().visitTagFloat(value);
	}

	default void visitTagDouble(double value) throws IOException {
		parent().visitTagDouble(value);
	}

	default void visitTagString(@Nonnull INBTValue<String> str) throws IOException {
		parent().visitTagString(str);
	}

	default void visitTagByteArray(@Nonnull INBTValue<byte[]> value) throws IOException {
		parent().visitTagByteArray(value);
	}

	default void visitTagIntArray(@Nonnull INBTValue<int[]> value) throws IOException {
		parent().visitTagIntArray(value);
	}

	default void visitTagLongArray(@Nonnull INBTValue<long[]> value) throws IOException {
		parent().visitTagLongArray(value);
	}

	default void visitTagEnd() throws IOException {
		parent().visitTagEnd();
	}

}
