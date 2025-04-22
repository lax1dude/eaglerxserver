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

package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorProc;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ProcedureDesc;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;

class SupervisorProcedure {

	final String name;
	final SupervisorDataType inputType;
	final SupervisorDataType outputType;
	final ISupervisorProc<?, ?> proc;

	<In extends ISupervisorData, Out extends ISupervisorData> SupervisorProcedure(ProcedureDesc<In, Out> procDesc,
			ISupervisorProc<? super In, ? extends Out> procImpl) {
		this.name = procDesc.getName();
		this.inputType = SupervisorDataType.provideType(procDesc.getInputType());
		this.outputType = SupervisorDataType.provideType(procDesc.getOutputType());
		this.proc = procImpl;
	}

}
