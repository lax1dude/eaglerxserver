package net.lax1dude.eaglercraft.backend.rpc.base.remote;

public class CapabilityBits {

	public static boolean hasCapability(int mask, byte[] vers, int id, int ver) {
		int bit = 1 << id;
		if((mask & bit) != 0) {
			int versIndex = Integer.bitCount(mask & (bit - 1));
			if(versIndex < vers.length) {
				return (vers[versIndex] & 0xFF) >= ver;
			}
		}
		return false;
	}

	public static int getCapability(int mask, byte[] vers, int id) {
		int bit = 1 << id;
		if((mask & bit) != 0) {
			int versIndex = Integer.bitCount(mask & (bit - 1));
			if(versIndex < vers.length) {
				return vers[versIndex] & 0xFF;
			}
		}
		return -1;
	}

}
