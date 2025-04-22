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

package net.lax1dude.eaglercraft.backend.server.base;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayInputStream;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayOutputStream;

public class DataSerializationContext {

	public final ReusableByteArrayInputStream byteInputStreamSingleton = new ReusableByteArrayInputStream();
	public final ReusableByteArrayOutputStream byteOutputStreamSingleton = new ReusableByteArrayOutputStream();
	public final DataInputStream inputStreamSingleton = new DataInputStream(byteInputStreamSingleton);
	public final DataOutputStream outputStreamSingleton = new DataOutputStream(byteOutputStreamSingleton);
	public final byte[] outputTempBuffer = new byte[512];

	private volatile int inputStreamLock;
	private volatile int outputStreamLock;

	private static final VarHandle IS_LOCK_HANDLE;
	private static final VarHandle OS_LOCK_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			IS_LOCK_HANDLE = l.findVarHandle(DataSerializationContext.class, "inputStreamLock", int.class);
			OS_LOCK_HANDLE = l.findVarHandle(DataSerializationContext.class, "outputStreamLock", int.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public final boolean aquireInputStream() {
		return (int) IS_LOCK_HANDLE.compareAndExchangeAcquire(this, 0, 1) == 0;
	}

	public final void releaseInputStream() {
		IS_LOCK_HANDLE.setRelease(this, 0);
	}

	public final boolean aquireOutputStream() {
		return (int) OS_LOCK_HANDLE.compareAndExchangeAcquire(this, 0, 1) == 0;
	}

	public final void releaseOutputStream() {
		OS_LOCK_HANDLE.setRelease(this, 0);
	}

}
