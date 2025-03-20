package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor;

public class RewindNBTVisitor implements INBTVisitor {

	public static void apply(INBTContext context, DataInput input, DataOutput output) throws IOException {
		context.accept(input, new RewindNBTVisitor(context, context.createWriter(output)));
	}

	private final INBTContext context;
	private final INBTVisitor parent;

	private RewindNBTVisitor(INBTContext context, INBTVisitor parent) {
		this.context = context;
		this.parent = parent;
	}

	@Override
	public INBTVisitor parent() {
		return parent;
	}

	@Override
	public INBTVisitor visitRootTag(EnumTag tagType) throws IOException {
		parent().visitRootTag(tagType);
		return this;
	}

	@Override
	public INBTVisitor visitTagList(EnumTag itemType, int length) throws IOException {
		parent().visitTagList(itemType, length);
		return this;
	}

	@Override
	public INBTVisitor visitTag(EnumTag tagType, INBTValue<String> tagName) throws IOException {
		if(tagType == EnumTag.COMPOUND) {
			String name = tagName.value();
			if("tag".equals(name)) {
				return this;
			}
			if("SpawnData".equals(name)) {
				return INBTVisitor.NOP;
			}
			if("SkullOwner".equals(name)) {
				return new SkullOwnerTransformer(false);
			}
			if("Owner".equals(name)) {
				return new SkullOwnerTransformer(true);
			}
		} else if(tagType == EnumTag.LIST && "SpawnPotentials".equals(tagName.value())) {
			return INBTVisitor.NOP;
		}
		parent.visitTag(tagType, tagName);
		return this;
	}

	private class SkullOwnerTransformer implements INBTVisitor {

		private final boolean notSkull;

		public SkullOwnerTransformer(boolean which) {
			notSkull = which;
		}

		@Override
		public INBTVisitor parent() {
			return INBTVisitor.NOP;
		}

		@Override
		public INBTVisitor visitRootTag(EnumTag tagType) throws IOException {
			parent().visitRootTag(tagType);
			return this;
		}

		@Override
		public INBTVisitor visitTagList(EnumTag itemType, int length) throws IOException {
			parent().visitTagList(itemType, length);
			return this;
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
			parent.visitTag(EnumTag.STRING, context.wrapValue(notSkull ? "ExtraType" : "SkullOwner")).visitTagString(str);
		}

	}
}
