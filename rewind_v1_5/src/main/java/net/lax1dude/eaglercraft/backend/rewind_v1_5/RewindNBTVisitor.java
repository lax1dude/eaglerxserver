package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.INBTVisitor;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;

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
		} else if(tagType == EnumTag.LIST) {
			String name = tagName.value();
			if("SpawnPotentials".equals(name)) {
				return INBTVisitor.NOP;
			} else if("pages".equals(name)) {
				return new PagesTransformer();
			} else if("ench".equals(name)) {
				return new EnchantmentTransformer();
			}
		}
		parent().visitTag(tagType, tagName);
		return this;
	}

	private class PagesTransformer implements INBTVisitor {
		@Override
		public INBTVisitor parent() {
			return INBTVisitor.NOP;
		}

		@Override
		public INBTVisitor visitRootTag(EnumTag tagType) {
			return INBTVisitor.NOP;
		}

		@Override
		public INBTVisitor visitTagList(EnumTag itemType, int length) {
			return INBTVisitor.NOP;
		}

		@Override
		public INBTVisitor visitTag(EnumTag tagType, INBTValue<String> tagName) {
			return INBTVisitor.NOP;
		}

		@Override
		public void visitTagString(INBTValue<String> str) throws IOException {
			// todo: .instance() BOOOOOOO
			String transformedText = EaglerXServerAPI.instance().getComponentHelper().convertJSONToLegacySection(str.value());
			parent().visitTagString(context.wrapValue(transformedText));
		}
	}

	private class EnchantmentTransformer implements INBTVisitor {
		@Override
		public INBTVisitor parent() {
			return INBTVisitor.NOP;
		}

		@Override
		public INBTVisitor visitRootTag(EnumTag tagType) {
			return INBTVisitor.NOP;
		}

		@Override
		public INBTVisitor visitTagList(EnumTag itemType, int length) {
			if(itemType == EnumTag.COMPOUND) {
				return this;
			}
			return INBTVisitor.NOP;
		}

		@Override
		public INBTVisitor visitTag(EnumTag tagType, INBTValue<String> tagName) throws IOException {
			if(tagType == EnumTag.INT && "id".equals(tagName.value())) {
				return this;
			}
			return INBTVisitor.NOP;
		}

		@Override
		public void visitTagInt(int value) throws IOException {
			int guh = value & 0xFFFF;
			if(guh == 8 || guh == 62 || guh == 61) {
				value = 0;
			}
			parent.visitTag(EnumTag.INT, context.wrapValue("id")).visitTagInt(value);
		}
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
		public INBTVisitor visitRootTag(EnumTag tagType) {
			return INBTVisitor.NOP;
		}

		@Override
		public INBTVisitor visitTagList(EnumTag itemType, int length) {
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
			parent.visitTag(EnumTag.STRING, context.wrapValue(notSkull ? "ExtraType" : "SkullOwner")).visitTagString(str);
		}

	}
}
