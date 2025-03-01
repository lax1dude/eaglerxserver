package net.lax1dude.eaglercraft.backend.server.base.command;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class CommandConfirmCode<PlayerObject> extends EaglerCommand<PlayerObject> {

	public CommandConfirmCode(EaglerXServer<PlayerObject> server) {
		super(server, "confirm-code", "eaglercraft.command.confirmcode", "confirmcode");
	}

	@Override
	public void handle(IEaglerXServerCommandType<PlayerObject> command, IPlatformCommandSender<PlayerObject> sender,
			String[] args) {
		if(args.length != 1) {
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED).end()
					.text("How to use: ").appendTextComponent().beginStyle().color(EnumChatColor.WHITE).end()
					.text("/confirm-code <code>").end().end());
		}else {
			getServer().setServerListConfirmCode(Util.hash2string(Util.sha1(args[0].getBytes(StandardCharsets.US_ASCII))));
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
					.text("Server list 2FA code has been set to: ").appendTextComponent().beginStyle()
					.color(EnumChatColor.GOLD).end().text(args[0]).end().end());
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
					.text("You can now return to the server list site and continue").end());
		}
	}

}
