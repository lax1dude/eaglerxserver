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

package net.lax1dude.eaglercraft.backend.server.bungee;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

class BungeeCommand extends Command {

	private final PlatformPluginBungee plugin;
	private final IEaglerXServerCommandType<ProxiedPlayer> cmd;

	public BungeeCommand(PlatformPluginBungee plugin, IEaglerXServerCommandType<ProxiedPlayer> cmd) {
		super(cmd.getCommandName(), cmd.getPermission(), nullFix(cmd.getCommandAliases()));
		this.plugin = plugin;
		this.cmd = cmd;
	}

	@Override
	public void execute(CommandSender arg0, String[] arg1) {
		cmd.getHandler().handle(cmd, plugin.getCommandSender(arg0), arg1);
	}

	private static String[] nullFix(String[] input) {
		return input != null ? input : new String[0];
	}

}
