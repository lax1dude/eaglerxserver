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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTVisitor;

class NBTContext implements INBTContext, IWrapperFactory {

	private final byte[] buf;
	private final NBTVisitorWriter writer;
	private final ValueString stringValue;
	private final ValueByteArray byteValue;
	private final ValueIntArray intValue;
	private final ValueLongArray longValue;

	NBTContext(int bufferSize) {
		this.buf = new byte[bufferSize];
		this.writer = new NBTVisitorWriter(null, buf);
		this.stringValue = new ValueString(null);
		this.byteValue = new ValueByteArray(null);
		this.intValue = new ValueIntArray(null);
		this.longValue = new ValueLongArray(null);
	}

	@Override
	public void accept(DataInput dataInput, INBTVisitor visitor) throws IOException {
		if (dataInput == null) {
			throw new NullPointerException("dataInput");
		}
		if (visitor == null) {
			throw new NullPointerException("visitor");
		}
		NBTVisitorReader.read(dataInput, visitor, this);
	}

	@Override
	public INBTVisitor createWriter(DataOutput dataOutput) {
		if (dataOutput == null) {
			throw new NullPointerException("dataOutput");
		}
		return writer.bind(dataOutput);
	}

	@Override
	public INBTValue<String> wrapValue(String value) {
		if (value == null) {
			throw new NullPointerException("value");
		}
		return new WrappedString(value);
	}

	@Override
	public INBTValue<byte[]> wrapValue(byte[] value) {
		if (value == null) {
			throw new NullPointerException("value");
		}
		return new WrappedByteArray(value);
	}

	@Override
	public INBTValue<int[]> wrapValue(int[] value) {
		if (value == null) {
			throw new NullPointerException("value");
		}
		return new WrappedIntArray(value);
	}

	@Override
	public INBTValue<long[]> wrapValue(long[] value) {
		if (value == null) {
			throw new NullPointerException("value");
		}
		return new WrappedLongArray(value);
	}

	@Override
	public ValueString wrapStringData(DataInput dataSource) {
		stringValue.reset(dataSource);
		return stringValue;
	}

	@Override
	public ValueByteArray wrapByteData(DataInput dataSource) {
		byteValue.reset(dataSource);
		return byteValue;
	}

	@Override
	public ValueIntArray wrapIntData(DataInput dataSource) {
		intValue.reset(dataSource);
		return intValue;
	}

	@Override
	public ValueLongArray wrapLongData(DataInput dataSource) {
		longValue.reset(dataSource);
		return longValue;
	}

}
