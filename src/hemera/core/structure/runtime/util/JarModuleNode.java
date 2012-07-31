package hemera.core.structure.runtime.util;

/**
 * <code>JarModuleNode</code> defines the immutable
 * extension of a module node whose class files are
 * in a Jar file.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public class JarModuleNode extends ModuleNode {
	/**
	 * The <code>String</code> module JAR file location.
	 */
	public final String jarLocation;

	/**
	 * Constructor of <code>JarModuleNode</code>.
	 * @param jarLocation The <code>String</code>
	 * module JAR file location.
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
	public JarModuleNode(final String jarLocation, final String classname, final String configLocation,
			final String resourcesDir, final String sharedResourcesDir) {
		super(classname, configLocation, resourcesDir, sharedResourcesDir);
		this.jarLocation = jarLocation;
	}
}
