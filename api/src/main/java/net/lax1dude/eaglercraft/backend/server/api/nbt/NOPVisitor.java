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

final class NOPVisitor implements INBTVisitor {
	NOPVisitor() {}
	@Override
	public INBTVisitor parent() {
		throw new IllegalStateException();
	}
	@Override
	public INBTVisitor visitRootTag(EnumDataType tagType) throws IOException {
		return this;
	}
	@Override
	public INBTVisitor visitTag(EnumDataType tagType, INBTValue<String> tagName) throws IOException {
		return this;
	}
	@Override
	public INBTVisitor visitTagList(EnumDataType itemType, int length) throws IOException {
		return this;
	}
	@Override
	public void visitTagByte(byte value) throws IOException { }
	@Override
	public void visitTagShort(short value) throws IOException { }
	@Override
	public void visitTagInt(int value) throws IOException { }
	@Override
	public void visitTagLong(long value) throws IOException { }
	@Override
	public void visitTagFloat(float value) throws IOException { }
	@Override
	public void visitTagDouble(double value) throws IOException { }
	@Override
	public void visitTagString(INBTValue<String> str) throws IOException { }
	@Override
	public void visitTagByteArray(INBTValue<byte[]> value) throws IOException { }
	@Override
	public void visitTagIntArray(INBTValue<int[]> value) throws IOException { }
	@Override
	public void visitTagLongArray(INBTValue<long[]> value) throws IOException { }
	@Override
	public void visitTagEnd() throws IOException { }
}
