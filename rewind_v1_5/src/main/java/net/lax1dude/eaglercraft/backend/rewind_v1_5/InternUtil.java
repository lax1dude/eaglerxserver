package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.UUID;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public class InternUtil {

	public static final Interner<UUID> uuidInterner = Interners.newWeakInterner();

}
