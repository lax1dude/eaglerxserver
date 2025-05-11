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

package net.lax1dude.eaglercraft.backend.server.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;

class VelocityCommand implements SimpleCommand {

	private final PlatformPluginVelocity plugin;
	private final IEaglerXServerCommandType<Player> cmd;

	VelocityCommand(PlatformPluginVelocity plugin, IEaglerXServerCommandType<Player> cmd) {
		this.plugin = plugin;
		this.cmd = cmd;
	}

	@Override
	public void execute(Invocation arg0) {
		cmd.getHandler().handle(cmd, plugin.getCommandSender(arg0.source()), arg0.arguments());
	}

	@Override
	public boolean hasPermission(Invocation invocation) {
		String perm = cmd.getPermission();
		return perm == null || invocation.source().hasPermission(perm);
	}

	public CommandMeta register() {
		CommandManager cmdManager = plugin.proxy().getCommandManager();
		CommandMeta.Builder builder = cmdManager.metaBuilder(cmd.getCommandName());
		builder.plugin(plugin);
		String[] aliases = cmd.getCommandAliases();
		if (aliases != null && aliases.length > 0) {
			builder.aliases(aliases);
		}
		CommandMeta ret = builder.build();
		cmdManager.register(ret, this);
		return ret;
	}

}
