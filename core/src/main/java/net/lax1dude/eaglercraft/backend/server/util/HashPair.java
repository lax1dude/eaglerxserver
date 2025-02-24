package net.lax1dude.eaglercraft.backend.server.util;

public class HashPair<A, B> {

	public final A valueA;
	public final B valueB;

	public HashPair(A valueA, B valueB) {
		this.valueA = valueA;
		this.valueB = valueB;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((valueA == null) ? 0 : valueA.hashCode());
		result = prime * result + ((valueB == null) ? 0 : valueB.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HashPair other = (HashPair) obj;
		if (valueA == null) {
			if (other.valueA != null)
				return false;
		} else if (!valueA.equals(other.valueA))
			return false;
		if (valueB == null) {
			if (other.valueB != null)
				return false;
		} else if (!valueB.equals(other.valueB))
			return false;
		return true;
	}

}
