package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.SupervisorDataVoid;

public final class ProcedureDesc<In extends ISupervisorData, Out extends ISupervisorData> {

	private final String name;
	private final Class<In> inputType;
	private final Class<Out> outputType;

	@Nonnull
	public static <In extends ISupervisorData, Out extends ISupervisorData> ProcedureDesc<In, Out> create(
			@Nonnull String name, @Nonnull Class<In> inputType, @Nonnull Class<Out> outputType) {
		if (name.length() == 0) {
			throw new IllegalArgumentException("Procedure name cannot be empty!");
		}
		if (name.length() > 255) {
			throw new IllegalArgumentException("Procedure name is too long! Max is 255 chars");
		}
		return new ProcedureDesc<>(name.intern(), inputType, outputType);
	}

	@Nonnull
	public static <In extends ISupervisorData> ProcedureDesc<In, SupervisorDataVoid> create(@Nonnull String name,
			@Nonnull Class<In> inputType) {
		if (name.length() == 0) {
			throw new IllegalArgumentException("Procedure name cannot be empty!");
		}
		if (name.length() > 255) {
			throw new IllegalArgumentException("Procedure name is too long! Max is 255 chars");
		}
		return create(name, inputType, SupervisorDataVoid.class);
	}

	@Nonnull
	public static ProcedureDesc<SupervisorDataVoid, SupervisorDataVoid> create(@Nonnull String name) {
		if (name.length() == 0) {
			throw new IllegalArgumentException("Procedure name cannot be empty!");
		}
		if (name.length() > 255) {
			throw new IllegalArgumentException("Procedure name is too long! Max is 255 chars");
		}
		return create(name, SupervisorDataVoid.class, SupervisorDataVoid.class);
	}

	@Nonnull
	private ProcedureDesc(@Nonnull String name, @Nonnull Class<In> inputType, @Nonnull Class<Out> outputType) {
		this.name = name;
		this.inputType = inputType;
		this.outputType = outputType;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public Class<In> getInputType() {
		return inputType;
	}

	@Nonnull
	public Class<Out> getOutputType() {
		return outputType;
	}

	public int hashCode() {
		return (name.hashCode() * 31 + inputType.hashCode()) * 31 + outputType.hashCode();
	}

	public boolean equals(Object o) {
		return this == o || ((o instanceof ProcedureDesc<?, ?> v) && v.name.equals(name) && v.inputType == inputType
				&& v.outputType == outputType);
	}

	@Nonnull
	public String toString() {
		return name;
	}

}
