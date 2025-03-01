package net.lax1dude.eaglercraft.backend.server.base.command;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class CommandProtocol<PlayerObject> extends EaglerCommand<PlayerObject> {

	public CommandProtocol(EaglerXServer<PlayerObject> server) {
		super(server, "protocol", "eaglercraft.command.protocol", "eaglerprotocol", "eagler-protocol");
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
				.text("Usage: /protocol <username>").end());
	}

	private void handle(IPlatformCommandSender<PlayerObject> sender, BasePlayerInstance<PlayerObject> player) {
		if(player.isEaglerPlayer()) {
			EaglerPlayerInstance<PlayerObject> eagPlayer = player.asEaglerPlayer();
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
					.text("Connection Type: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
					.text("Eaglercraft").end().end());
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
					.text("Eaglercraft Handshake: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
					.text(Integer.toString(eagPlayer.getHandshakeEaglerProtocol())).end().end());
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
					.text("Eaglercraft Protocol: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
					.text(eagPlayer.getEaglerProtocol().name()).end().end());
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
					.text("Minecraft Protocol: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
					.text(Integer.toString(player.getMinecraftProtocol())).end().end());
		}else {
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
					.text("Connection Type: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
					.text("Minecraft").end().end());
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
					.text("Minecraft Protocol: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
					.text(Integer.toString(player.getMinecraftProtocol())).end().end());
		}
	}

}
