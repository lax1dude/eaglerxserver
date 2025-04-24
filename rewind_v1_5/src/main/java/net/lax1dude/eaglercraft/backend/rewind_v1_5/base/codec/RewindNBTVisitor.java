/*
 * Copyright (c) 2025 ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.rewind_v1_5.base.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.IComponentHelper;
import net.lax1dude.eaglercraft.backend.server.api.nbt.EnumDataType;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTVisitor;

public class RewindNBTVisitor implements INBTVisitor {

	public static void apply(INBTContext context, DataInput input, DataOutput output, IComponentHelper componentHelper) throws IOException {
		context.accept(input, new RewindNBTVisitor(context, context.createWriter(output), componentHelper));
	}

	private final INBTContext context;
	private final INBTVisitor parent;
	private final IComponentHelper componentHelper;

	private RewindNBTVisitor(INBTContext context, INBTVisitor parent, IComponentHelper componentHelper) {
		this.context = context;
		this.parent = parent;
		this.componentHelper = componentHelper;
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
		if(tagType == EnumDataType.COMPOUND) {
			String name = tagName.value();
			switch(name) {
			case "tag":
				parent().visitTag(tagType, tagName);
				return this;
			case "SpawnData":
				return INBTVisitor.NOP;
			case "SkullOwner":
				return new SkullOwnerTransformer(false);
			case "Owner":
				return new SkullOwnerTransformer(true);
			}
		} else if(tagType == EnumDataType.LIST) {
			String name = tagName.value();
			switch(name) {
			case "SpawnPotentials":
				return INBTVisitor.NOP;
			case "pages":
				parent().visitTag(tagType, tagName);
				return new PagesTransformer();
			case "ench":
				parent().visitTag(tagType, tagName);
				return new EnchantmentTransformer();
			}
		}
		parent().visitTag(tagType, tagName);
		return this;
	}

	private class PagesTransformer implements INBTVisitor {
		@Override
		public INBTVisitor parent() {
			return RewindNBTVisitor.this;
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
			String transformedText = str.value();
			try {
				transformedText = componentHelper.convertJSONToLegacySection(transformedText);
			} catch (IllegalArgumentException e) {
				//
			}
			parent().visitTagString(context.wrapValue(transformedText));
		}
	}

	private class EnchantmentTransformer implements INBTVisitor {
		@Override
		public INBTVisitor parent() {
			return RewindNBTVisitor.this;
		}

		private int w = Integer.MAX_VALUE;
		private int len = Integer.MAX_VALUE;
		private boolean isId = false;

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
			if(tagType == EnumDataType.COMPOUND) {
				w++;
			} else if(tagType == EnumDataType.SHORT || tagType == EnumDataType.INT) {
				isId = "id".equals(tagName.value());
			}
			parent().visitTag(tagType, tagName);
			return this;
		}

		@Override
		public void visitTagInt(int value) throws IOException {
			if (isId) {
				int guh = value & 0xFFFF;
				if (guh == 8 || guh == 62 || guh == 61) {
					value = 0;
				}
			}
			parent().visitTagInt(value);
		}

		@Override
		public void visitTagShort(short value) throws IOException {
			if (isId) {
				int guh = value & 0xFFFF;
				if (guh == 8 || guh == 62 || guh == 61) {
					value = 0;
				}
			}
			parent().visitTagShort(value);
		}
	}

	private class SkullOwnerTransformer implements INBTVisitor {

		private final boolean notSkull;

		public SkullOwnerTransformer(boolean which) {
			notSkull = which;
		}

		@Override
		public INBTVisitor parent() {
			return RewindNBTVisitor.this;
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
			if(tagType == EnumDataType.STRING && "Name".equals(tagName.value())) {
				return this;
			}
			return parent().visitTag(tagType, tagName);
		}

		@Override
		public void visitTagString(INBTValue<String> str) throws IOException {
			parent().visitTag(EnumDataType.STRING, context.wrapValue(notSkull ? "ExtraType" : "SkullOwner")).visitTagString(str);
		}

	}
}
