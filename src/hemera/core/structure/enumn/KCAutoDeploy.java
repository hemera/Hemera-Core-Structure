package hemera.core.structure.enumn;

/**
 * <code>KCAutoDeploy</code> defines the configuration
 * keys used in runtime environment auto-deployment file.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public enum KCAutoDeploy {
	/**
	 * The root tag name.
	 */
	RootTag("auto-deployment"),
	/**
	 * The module tag name.
	 */
	ModuleTag("module"),
	/**
	 * The JAR file location tag name. The value is of
	 * type <code>String</code>.
	 */
	JarLocation("jar-location"),
	/**
	 * The class name tag name. The value is of type
	 * <code>String</code>.
	 */
	Classname("classname"),
	/**
	 * The JAR file local tag name. The value is of
	 * type <code>boolean</code>.
	 */
	JarLocal("jar-local"),
	/**
	 * The module configuration file path name. The
	 * value if of type <code>String</code>.
	 */
	ConfigPath("config-path");
	
	/**
	 * The <code>String</code> value.
	 */
	public final String value;
	
	/**
	 * Constructor of <code>KCAutoDeploy</code>.
	 * @param value The <code>String</code> value.
	 */
	private KCAutoDeploy(final String value) {
		this.value = value;
	}
}
