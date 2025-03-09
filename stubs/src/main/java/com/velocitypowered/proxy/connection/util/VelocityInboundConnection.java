package com.velocitypowered.proxy.connection.util;

import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.proxy.connection.MinecraftConnection;

// For performance, we will extract the MinecraftConnection through this interface
public interface VelocityInboundConnection extends InboundConnection {

	MinecraftConnection getConnection();

}
