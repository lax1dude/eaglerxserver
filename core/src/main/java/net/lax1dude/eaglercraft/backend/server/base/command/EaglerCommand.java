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

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public abstract class EaglerCommand<PlayerObject>
		implements IEaglerXServerCommandType<PlayerObject>, IEaglerXServerCommandHandler<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final String name;
	private final String permission;
	private final String[] aliases;

	public EaglerCommand(EaglerXServer<PlayerObject> server, String name, String permission, String... aliases) {
		this.server = server;
		this.name = name;
		this.permission = permission;
		this.aliases = aliases;
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String getPermission() {
		return permission;
	}

	@Override
	public String[] getCommandAliases() {
		return aliases;
	}

	public EaglerXServer<PlayerObject> getServer() {
		return server;
	}

	protected IPlatformComponentHelper getChatHelper() {
		return server.getPlatform().getComponentHelper();
	}

	protected IPlatformComponentBuilder getChatBuilder() {
		return server.getPlatform().getComponentHelper().builder();
	}

	@Override
	public IEaglerXServerCommandHandler<PlayerObject> getHandler() {
		return this;
	}

}
