package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.SupervisorDataVoid;

/**
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
public interface ISupervisorProcSyncVoid<In extends ISupervisorData> extends ISupervisorProc<In, SupervisorDataVoid> {

	default void call(int sourceNode, In dataInput, Consumer<SupervisorDataVoid> dataOutput) {
		call0(sourceNode, dataInput);
		dataOutput.accept(ISupervisorData.VOID);
	}

	void call0(int sourceNode, In dataInput);

}