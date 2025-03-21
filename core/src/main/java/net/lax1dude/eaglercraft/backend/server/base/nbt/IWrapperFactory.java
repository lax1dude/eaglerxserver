package net.lax1dude.eaglercraft.backend.server.base.nbt;

import java.io.DataInput;

interface IWrapperFactory {

	ValueString wrapStringData(DataInput dataSource);

	ValueByteArray wrapByteData(DataInput dataSource);

	ValueIntArray wrapIntData(DataInput dataSource);

	ValueLongArray wrapLongData(DataInput dataSource);

}
