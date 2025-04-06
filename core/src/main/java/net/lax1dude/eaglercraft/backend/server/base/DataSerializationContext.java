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
