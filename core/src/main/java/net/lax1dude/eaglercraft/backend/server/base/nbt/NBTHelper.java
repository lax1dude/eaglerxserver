package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.INBTHelper;
import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor;

public class NBTHelper implements INBTHelper {

	public static final INBTHelper INSTANCE = new NBTHelper();

	private NBTHelper() {
	}

	@Override
	public void accept(DataInput dataInput, INBTVisitor visitor) throws IOException {
		NBTVisitorReader.read(dataInput, visitor);
	}

	@Override
	public INBTVisitor createWriter(DataOutput dataOutput) {
		return new NBTVisitorWriter(dataOutput);
	}

	@Override
	public INBTContext createThreadContext(int bufferSize) {
		return new NBTContext(bufferSize);
	}

}
