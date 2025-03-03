package com.velocitypowered.natives;

import java.io.Closeable;

public interface Disposable extends Closeable {
	void close();
}
