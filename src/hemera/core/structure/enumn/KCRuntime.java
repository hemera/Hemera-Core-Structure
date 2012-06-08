package hemera.core.structure.enumn;

/**
 * <code>KCRuntime</code> defines all the keys used
 * for runtime environment configuration.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public enum KCRuntime {
	/**
	 * The key for <code>Integer</code> number of the
	 * foreground assist executor threads to be used
	 * for notifying processors and executing other
	 * foreground tasks.
	 */
	ExecutorCount,
	/**
	 * The key for <code>String</code> logging directory
	 * path.
	 */
	LoggingDirectory,
	/**
	 * The key for the optional <code>String</code>
	 * exception handler JAR file location.
	 */
	ExceptionHandlerJarLocation,
	/**
	 * The key for the optional <code>String</code>
	 * exception handler fully qualified class name.
	 */
	ExceptionHandlerClassname,
	/**
	 * The key for the optional <code>String</code>
	 * auto-deployment module configuration file.
	 */
	AutoDeployFile;
}
