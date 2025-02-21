package net.lax1dude.eaglercraft.backend.server.base.command;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class CommandVersion<PlayerObject> extends EaglerCommand<PlayerObject> {

	public CommandVersion(EaglerXServer<PlayerObject> server) {
		super(server, "eaglerxserver", "eaglercraft.command.version");
	}

	@Override
	public void handle(IEaglerXServerCommandType<PlayerObject> command, IPlatformCommandSender<PlayerObject> sender,
			String[] args) {
		sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
				.text("Server Brand: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
				.text(getServer().getServerBrand()).end().end());
		sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
				.text("Server Version: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
				.text(getServer().getServerVersion()).end().end());
		sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
				.text("Server Platform: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
				.text(getServer().getPlatformType().getName()).end().end());
	}

}
