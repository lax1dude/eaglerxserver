package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTTransformer;
import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor;

public class NBTTransformer implements INBTTransformer {

	@Override
	public void accept(DataInput dataInput, INBTVisitor visitor) throws IOException {
		NBTVisitorReader.read(dataInput, visitor);
	}

	@Override
	public INBTVisitor createWriter(DataOutput dataOutput) {
		return new NBTVisitorWriter(dataOutput);
	}

}
