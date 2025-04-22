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

import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;

class ValueByteArray implements INBTValue<byte[]> {

	private DataInput dataSource;
	private byte[] resolved;
	private boolean done;

	ValueByteArray(DataInput dataSource) {
		this.dataSource = dataSource;
	}

	void reset(DataInput dataSource) {
		this.dataSource = dataSource;
		this.resolved = null;
		this.done = false;
	}

	@Override
	public void mutate(byte[] value) throws IOException {
		if(value == null) {
			throw new NullPointerException("Cannot mutate to a null value");
		}
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			int len = dataSource.readInt();
			if(len < 0) {
				throw new IOException("Invalid length!");
			}
			dataSource.skipBytes(len);
		}
		resolved = value;
	}

	@Override
	public void write(DataOutput dataOutput, byte[] tmp) throws IOException {
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved != null) {
			dataOutput.writeInt(resolved.length);
			dataOutput.write(resolved);
		}else {
			done = true;
			int len = dataSource.readInt();
			if(len < 0) {
				throw new IOException("Invalid length!");
			}
			dataOutput.writeInt(len);
			while(len > 0) {
				int j = Math.min(tmp.length, len);
				dataSource.readFully(tmp, 0, j);
				dataOutput.write(tmp, 0, j);
				len -=j;
			}
		}
	}

	@Override
	public byte[] value() throws IOException {
		if(done) {
			throw new IllegalStateException();
		}
		if(resolved == null) {
			int len = dataSource.readInt();
			if(len < 0) {
				throw new IOException("Invalid length!");
			}
			resolved = new byte[len];
			dataSource.readFully(resolved);
		}
		return resolved;
	}

	void finish() throws IOException {
		if(!done) {
			done = true;
			if(resolved == null) {
				int len = dataSource.readInt();
				if(len < 0) {
					throw new IOException("Invalid length!");
				}
				dataSource.skipBytes(len);
			}
		}
	}

}
