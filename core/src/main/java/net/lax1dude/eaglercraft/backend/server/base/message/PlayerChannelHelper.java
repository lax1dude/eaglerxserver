package net.lax1dude.eaglercraft.backend.server.base.message;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageHandler;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.MessageChannel;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageConstants;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class PlayerChannelHelper {

	static final Map<String, String> CHANNEL_MODERN_NAMES = ImmutableMap.copyOf(GamePluginMessageProtocol
			.getAllChannels().stream().collect(Collectors.toMap((k) -> k, GamePluginMessageConstants::getModernName)));

	public static <PlayerObject> Collection<IEaglerXServerMessageChannel<PlayerObject>> getPlayerChannels(EaglerXServer<PlayerObject> server) {
		IEaglerXServerMessageHandler<PlayerObject> handler = (ch, player, data) -> {
			BasePlayerInstance<PlayerObject> basePlayer = player.<BasePlayerInstance<PlayerObject>>getPlayerAttachment();
			if(basePlayer.isEaglerPlayer()) {
				MessageController msgController = basePlayer.asEaglerPlayer().getMessageController();
				if(msgController instanceof LegacyMessageController msgController2) {
					msgController2.readPacket(ch.getLegacyName(), data);
				}
			}
		};
		ImmutableList.Builder<IEaglerXServerMessageChannel<PlayerObject>> playerChannelBuilder = ImmutableList.builder();
		for(String channel : GamePluginMessageProtocol.getAllChannels()) {
			String modernChannel = CHANNEL_MODERN_NAMES.get(channel);
			playerChannelBuilder.add(new MessageChannel<PlayerObject>(channel, modernChannel, handler));
		}
		return playerChannelBuilder.build();
	}

	public static String mapModernName(String chan) {
		String ret = CHANNEL_MODERN_NAMES.get(chan);
		if(ret == null) {
			throw new IllegalStateException("Don't know the modern channel name for: " + chan);
		}
		return ret;
	}

}
