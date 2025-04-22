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

package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.nbt.EnumDataType;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTVisitor;

public class NBTVisitorWriter implements INBTVisitor {

	private DataOutput dataOutput;
	private byte[] tmp;

	public NBTVisitorWriter(DataOutput dataOutput) {
		this.dataOutput = dataOutput;
	}

	public NBTVisitorWriter(DataOutput dataOutput, byte[] tmp) {
		this.dataOutput = dataOutput;
		this.tmp = tmp;
	}

	NBTVisitorWriter bind(DataOutput dataOutput) {
		this.dataOutput = dataOutput;
		return this;
	}

	@Override
	public INBTVisitor parent() {
		throw new IllegalStateException();
	}

	@Override
	public INBTVisitor visitRootTag(EnumDataType tagType) throws IOException {
		dataOutput.writeByte(tagType.getId());
		dataOutput.writeShort(0);
		return this;
	}

	@Override
	public INBTVisitor visitTag(EnumDataType tagType, INBTValue<String> tagName) throws IOException {
		dataOutput.writeByte(tagType.getId());
		handleWriteValue(tagName);
		return this;
	}

	@Override
	public INBTVisitor visitTagList(EnumDataType itemType, int length) throws IOException {
		dataOutput.writeByte(itemType.getId());
		dataOutput.writeInt(length);
		return this;
	}

	@Override
	public void visitTagByte(byte value) throws IOException {
		dataOutput.writeByte(value);
	}

	@Override
	public void visitTagShort(short value) throws IOException {
		dataOutput.writeShort(value);
	}

	@Override
	public void visitTagInt(int value) throws IOException {
		dataOutput.writeInt(value);
	}

	@Override
	public void visitTagLong(long value) throws IOException {
		dataOutput.writeLong(value);
	}

	@Override
	public void visitTagFloat(float value) throws IOException {
		dataOutput.writeFloat(value);
	}

	@Override
	public void visitTagDouble(double value) throws IOException {
		dataOutput.writeDouble(value);
	}

	@Override
	public void visitTagString(INBTValue<String> str) throws IOException {
		handleWriteValue(str);
	}

	@Override
	public void visitTagByteArray(INBTValue<byte[]> value) throws IOException {
		handleWriteValue(value);
	}

	@Override
	public void visitTagIntArray(INBTValue<int[]> value) throws IOException {
		handleWriteValue(value);
	}

	@Override
	public void visitTagLongArray(INBTValue<long[]> value) throws IOException {
		handleWriteValue(value);
	}

	private void handleWriteValue(INBTValue<?> str) throws IOException {
		if(tmp == null) {
			tmp = new byte[256];
		}
		str.write(dataOutput, tmp);
	}

	@Override
	public void visitTagEnd() throws IOException {
		dataOutput.writeByte(0);
	}

}
