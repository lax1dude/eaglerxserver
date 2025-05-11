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

package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;

class BukkitCommand extends Command {

	private final PlatformPluginBukkit plugin;
	private final IEaglerXServerCommandType<Player> cmd;

	protected BukkitCommand(PlatformPluginBukkit plugin, IEaglerXServerCommandType<Player> cmd) {
		super(cmd.getCommandName(), "EaglerXServer /" + cmd.getCommandName() + " command",
				"/" + cmd.getCommandName() + " [...]", nullFix(cmd.getCommandAliases()));
		String perm = cmd.getPermission();
		if (perm != null) {
			this.setPermission(perm);
		}
		this.plugin = plugin;
		this.cmd = cmd;
	}

	@Override
	public boolean execute(CommandSender var1, String var2, String[] var3) {
		cmd.getHandler().handle(cmd, plugin.getCommandSender(var1), var3);
		return true;
	}

	private static List<String> nullFix(String[] input) {
		return input != null ? Arrays.asList(input) : Collections.emptyList();
	}

}
