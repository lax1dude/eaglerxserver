package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.SupervisorDataVoid;

public interface ISupervisorRPCHandler {

	<In extends ISupervisorData, Out extends ISupervisorData> void registerProcedure(ProcedureDesc<In, Out> desc,
			ISupervisorProc<In, Out> proc);

	<In extends ISupervisorData, Out extends ISupervisorData> void unregisterProcedure(ProcedureDesc<In, Out> desc);

	default <In extends ISupervisorData, Out extends ISupervisorData> void registerProcedureSync(
			ProcedureDesc<In, Out> desc, ISupervisorProcSync<In, Out> proc) {
		registerProcedure(desc, proc);
	}

	default <In extends ISupervisorData> void registerProcedureVoid(ProcedureDesc<In, SupervisorDataVoid> desc,
			ISupervisorProcVoid<In> proc) {
		registerProcedure(desc, proc);
	}

	default <In extends ISupervisorData> void registerProcedureSyncVoid(ProcedureDesc<In, SupervisorDataVoid> desc,
			ISupervisorProcSyncVoid<In> proc) {
		registerProcedure(desc, proc);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAtPlayer(String username,
			ProcedureDesc<In, Out> desc, int timeout, In input, Consumer<Out> output);

	default <In extends ISupervisorData> void invokeAtPlayerVoid(String username,
			ProcedureDesc<In, SupervisorDataVoid> desc, int timeout, In input, IVoidCallback onComplete) {
		invokeAtPlayer(username, desc, timeout, input, onComplete != null ? (Consumer<SupervisorDataVoid>) (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default void invokeAtPlayerVoid(String username, ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc,
			int timeout, IVoidCallback onComplete) {
		invokeAtPlayer(username, desc, timeout, ISupervisorData.VOID,
				onComplete != null ? (Consumer<SupervisorDataVoid>) (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAtPlayerVoid(String username,
			ProcedureDesc<In, SupervisorDataVoid> desc, In input) {
		invokeAtPlayerVoid(username, desc, 0, input, null);
	}

	default void invokeAtPlayerVoid(String username, ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAtPlayerVoid(username, desc, 0, ISupervisorData.VOID, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAtPlayer(UUID player,
			ProcedureDesc<In, Out> desc, int timeout, In input, Consumer<Out> output);

	default <In extends ISupervisorData> void invokeAtPlayerVoid(UUID player,
			ProcedureDesc<In, SupervisorDataVoid> desc, int timeout, In input, IVoidCallback onComplete) {
		invokeAtPlayer(player, desc, timeout, input, onComplete != null ? (Consumer<SupervisorDataVoid>) (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default void invokeAtPlayerVoid(UUID player, ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc,
			int timeout, IVoidCallback onComplete) {
		invokeAtPlayer(player, desc, timeout, ISupervisorData.VOID,
				onComplete != null ? (Consumer<SupervisorDataVoid>) (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAtPlayerVoid(UUID player,
			ProcedureDesc<In, SupervisorDataVoid> desc, In input) {
		invokeAtPlayerVoid(player, desc, 0, input, null);
	}

	default void invokeAtPlayerVoid(UUID player, ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAtPlayerVoid(player, desc, 0, ISupervisorData.VOID, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAtNode(int nodeId, ProcedureDesc<In, Out> desc,
			int timeout, In input, Consumer<Out> output);

	default <In extends ISupervisorData> void invokeAtNodeVoid(int nodeId, ProcedureDesc<In, SupervisorDataVoid> desc,
			int timeout, In input, IVoidCallback onComplete) {
		invokeAtNode(nodeId, desc, timeout, input, onComplete != null ? (Consumer<SupervisorDataVoid>) (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default void invokeAtNodeVoid(int nodeId, ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc, int timeout,
			IVoidCallback onComplete) {
		invokeAtNode(nodeId, desc, timeout, ISupervisorData.VOID,
				onComplete != null ? (Consumer<SupervisorDataVoid>) (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAtNodeVoid(int nodeId, ProcedureDesc<In, SupervisorDataVoid> desc,
			In input) {
		invokeAtNode(nodeId, desc, 0, input, null);
	}

	default void invokeAtNodeVoid(int nodeId, ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAtNode(nodeId, desc, 0, ISupervisorData.VOID, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAllNodes(ProcedureDesc<In, Out> desc,
			int timeout, In input, Consumer<Collection<NodeResult<Out>>> output);

	default <In extends ISupervisorData> void invokeAllNodesVoid(ProcedureDesc<In, SupervisorDataVoid> desc,
			int timeout, In input, Consumer<Set<Integer>> onComplete) {
		invokeAllNodes(desc, timeout, input,
				onComplete != null ? (Consumer<Collection<NodeResult<SupervisorDataVoid>>>) (lst) -> {
			Set<Integer> ret = new HashSet<>();
			for (NodeResult<SupervisorDataVoid> o : lst) {
				if (o.isSuccessful()) {
					ret.add(o.getNodeId());
				}
			}
			onComplete.accept(ret);
		} : null);
	}

	default void invokeAllNodesVoid(ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc, int timeout,
			Consumer<Set<Integer>> onComplete) {
		invokeAllNodes(desc, timeout, ISupervisorData.VOID,
				onComplete != null ? (Consumer<Collection<NodeResult<SupervisorDataVoid>>>) (lst) -> {
			Set<Integer> ret = new HashSet<>();
			for (NodeResult<SupervisorDataVoid> o : lst) {
				if (o.isSuccessful()) {
					ret.add(o.getNodeId());
				}
			}
			onComplete.accept(ret);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAllNodesVoid(ProcedureDesc<In, SupervisorDataVoid> desc, In input) {
		invokeAllNodes(desc, 0, input, null);
	}

	default void invokeAllNodesVoid(ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAllNodes(desc, 0, ISupervisorData.VOID, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAllOtherNodes(ProcedureDesc<In, Out> desc,
			int timeout, In input, Consumer<Collection<NodeResult<Out>>> output);

	default <In extends ISupervisorData> void invokeAllOtherNodesVoid(ProcedureDesc<In, SupervisorDataVoid> desc,
			int timeout, In input, Consumer<Set<Integer>> onComplete) {
		invokeAllOtherNodes(desc, timeout, input, onComplete != null ? (Consumer<Collection<NodeResult<SupervisorDataVoid>>>) (lst) -> {
			Set<Integer> ret = new HashSet<>();
			for (NodeResult<SupervisorDataVoid> o : lst) {
				if (o.isSuccessful()) {
					ret.add(o.getNodeId());
				}
			}
			onComplete.accept(ret);
		} : null);
	}

	default void invokeAllOtherNodesVoid(ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc, int timeout,
			Consumer<Set<Integer>> onComplete) {
		invokeAllOtherNodes(desc, timeout, ISupervisorData.VOID,
				onComplete != null ? (Consumer<Collection<NodeResult<SupervisorDataVoid>>>) (lst) -> {
			Set<Integer> ret = new HashSet<>();
			for (NodeResult<SupervisorDataVoid> o : lst) {
				if (o.isSuccessful()) {
					ret.add(o.getNodeId());
				}
			}
			onComplete.accept(ret);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAllOtherNodesVoid(ProcedureDesc<In, SupervisorDataVoid> desc,
			In input) {
		invokeAllOtherNodes(desc, 0, input, null);
	}

	default <In extends ISupervisorData> void invokeAllOtherNodesVoid(ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAllOtherNodes(desc, 0, ISupervisorData.VOID, null);
	}

}
