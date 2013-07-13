package hemera.core.structure.interfaces;

import hemera.core.structure.enumn.EHttpMethod;
import hemera.core.utility.uri.RESTURI;

/**
 * <code>IResourceRegistry</code> defines the interface
 * of a registry service that provides access to a set
 * of resources based on defined REST path.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.5
 */
public interface IResourceRegistry {
	
	/**
	 * Retrieve the resource defined for the given REST
	 * URI.
	 * <p>
	 * This method attempts to <code>poll</code> one
	 * element off of the <code>RESTURI</code> at a time
	 * until a resource is found.
	 * @param path The <code>RESTURI</code> to check.
	 * @param method The <code>EHttpMethod</code> to
	 * check.
	 * @return The <code>IResource</code> instance. Or
	 * <code>null</code> if there is no such resource.
	 */
	public IResource getResource(final RESTURI uri, final EHttpMethod method);
}
