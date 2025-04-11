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
