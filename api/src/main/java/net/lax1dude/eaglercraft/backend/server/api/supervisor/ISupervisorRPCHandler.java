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

package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.collect.IntSet;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.SupervisorDataVoid;

public interface ISupervisorRPCHandler {

	<In extends ISupervisorData, Out extends ISupervisorData> void registerProcedure(
			@Nonnull ProcedureDesc<In, Out> desc, @Nonnull ISupervisorProc<? super In, ? extends Out> proc);

	<In extends ISupervisorData, Out extends ISupervisorData> void unregisterProcedure(
			@Nonnull ProcedureDesc<In, Out> desc);

	default <In extends ISupervisorData, Out extends ISupervisorData> void registerProcedureSync(
			@Nonnull ProcedureDesc<In, Out> desc, @Nonnull ISupervisorProcSync<? super In, ? extends Out> proc) {
		registerProcedure(desc, proc);
	}

	default <In extends ISupervisorData> void registerProcedureVoid(@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc,
			@Nonnull ISupervisorProcVoid<? super In> proc) {
		registerProcedure(desc, proc);
	}

	default <In extends ISupervisorData> void registerProcedureSyncVoid(
			@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc, @Nonnull ISupervisorProcSyncVoid<? super In> proc) {
		registerProcedure(desc, proc);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAtPlayer(@Nonnull String username,
			@Nonnull ProcedureDesc<In, Out> desc, int timeout, @Nonnull In input,
			@Nullable Consumer<? super Out> output);

	default <Out extends ISupervisorData> void invokeAtPlayer(@Nonnull String username,
			@Nonnull ProcedureDesc<SupervisorDataVoid, Out> desc, int timeout, @Nullable Consumer<? super Out> output) {
		invokeAtPlayer(username, desc, timeout, ISupervisorData.VOID, output);
	}

	default <In extends ISupervisorData> void invokeAtPlayerVoid(@Nonnull String username,
			@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc, int timeout, @Nonnull In input,
			@Nullable IVoidCallback onComplete) {
		invokeAtPlayer(username, desc, timeout, input, onComplete != null ? (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default void invokeAtPlayerVoid(@Nonnull String username,
			@Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc, int timeout,
			@Nullable IVoidCallback onComplete) {
		invokeAtPlayer(username, desc, timeout, ISupervisorData.VOID, onComplete != null ? (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAtPlayerVoid(@Nonnull String username,
			@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc, @Nonnull In input) {
		invokeAtPlayerVoid(username, desc, 0, input, null);
	}

	default void invokeAtPlayerVoid(@Nonnull String username,
			@Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAtPlayerVoid(username, desc, 0, ISupervisorData.VOID, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAtPlayer(@Nonnull UUID player,
			@Nonnull ProcedureDesc<In, Out> desc, int timeout, @Nonnull In input,
			@Nullable Consumer<? super Out> output);

	default <Out extends ISupervisorData> void invokeAtPlayer(@Nonnull UUID player,
			@Nonnull ProcedureDesc<SupervisorDataVoid, Out> desc, int timeout, @Nullable Consumer<? super Out> output) {
		invokeAtPlayer(player, desc, timeout, ISupervisorData.VOID, output);
	}

	default <In extends ISupervisorData> void invokeAtPlayerVoid(@Nonnull UUID player,
			@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc, int timeout, @Nonnull In input,
			@Nullable IVoidCallback onComplete) {
		invokeAtPlayer(player, desc, timeout, input, onComplete != null ? (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default void invokeAtPlayerVoid(@Nonnull UUID player,
			@Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc, int timeout,
			@Nullable IVoidCallback onComplete) {
		invokeAtPlayer(player, desc, timeout, ISupervisorData.VOID, onComplete != null ? (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAtPlayerVoid(@Nonnull UUID player,
			@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc, @Nonnull In input) {
		invokeAtPlayerVoid(player, desc, 0, input, null);
	}

	default void invokeAtPlayerVoid(@Nonnull UUID player,
			@Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAtPlayerVoid(player, desc, 0, ISupervisorData.VOID, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAtNode(int nodeId,
			@Nonnull ProcedureDesc<In, Out> desc, int timeout, @Nonnull In input,
			@Nullable Consumer<? super Out> output);

	default <Out extends ISupervisorData> void invokeAtNode(int nodeId,
			@Nonnull ProcedureDesc<SupervisorDataVoid, Out> desc, int timeout, @Nullable Consumer<? super Out> output) {
		invokeAtNode(nodeId, desc, timeout, ISupervisorData.VOID, output);
	}

	default <In extends ISupervisorData> void invokeAtNodeVoid(int nodeId,
			@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc, int timeout, @Nonnull In input,
			@Nullable IVoidCallback onComplete) {
		invokeAtNode(nodeId, desc, timeout, input, onComplete != null ? (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default void invokeAtNodeVoid(int nodeId, @Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc,
			int timeout, @Nullable IVoidCallback onComplete) {
		invokeAtNode(nodeId, desc, timeout, ISupervisorData.VOID, onComplete != null ? (res) -> {
			onComplete.call(res != null);
		} : null);
	}

	default <In extends ISupervisorData> void invokeAtNodeVoid(int nodeId,
			@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc, @Nonnull In input) {
		invokeAtNode(nodeId, desc, 0, input, null);
	}

	default void invokeAtNodeVoid(int nodeId, @Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAtNode(nodeId, desc, 0, ISupervisorData.VOID, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAllNodes(@Nonnull ProcedureDesc<In, Out> desc,
			int timeout, @Nonnull In input, @Nullable Consumer<? super Collection<NodeResult<Out>>> output);

	default <Out extends ISupervisorData> void invokeAllNodes(@Nonnull ProcedureDesc<SupervisorDataVoid, Out> desc,
			int timeout, @Nullable Consumer<? super Collection<NodeResult<Out>>> output) {
		invokeAllNodes(desc, timeout, ISupervisorData.VOID, output);
	}

	default <In extends ISupervisorData> void invokeAllNodesVoid(@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc,
			int timeout, @Nonnull In input, @Nullable Consumer<IntSet> onComplete) {
		invokeAllNodes(desc, timeout, input, onComplete != null ? (lst) -> {
			onComplete.accept(toIntSet(lst));
		} : null);
	}

	default void invokeAllNodesVoid(@Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc, int timeout,
			@Nullable Consumer<IntSet> onComplete) {
		invokeAllNodes(desc, timeout, ISupervisorData.VOID, onComplete != null ? (lst) -> {
			onComplete.accept(toIntSet(lst));
		} : null);
	}

	default <In extends ISupervisorData> void invokeAllNodesVoid(@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc,
			@Nonnull In input) {
		invokeAllNodes(desc, 0, input, null);
	}

	default void invokeAllNodesVoid(@Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAllNodes(desc, 0, ISupervisorData.VOID, null);
	}

	<In extends ISupervisorData, Out extends ISupervisorData> void invokeAllOtherNodes(
			@Nonnull ProcedureDesc<In, Out> desc, int timeout, @Nonnull In input,
			@Nullable Consumer<? super Collection<NodeResult<Out>>> output);

	default <Out extends ISupervisorData> void invokeAllOtherNodes(@Nonnull ProcedureDesc<SupervisorDataVoid, Out> desc,
			int timeout, @Nullable Consumer<? super Collection<NodeResult<Out>>> output) {
		invokeAllOtherNodes(desc, timeout, ISupervisorData.VOID, output);
	}

	default <In extends ISupervisorData> void invokeAllOtherNodesVoid(
			@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc, int timeout, @Nonnull In input,
			@Nullable Consumer<IntSet> onComplete) {
		invokeAllOtherNodes(desc, timeout, input, onComplete != null ? (lst) -> {
			onComplete.accept(toIntSet(lst));
		} : null);
	}

	default void invokeAllOtherNodesVoid(@Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc,
			int timeout, @Nullable Consumer<IntSet> onComplete) {
		invokeAllOtherNodes(desc, timeout, ISupervisorData.VOID, onComplete != null ? (lst) -> {
			onComplete.accept(toIntSet(lst));
		} : null);
	}

	default <In extends ISupervisorData> void invokeAllOtherNodesVoid(
			@Nonnull ProcedureDesc<In, SupervisorDataVoid> desc, @Nonnull In input) {
		invokeAllOtherNodes(desc, 0, input, null);
	}

	default <In extends ISupervisorData> void invokeAllOtherNodesVoid(
			@Nonnull ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> desc) {
		invokeAllOtherNodes(desc, 0, ISupervisorData.VOID, null);
	}

	@Nullable
	IntSet toIntSet(@Nullable Collection<NodeResult<SupervisorDataVoid>> collection);

}
