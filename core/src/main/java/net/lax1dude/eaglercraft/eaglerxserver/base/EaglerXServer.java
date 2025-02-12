package net.lax1dude.eaglercraft.eaglerxserver.base;

import net.lax1dude.eaglercraft.eaglerxserver.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.eaglerxserver.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.eaglerxserver.adapter.IPlatform;
import net.lax1dude.eaglercraft.eaglerxserver.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.eaglerxserver.adapter.IPlatformConnectionInitializer;
import net.lax1dude.eaglercraft.eaglerxserver.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.eaglerxserver.adapter.IPlatformPlayerInitializer;

public class EaglerXServer<PlayerObject> implements IEaglerXServerImpl {

	private IPlatform platform;

	public EaglerXServer() {
	}

	@Override
	public void load(IPlatform.Init init) {
		platform = init.getPlatform();
		init.setOnServerEnable(this::enable);
		init.setOnServerDisable(this::disable);
		init.setPipelineInitializer(this::initializePipeline);
		init.setPlayerInitializer(this::initializePlayer);
		if(platform.getType().proxy) {
			loadProxying((IPlatform.InitProxying)init);
		}else {
			loadNonProxying((IPlatform.InitNonProxying)init);
		}
	}

	private void loadProxying(IPlatform.InitProxying init) {
		
	}

	private void loadNonProxying(IPlatform.InitNonProxying init) {
		
	}

	public IPlatform getPlatform() {
		return platform;
	}

	public void enable() {
		
	}

	public void disable() {
		
	}

	public void initializePipeline(IEaglerXServerListener listener, IPlatformConnection conn,
			IPlatformConnectionInitializer<Object> initializer) {

	}

	public void initializePlayer(IPlatformPlayer player, IPlatformPlayerInitializer<Object> initializer) {

	}

}
