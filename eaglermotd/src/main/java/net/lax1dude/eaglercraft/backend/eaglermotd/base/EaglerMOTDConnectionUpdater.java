package net.lax1dude.eaglercraft.backend.eaglermotd.base;

import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;

public class EaglerMOTDConnectionUpdater {

	public final EaglerMOTDConfiguration conf;
	public final String listenerName;
	public final IMOTDConnection motd;
	
	public MessagePoolEntry currentMessage = null;
	public int messageTimeTimer = 0;
	public int messageIntervalTimer = 0;
	public int currentFrame = 0;
	public int ageTimer = 0;

	public EaglerMOTDConnectionUpdater(EaglerMOTDConfiguration conf, IMOTDConnection motd) {
		this.conf = conf;
		this.motd = motd;
		this.listenerName = motd.getListenerInfo().getName();
	}

	public boolean execute() {
		MessagePool p = conf.messagePools.get(listenerName);
		if(p == null) {
			return false;
		}
		
		messageTimeTimer = 0;
		messageIntervalTimer = 0;
		currentMessage = p.pickDefault();
		currentFrame = 0;
		
		currentMessage.frames.get(currentFrame).update(motd);
		if(currentMessage.interval > 0 || currentMessage.next != null) {
			motd.setMaxAge(conf.close_socket_after * 50l);
			return true;
		}else {
			return false;
		}
	}

	public boolean tick() {
		ageTimer++;
		if(!motd.isConnected()) {
			return false;
		}
		if(ageTimer > conf.close_socket_after) {
			motd.disconnect();
			return false;
		}
		messageTimeTimer++;
		if(messageTimeTimer >= currentMessage.timeout) {
			if(currentMessage.next != null) {
				if(currentMessage.next.equalsIgnoreCase("any") || currentMessage.next.equalsIgnoreCase("random")) {
					MessagePool p = conf.messagePools.get(listenerName);
					if(p == null) {
						motd.disconnect();
						return false;
					}
					if(p.messagePool.size() > 1) {
						MessagePoolEntry m;
						do {
							m = p.pickNew();
						}while(m == currentMessage);
						currentMessage = m;
					}
				}else {
					if(!changeMessageTo(listenerName, currentMessage.next)) {
						boolean flag = false;
						for(String s : conf.messages.keySet()) {
							if(!s.equalsIgnoreCase(listenerName) && changeMessageTo(s, currentMessage.next)) {
								flag = true;
								break;
							}
						}
						if(!flag) {
							motd.disconnect();
							return false;
						}
					}
				}
				if(currentMessage == null) {
					motd.disconnect();
					return false;
				}
				messageTimeTimer = 0;
				messageIntervalTimer = 0;
				currentFrame = 0;
				currentMessage.frames.get(currentFrame).update(motd);
				motd.sendToUser();
				if(currentMessage.next == null && currentMessage.interval <= 0) {
					motd.disconnect();
					return false;
				}else {
					return true;
				}
			}else {
				this.motd.disconnect();
				return false;
			}
		}else {
			messageIntervalTimer++;
			if(currentMessage.interval > 0 && messageIntervalTimer >= currentMessage.interval) {
				messageIntervalTimer = 0;
				if(currentMessage.frames.size() > 1) {
					++currentFrame;
					if(currentFrame >= currentMessage.frames.size()) {
						currentFrame = 0;
					}
					currentMessage.frames.get(currentFrame).update(motd);
					motd.sendToUser();
				}
			}
			if(currentMessage.next == null && currentMessage.interval <= 0) {
				motd.disconnect();
				return false;
			}else {
				return true;
			}
		}
	}
	
	private boolean changeMessageTo(String group, String s) {
		if(group == null || s == null) {
			return false;
		}
		List<MessagePoolEntry> lst = conf.messages.get(group);
		if(lst == null) {
			return false;
		}
		for(MessagePoolEntry m : lst) {
			if(m.name.equalsIgnoreCase(s)) {
				currentMessage = m;
				return true;
			}
		}
		return false;
	}

	public void close() {
		motd.disconnect();
	}

}
