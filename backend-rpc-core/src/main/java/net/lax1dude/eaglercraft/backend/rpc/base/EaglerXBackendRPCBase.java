package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.Collections;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCImpl;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform.Init;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IScheduler;
import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.IEaglerRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.base.local.EaglerXBackendRPCLocal;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.EaglerXBackendRPCRemote;

public abstract class EaglerXBackendRPCBase<PlayerObject> extends RPCAttributeHolder
		implements IBackendRPCImpl<PlayerObject>, IEaglerRPCFactory, IEaglerXBackendRPC<PlayerObject> {

	private boolean hasStartedLoading;
	protected IPlatform<PlayerObject> platform;
	protected EnumPlatformType platformType;
	protected Class<PlayerObject> playerClass;
	protected Set<Class<?>> playerClassSet;
	protected SchedulerExecutors schedulerExecutors;
	protected FutureTimeoutLoop timeoutLoop;
	protected int baseRequestTimeout = 10;

	public static <PlayerObject> EaglerXBackendRPCBase<PlayerObject> init() {
		if(detectAPI()) {
			return new EaglerXBackendRPCLocal<>();
		}else {
			return new EaglerXBackendRPCRemote<>();
		}
	}

	private static boolean detectAPI() {
		try {
			Class.forName("net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI");
			return true;
		}catch(ClassNotFoundException ex) {
			return false;
		}
	}

	@Override
	public final void load(Init<PlayerObject> platf) {
		if(hasStartedLoading) {
			throw new IllegalStateException("EaglerXBackendRPC is already loading");
		}
		hasStartedLoading = true;

		platform = platf.getPlatform();
		playerClass = platform.getPlayerClass();
		playerClassSet = Collections.singleton(playerClass);

		schedulerExecutors = new SchedulerExecutors(platform.getScheduler());
		timeoutLoop = new FutureTimeoutLoop(platform.getScheduler());

		switch(platform.getType()) {
		case BUKKIT:
			platformType = EnumPlatformType.BUKKIT;
			break;
		default:
			throw new IllegalStateException();
		}

		load0(platf);

		platform.eventDispatcher().setAPI(this);
		APIFactoryImpl.INSTANCE.initialize(playerClass, this);
	}

	protected abstract void load0(Init<PlayerObject> platf);

	protected EaglerXBackendRPCBase() {
	}

	@Override
	public EnumPlatformType getPlatformType() {
		return platformType;
	}

	@Override
	public Class<PlayerObject> getPlayerClass() {
		return playerClass;
	}

	@Override
	public Set<Class<?>> getPlayerTypes() {
		return playerClassSet;
	}

	@Override
	public <T> IEaglerXBackendRPC<T> getAPI(Class<T> playerClass) {
		if(!playerClass.isAssignableFrom(this.playerClass)) {
			throw new ClassCastException("Class " + this.playerClass.getName() + " cannot be cast to " + playerClass.getName());
		}
		return (IEaglerXBackendRPC<T>) this;
	}

	@Override
	public IEaglerXBackendRPC<?> getDefaultAPI() {
		return this;
	}

	@Override
	public IEaglerRPCFactory getFactory() {
		return this;
	}

	public IPlatform<PlayerObject> getPlatform() {
		return platform;
	}

	@Override
	public IScheduler getScheduler() {
		return platform.getScheduler();
	}

	@Override
	public void setBaseRequestTimeout(int seconds) {
		baseRequestTimeout = seconds;
	}

	@Override
	public int getBaseRequestTimeout() {
		return baseRequestTimeout;
	}

	public IPlatformLogger logger() {
		return platform.logger();
	}

	public SchedulerExecutors schedulerExecutors() {
		return schedulerExecutors;
	}

	public FutureTimeoutLoop timeoutLoop() {
		return timeoutLoop;
	}

	public <V> RPCActiveFuture<V> createFuture(int expiresAfter) {
		long now = System.nanoTime();
		RPCActiveFuture<V> ret = new RPCActiveFuture<V>(schedulerExecutors, now + expiresAfter * 1000000000l);
		timeoutLoop.addFuture(now, ret);
		return ret;
	}

}
