package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.api.nbt.EnumDataType;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTVisitor;

public class NBTVisitorReader {

	public static final int MAX_RECURSION = 128;

	public static void read(DataInput dataInput, INBTVisitor visitor, IWrapperFactory wrapper) throws IOException {
		int type = dataInput.readUnsignedByte();
		if(type != 10) {
			throw new IOException("Root tag is not a compound tag!");
		}
		dataInput.skipBytes(dataInput.readUnsignedShort());
		readCompound(dataInput, 0, visitor.visitRootTag(EnumDataType.COMPOUND), wrapper);
	}

	private static void readCompound(DataInput dataInput, int lvl, INBTVisitor visitor, IWrapperFactory wrapper) throws IOException {
		for(;;) {
			int type = dataInput.readUnsignedByte();
			if(type == 0) {
				break;
			}
			EnumDataType typeEnum = parseTag(type);
			ValueString keyName = wrapper.wrapStringData(dataInput);
			INBTVisitor visitor2 = visitor.visitTag(typeEnum, keyName);
			keyName.finish();
			readValue(dataInput, lvl, type, visitor2, wrapper);
		}
		visitor.visitTagEnd();
	}

	private static void readValue(DataInput dataInput, int lvl, int type, INBTVisitor visitor, IWrapperFactory wrapper) throws IOException {
		if(lvl > MAX_RECURSION) {
			throw new IOException("Reached tag recursion limit");
		}
		switch(type) {
		case 1:
			visitor.visitTagByte(dataInput.readByte());
			break;
		case 2:
			visitor.visitTagShort(dataInput.readShort());
			break;
		case 3:
			visitor.visitTagInt(dataInput.readInt());
			break;
		case 4:
			visitor.visitTagLong(dataInput.readLong());
			break;
		case 5:
			visitor.visitTagFloat(dataInput.readFloat());
			break;
		case 6:
			visitor.visitTagDouble(dataInput.readDouble());
			break;
		case 7: {
			ValueByteArray val = wrapper.wrapByteData(dataInput);
			visitor.visitTagByteArray(val);
			val.finish();
			break;
		}
		case 8: {
			ValueString val = wrapper.wrapStringData(dataInput);
			visitor.visitTagString(val);
			val.finish();
			break;
		}
		case 9: {
			int listTypeId = dataInput.readUnsignedByte();
			int len = dataInput.readInt();
			if(listTypeId == 0) {
				if(len != 0) {
					throw new IOException("Invalid list length for empty list: " + len);
				}
				break;
			}
			EnumDataType listType = parseTag(listTypeId);
			if(len < 0) {
				throw new IOException("Invalid list length: " + len);
			}
			INBTVisitor visitor2 = visitor.visitTagList(listType, len);
			for(int i = 0; i < len; ++i) {
				readValue(dataInput, lvl + 1, listTypeId, visitor2, wrapper);
			}
			break;
		}
		case 10:
			readCompound(dataInput, lvl + 1, visitor, wrapper);
			break;
		case 11: {
			ValueIntArray val = wrapper.wrapIntData(dataInput);
			visitor.visitTagIntArray(val);
			val.finish();
			break;
		}
		case 12: {
			ValueLongArray val = wrapper.wrapLongData(dataInput);
			visitor.visitTagLongArray(val);
			val.finish();
			break;
		}
		default:
			throw new IOException("Unknown tag type: " + type);
		}
	}

	private static EnumDataType parseTag(int type) throws IOException {
		EnumDataType ret = EnumDataType.getById(type);
		if(ret == null) {
			throw new IOException("Unknown tag type: " + type);
		}
		return ret;
	}

}
