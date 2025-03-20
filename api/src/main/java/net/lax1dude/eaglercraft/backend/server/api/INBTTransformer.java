package net.lax1dude.eaglercraft.backend.server.api;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface INBTTransformer {

	void accept(DataInput dataInput, INBTVisitor visitor) throws IOException;

	INBTVisitor createWriter(DataOutput dataOutput);

}
