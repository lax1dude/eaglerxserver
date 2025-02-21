package net.lax1dude.eaglercraft.backend.server.base.command;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class CommandDomain<PlayerObject> extends EaglerCommand<PlayerObject> {

	public CommandDomain(EaglerXServer<PlayerObject> server) {
		super(server, "domain", "eaglercraft.command.domain");
	}

	@Override
	public void handle(IEaglerXServerCommandType<PlayerObject> command, IPlatformCommandSender<PlayerObject> sender,
			String[] args) {
		if(args.length == 0 && sender.isPlayer()) {
			handle(sender, sender.asPlayer().getPlayerAttachment());
			return;
		}else if(args.length == 1) {
			BasePlayerInstance<PlayerObject> player = getServer().getPlayerByName(args[0]);
			if(player != null) {
				handle(sender, player);
				return;
			}else {
				sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED).end()
						.text("Player \"" + args[0] + "\" was not found").end());
			}
		}else {
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED).end()
					.text("Invalid number of arguments").end());
		}
		sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED).end()
				.text("Usage: /domain <username>").end());
	}

	private void handle(IPlatformCommandSender<PlayerObject> sender, BasePlayerInstance<PlayerObject> player) {
		if(player.isEaglerPlayer()) {
			String origin = player.asEaglerPlayer().getWebSocketHeader(EnumWebSocketHeader.HEADER_ORIGIN);
			if(origin != null) {
				if("null".equals(origin)) {
					sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.BLUE).end()
							.text("The player " + player.getUsername() + " is using an offline download").end());
				}else {
					sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.BLUE).end()
							.text("The player " + player.getUsername() + "'s domain is \"" + origin + "\"").end());
				}
			}else {
				sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.BLUE).end()
						.text("The player " + player.getUsername() + " is using a browser that did not send an origin header").end());
			}
		}else {
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.BLUE).end()
					.text("The player " + player.getUsername() + " is not using Eaglercraft").end());
		}
	}

}
