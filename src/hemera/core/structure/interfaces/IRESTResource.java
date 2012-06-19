package hemera.core.structure.interfaces;

/**
 * <code>IRESTResource</code> defines the interface of
 * a server resource that can be accessed via REST HTTP
 * requests at the defined path.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IRESTResource {

	/**
	 * Retrieve the REST HTTP access path for this
	 * resource.
	 * @return The <code>String</code> access path.
	 */
	public String getPath();
}
