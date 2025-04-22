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

package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;

class BukkitPlayer implements IPlatformPlayer<Player> {

	private static final VarHandle CONFIRM_TASK_HANDLE;

	private static final boolean PAPER_VIEW_DISTANCE_SUPPORT;
	private static final Method PAPER_SET_VIEW_DISTANCE_SEND;
	private static final Method PAPER_SET_VIEW_DISTANCE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			CONFIRM_TASK_HANDLE = l.findVarHandle(BukkitPlayer.class, "confirmTask", BukkitTask.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
		boolean support;
		Method viewDistance;
		Method viewDistanceSend;
		try {
			viewDistanceSend = Player.class.getMethod("setSendViewDistance", int.class);
			viewDistance = null;
			support = true;
		}catch(NoSuchMethodException ex) {
			viewDistanceSend = null;
			try {
				viewDistance = Player.class.getMethod("setViewDistance", int.class);
				support = true;
			}catch(NoSuchMethodException exx) {
				viewDistance = null;
				support = false;
			}
		}
		PAPER_VIEW_DISTANCE_SUPPORT = support;
		PAPER_SET_VIEW_DISTANCE_SEND = viewDistanceSend;
		PAPER_SET_VIEW_DISTANCE = viewDistance;
	}

	private final PlatformPluginBukkit plugin;
	private final Player player;
	Object attachment;
	volatile BukkitTask confirmTask;

	BukkitPlayer(PlatformPluginBukkit plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}

	BukkitTask xchgConfirmTask() {
		return (BukkitTask)CONFIRM_TASK_HANDLE.getAndSetAcquire(this, null);
	}

	@Override
	public Player getPlayerObject() {
		return player;
	}

	@Override
	public <T> T getAttachment() {
		return (T) attachment;
	}

	@Override
	public String getUsername() {
		return player.getName();
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public boolean isConnected() {
		return player.isOnline();
	}

	@Override
	public void sendData(String channel, byte[] message) {
		player.sendPluginMessage(plugin, channel, message);
	}

	@Override
	public boolean isSetViewDistanceSupportedPaper() {
		return PAPER_VIEW_DISTANCE_SUPPORT;
	}

	@Override
	public void setViewDistancePaper(int distance) {
		if(PAPER_SET_VIEW_DISTANCE_SEND != null) {
			try {
				PAPER_SET_VIEW_DISTANCE_SEND.invoke(player, distance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Reflection failed!");
			}
		}else if(PAPER_SET_VIEW_DISTANCE != null) {
			try {
				PAPER_SET_VIEW_DISTANCE.invoke(player, distance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Reflection failed!");
			}
		}
	}

}
