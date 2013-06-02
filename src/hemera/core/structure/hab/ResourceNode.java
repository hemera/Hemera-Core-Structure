package hemera.core.structure.hab;

/**
 * <code>ResourceNode</code> defines the immutable data
 * structure of a resource with its defining fields.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.4
 */
public class ResourceNode {
	/**
	 * The <code>String</code> resource JAR file
	 * location.
	 */
	public final String jarLocation;
	/**
	 * The <code>String</code> fully qualified class
	 * name of the module implementation.
	 */
	public final String classname;
	/**
	 * The <code>String</code> optional module
	 * configuration file location.
	 */
	public final String configLocation;
	/**
	 * The <code>String</code> optional module
	 * resources directory.
	 */
	public final String resourcesDir;
	/**
	 * The <code>String</code> optional shared
	 * resources directory.
	 */
	public final String sharedResourcesDir;

	/**
	 * Constructor of <code>ResourceNode</code>.
	 * @param jarLocation The <code>String</code>
	 * resource JAR file location.
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
	protected ResourceNode(final String jarLocation, final String classname, final String configLocation, final String resourcesDir,
			final String sharedResourcesDir) {
		this.jarLocation = jarLocation;
		this.classname = classname;
		this.configLocation = configLocation;
		this.resourcesDir = resourcesDir;
		this.sharedResourcesDir = sharedResourcesDir;
	}
}
