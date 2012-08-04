package hemera.core.structure.runtime.util;

/**
 * <code>JarResourceNode</code> defines the immutable
 * extension of a resource node whose class files are
 * in a Jar file.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public class JarResourceNode extends ResourceNode {
	/**
	 * The <code>String</code> resource JAR file location.
	 */
	public final String jarLocation;

	/**
	 * Constructor of <code>JarResourceNode</code>.
	 * @param jarLocation The <code>String</code>
	 * resource JAR file location.
	 * @param classname The <code>String</code>
	 * fully qualified class name of the resource
	 * implementation.
	 * @param configLocation The <code>String</code>
	 * resource configuration file location. This is
	 * an optional value and can be <code>null</code>.
	 * @param resourcesDir The <code>String</code>
	 * optional resources directory.
	 * @param sharedResourcesDir The optional shared
	 * resources directory <code>String</code>.
	 */
	public JarResourceNode(final String jarLocation, final String classname, final String configLocation,
			final String resourcesDir, final String sharedResourcesDir) {
		super(classname, configLocation, resourcesDir, sharedResourcesDir);
		this.jarLocation = jarLocation;
	}
}
