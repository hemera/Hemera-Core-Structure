package hemera.core.structure.runtime.util;

/**
 * <code>ModuleNode</code> defines the immutable data
 * structure of a module with its defining fields.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
abstract class ResourceNode {
	/**
	 * The <code>String</code> fully qualified class
	 * name of the module implementation.
	 */
	final String classname;
	/**
	 * The <code>String</code> optional module
	 * configuration file location.
	 */
	final String configLocation;
	/**
	 * The <code>String</code> optional module
	 * resources directory.
	 */
	final String resourcesDir;
	/**
	 * The <code>String</code> optional shared
	 * resources directory.
	 */
	final String sharedResourcesDir;

	/**
	 * Constructor of <code>ModuleNode</code>.
	 * @param classname The <code>String</code>
	 * fully qualified class name of the module
	 * implementation.
	 * @param configLocation The <code>String</code>
	 * module configuration file location. This is
	 * an optional value and can be <code>null</code>.
	 * @param resourcesDir The <code>String</code>
	 * optional module resources directory.
	 * @param sharedResourcesDir The optional shared
	 * resources directory <code>String</code>.
	 */
	ResourceNode(final String classname, final String configLocation, final String resourcesDir,
			final String sharedResourcesDir) {
		this.classname = classname;
		this.configLocation = configLocation;
		this.resourcesDir = resourcesDir;
		this.sharedResourcesDir = sharedResourcesDir;
	}
}
