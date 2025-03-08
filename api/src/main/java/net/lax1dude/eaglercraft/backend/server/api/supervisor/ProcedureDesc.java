package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.SupervisorDataVoid;

public final class ProcedureDesc<In extends ISupervisorData, Out extends ISupervisorData> {

	private final String name;
	private final Class<In> inputType;
	private final Class<Out> outputType;

	public static <In extends ISupervisorData, Out extends ISupervisorData> ProcedureDesc<In, Out> create(String name, Class<In> inputType, Class<Out> outputType) {
		return new ProcedureDesc<>(name.intern(), inputType, outputType);
	}

	public static <In extends ISupervisorData> ProcedureDesc<In, SupervisorDataVoid> create(String name, Class<In> inputType) {
		return create(name, inputType, SupervisorDataVoid.class);
	}

	public static ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> create(String name) {
		return create(name, SupervisorDataVoid.class, SupervisorDataVoid.class);
	}

	private ProcedureDesc(String name, Class<In> inputType, Class<Out> outputType) {
		this.name = name;
		this.inputType = inputType;
		this.outputType = outputType;
	}

	public Class<In> getInputType() {
		return inputType;
	}

	public Class<Out> getOutputType() {
		return outputType;
	}

	public int hashCode() {
		return (name.hashCode() * 31 + inputType.hashCode()) * 31 + outputType.hashCode();
	}

	public boolean equals(Object o) {
		ProcedureDesc<?, ?> v;
		return this == o || ((o instanceof ProcedureDesc<?, ?>) && (v = (ProcedureDesc<?, ?>) o).name.equals(name)
				&& v.inputType == inputType && v.outputType == outputType);
	}

}
