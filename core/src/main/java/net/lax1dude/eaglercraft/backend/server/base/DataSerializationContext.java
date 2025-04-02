package net.lax1dude.eaglercraft.backend.server.base;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayInputStream;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.ReusableByteArrayOutputStream;

public class DataSerializationContext {

	public final ReusableByteArrayInputStream byteInputStreamSingleton = new ReusableByteArrayInputStream();
	public final ReusableByteArrayOutputStream byteOutputStreamSingleton = new ReusableByteArrayOutputStream();
	public final DataInputStream inputStreamSingleton = new DataInputStream(byteInputStreamSingleton);
	public final DataOutputStream outputStreamSingleton = new DataOutputStream(byteOutputStreamSingleton);
	public final AtomicBoolean inputStreamLock = new AtomicBoolean(false);
	public final AtomicBoolean outputStreamLock = new AtomicBoolean(false);

}
