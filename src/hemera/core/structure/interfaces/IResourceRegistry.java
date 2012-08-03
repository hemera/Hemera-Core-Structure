package hemera.core.structure.interfaces;

/**
 * <code>IResourceRegistry</code> defines the interface
 * of a registry service that provides access to a set
 * of resources based on defined REST path.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IResourceRegistry {
	
	/**
	 * Retrieve the resource defined for the given REST
	 * access path.
	 * @param path The <code>String</code> REST access
	 * path to check.
	 * @return The <code>IResource</code> instance. Or
	 * <code>null</code> if there is no such resource.
	 */
	public IResource getResource(final String path);
}
