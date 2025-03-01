package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.SupervisorDataVoid;

public interface ISupervisorRPCHandler {

	<In extends ISupervisorData, Out extends ISupervisorData> void registerProcedure(String name,
			Class<In> inputType, Class<Out> outputType, ISupervisorProc<In, Out> proc);

	void unregisterProcedure(String name);

	default <In extends ISupervisorData, Out extends ISupervisorData> void registerProcedureSync(String name,
			Class<In> inputType, Class<Out> outputType, ISupervisorProcSync<In, Out> callable) {
		registerProcedure(name, inputType, outputType, callable);
	}

	default <In extends ISupervisorData> void registerProcedureVoid(String name, Class<In> inputType,
			ISupervisorProcVoid<In> callable) {
		registerProcedure(name, inputType, SupervisorDataVoid.class, callable);
	}

	default <In extends ISupervisorData> void registerProcedureSyncVoid(String name, Class<In> inputType,
			ISupervisorProcSyncVoid<In> callable) {
		registerProcedure(name, inputType, SupervisorDataVoid.class, callable);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAtPlayer(String username,
			String procedureName, int timeout, In input, Consumer<Out> output);

	default <In extends ISupervisorData> void invokeAtPlayerVoid(String username, String procedureName, int timeout,
			In input, IVoidCallback onComplete) {
		invokeAtPlayer(username, procedureName, timeout, input, onComplete != null ? (Consumer<SupervisorDataVoid>) (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAtPlayerVoid(String username, String procedureName, In input) {
		invokeAtPlayerVoid(username, procedureName, 0, input, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAtPlayer(UUID player,
			String procedureName, int timeout, In input, Consumer<Out> output);

	default <In extends ISupervisorData> void invokeAtPlayerVoid(UUID player, String procedureName, int timeout,
			In input, IVoidCallback onComplete) {
		invokeAtPlayer(player, procedureName, timeout, input, onComplete != null ? (Consumer<SupervisorDataVoid>) (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAtPlayerVoid(UUID player, String procedureName, In input) {
		invokeAtPlayerVoid(player, procedureName, 0, input, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAtNode(int nodeId,
			String procedureName, int timeout, In input, Consumer<Out> output);

	default <In extends ISupervisorData> void invokeAtNodeVoid(int nodeId, String procedureName, int timeout,
			In input, IVoidCallback onComplete) {
		invokeAtNode(nodeId, procedureName, timeout, input, onComplete != null ? (Consumer<SupervisorDataVoid>) (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAtNodeVoid(int nodeId, String procedureName, In input) {
		invokeAtNode(nodeId, procedureName, 0, input, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAllNodes(String procedureName,
			int timeout, In input, Consumer<Collection<NodeResult<Out>>> output);

	default <In extends ISupervisorData> void invokeAllNodesVoid(String procedureName, int timeout, In input,
			Consumer<Set<Integer>> onComplete) {
		invokeAllNodes(procedureName, timeout, input, onComplete != null ? (Consumer<Collection<NodeResult<SupervisorDataVoid>>>) (lst) -> {
			Set<Integer> ret = new HashSet<>();
			for(NodeResult<SupervisorDataVoid> o : lst) {
				if(o.isSuccessful()) {
					ret.add(o.nodeId);
				}
			}
			onComplete.accept(ret);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAllNodesVoid(String procedureName, In input) {
		invokeAllNodes(procedureName, 0, input, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAllOtherNodes(String procedureName,
			int timeout, In input, Consumer<Collection<NodeResult<Out>>> output);

	default <In extends ISupervisorData> void invokeAllOtherNodesVoid(String procedureName, int timeout, In input,
			Consumer<Set<Integer>> onComplete) {
		invokeAllOtherNodes(procedureName, timeout, input, onComplete != null ? (Consumer<Collection<NodeResult<SupervisorDataVoid>>>) (lst) -> {
			Set<Integer> ret = new HashSet<>();
			for(NodeResult<SupervisorDataVoid> o : lst) {
				if(o.isSuccessful()) {
					ret.add(o.nodeId);
				}
			}
			onComplete.accept(ret);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAllOtherNodesVoid(String procedureName, In input) {
		invokeAllOtherNodes(procedureName, 0, input, null);
	}

}
