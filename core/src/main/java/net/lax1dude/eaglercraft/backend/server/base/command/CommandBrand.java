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

package net.lax1dude.eaglercraft.backend.server.base.command;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class CommandBrand<PlayerObject> extends EaglerCommand<PlayerObject> {

	public CommandBrand(EaglerXServer<PlayerObject> server) {
		super(server, "brand", "eaglercraft.command.brand", "eaglerbrand", "eagler-brand");
	}

	@Override
	public void handle(IEaglerXServerCommandType<PlayerObject> command, IPlatformCommandSender<PlayerObject> sender,
			String[] args) {
		if(args.length == 0 && sender.isPlayer()) {
			handle(sender, sender.asPlayer().getPlayerAttachment(), true, true, true);
			return;
		}else if(args.length == 1) {
			BasePlayerInstance<PlayerObject> player = getServer().getPlayerByName(args[0]);
			if(player != null) {
				handle(sender, player, true, true, true);
				return;
			}else {
				sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED).end()
						.text("Player \"" + args[0] + "\" was not found").end());
			}
		}else if(args.length == 2) {
			BasePlayerInstance<PlayerObject> player = getServer().getPlayerByName(args[1]);
			if(player != null) {
				String s = args[0].toLowerCase();
				boolean a = "name".equals(s);
				boolean b = !a && "uuid".equals(s);
				boolean c = !a && !b && "mc".equals(s);
				if(a || b || c) {
					handle(sender, player, a, b, c);
					return;
				}else {
					sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED).end()
							.text("Unknown brand lookup type \"" + args[0] + "\"").end());
				}
			}else {
				sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED).end()
						.text("Player \"" + args[1] + "\" was not found").end());
			}
		}else {
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED).end()
					.text("Invalid number of arguments").end());
		}
		sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED).end()
				.text("Usage: /brand [name|uuid|mc] <username>").end());
	}

	private void handle(IPlatformCommandSender<PlayerObject> sender, BasePlayerInstance<PlayerObject> player,
			boolean name, boolean uuid, boolean mc) {
		if(name) {
			if(player.isEaglerPlayer()) {
				EaglerPlayerInstance<PlayerObject> eagPlayer = player.asEaglerPlayer();
				sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
						.text("Eagler Client Brand: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
						.text(eagPlayer.getEaglerBrandString()).end().end());
				sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
						.text("Eagler Client Version: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
						.text(eagPlayer.getEaglerVersionString()).end().end());
			}else {
				sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
						.text("Player is not using Eaglercraft").end());
			}
		}
		if(uuid) {
			UUID brandUUID = player.getEaglerBrandUUID();
			IBrandRegistration registeredBrand = getServer().getBrandService().lookupRegisteredBrand(brandUUID);
			if(registeredBrand != null) {
				sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
						.text("Eagler Client UUID: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
						.text(brandUUID.toString()).end().appendTextComponent().beginStyle().color(EnumChatColor.AQUA)
						.end().text(" (").end().appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
						.text(registeredBrand.getBrandDesc()).end().appendTextComponent().beginStyle()
						.color(EnumChatColor.AQUA).end().text(")").end().end());
			}else {
				sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
						.text("Eagler Client UUID: ").appendTextComponent().beginStyle().color(EnumChatColor.GOLD).end()
						.text(brandUUID.toString()).end().appendTextComponent().beginStyle().color(EnumChatColor.AQUA)
						.end().text(" (Unknown)").end().end());
			}
		}
		if(mc) {
			String mcBrand = player.getMinecraftBrand();
			sender.sendMessage(getChatBuilder().buildTextComponent().beginStyle().color(EnumChatColor.AQUA).end()
					.text("Minecraft Client Brand: ").appendTextComponent().beginStyle()
					.color(mcBrand != null ? EnumChatColor.GOLD : EnumChatColor.AQUA).end()
					.text(mcBrand != null ? mcBrand : "(null)").end().end());
		}
	}

}
