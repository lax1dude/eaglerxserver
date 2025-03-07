package net.lax1dude.eaglercraft.backend.server.base;

public interface IIdentifiedConnection {

	Object getIdentityToken();

	public abstract class Base implements IIdentifiedConnection {

		@Override
		public int hashCode() {
			return System.identityHashCode(getIdentityToken());
		}

		public boolean equals(Object o) {
			return this == o || ((o instanceof IIdentifiedConnection)
					&& ((IIdentifiedConnection) o).getIdentityToken() == getIdentityToken());
		}

	}

}
