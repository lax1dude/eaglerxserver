package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor;

public class NBTExample implements INBTVisitor {

	public static void example(INBTContext context, DataInput input, DataOutput output) throws IOException {
		context.accept(input, new NBTExample(context, context.createWriter(output)));
	}

	private final INBTContext context;
	private final INBTVisitor parent;

	private NBTExample(INBTContext context, INBTVisitor parent) {
		this.context = context;
		this.parent = parent;
	}

	@Override
	public INBTVisitor parent() {
		return parent;
	}

	@Override
	public INBTVisitor visitTag(EnumTag tagType, INBTValue<String> tagName) throws IOException {
		if(tagType == EnumTag.COMPOUND && "SkullOwner".equals(tagName.value())) {
			return new SkullOwnerTransformer();
		}
		return parent.visitTag(tagType, tagName);
	}

	private class SkullOwnerTransformer implements INBTVisitor {

		@Override
		public INBTVisitor parent() {
			return INBTVisitor.NOP;
		}

		@Override
		public INBTVisitor visitTag(EnumTag tagType, INBTValue<String> tagName) throws IOException {
			if(tagType == EnumTag.STRING && "Name".equals(tagName.value())) {
				return this;
			}
			return INBTVisitor.NOP;
		}

		@Override
		public void visitTagString(INBTValue<String> str) throws IOException {
			parent.visitTag(EnumTag.STRING, context.wrapValue("SkullOwner")).visitTagString(str);
		}

	}
}
