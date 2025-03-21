package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import net.lax1dude.eaglercraft.backend.server.api.nbt.EnumDataType;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTVisitor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class RewindNBTVisitorReverse implements INBTVisitor {

	public static void apply(INBTContext context, DataInput input, DataOutput output) throws IOException {
		context.accept(input, new RewindNBTVisitorReverse(context, context.createWriter(output)));
	}

	private final INBTContext context;
	private final INBTVisitor parent;

	private RewindNBTVisitorReverse(INBTContext context, INBTVisitor parent) {
		this.context = context;
		this.parent = parent;
	}

	@Override
	public INBTVisitor parent() {
		return parent;
	}

	@Override
	public INBTVisitor visitRootTag(EnumDataType tagType) throws IOException {
		parent().visitRootTag(tagType);
		return this;
	}

	@Override
	public INBTVisitor visitTagList(EnumDataType itemType, int length) throws IOException {
		parent().visitTagList(itemType, length);
		return this;
	}

	@Override
	public INBTVisitor visitTag(EnumDataType tagType, INBTValue<String> tagName) throws IOException {
		if(tagType == EnumDataType.COMPOUND && "ExtraType".equals(tagName.value())) {
			tagName.mutate("Owner");
		} else if(tagType == EnumDataType.LIST && "pages".equals(tagName.value())) {
			parent().visitTag(tagType, tagName);
			return new PagesTransformer();
		}
		parent().visitTag(tagType, tagName);
		return this;
	}

	private class PagesTransformer implements INBTVisitor {
		@Override
		public INBTVisitor parent() {
			return RewindNBTVisitorReverse.this;
		}

		private int w = Integer.MAX_VALUE;
		private int len = Integer.MAX_VALUE;

		@Override
		public INBTVisitor visitRootTag(EnumDataType tagType) throws IOException {
			if (w >= len) {
				return parent().visitRootTag(tagType);
			}
			parent().visitRootTag(tagType);
			return this;
		}

		@Override
		public INBTVisitor visitTagList(EnumDataType itemType, int length) throws IOException {
			if (length != 0 && w == Integer.MAX_VALUE && len == Integer.MAX_VALUE) {
				len = length;
				parent().visitTagList(itemType, length);
				return this;
			} else {
				return parent().visitTagList(itemType, length);
			}
		}

		@Override
		public INBTVisitor visitTag(EnumDataType tagType, INBTValue<String> tagName) throws IOException {
			if (w >= len) {
				return parent().visitTag(tagType, tagName);
			}
			if(tagType == EnumDataType.STRING) {
				w++;
			}
			parent().visitTag(tagType, tagName);
			return this;
		}

		@Override
		public void visitTagString(INBTValue<String> str) throws IOException {
			if (w >= len) {
				parent().visitTagString(str);
				return;
			}
			String transformedText = BufferUtils.stringToChat(str.value());
			parent().visitTagString(context.wrapValue(transformedText));
		}
	}
}
