package hemera.core.structure.runtime;

import hemera.core.execution.interfaces.exception.IExceptionHandler;
import hemera.core.structure.enumn.KCAutoDeploy;
import hemera.core.structure.enumn.KCRuntime;
import hemera.core.structure.interfaces.IModule;
import hemera.core.structure.interfaces.runtime.IRuntime;
import hemera.core.utility.config.ConfigImporter;
import hemera.core.utility.config.Configuration;
import hemera.core.utility.config.xml.XMLParser;
import hemera.core.utility.config.xml.XMLTag;
import hemera.core.utility.logging.CLogging;
import hemera.core.utility.logging.FileLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

/**
 * <code>RuntimeLauncher</code> defines the abstraction
 * of an utility unit that provides the functionality to
 * launch a runtime environment instance based on user
 * provided configuration as a UNIX <code>Daemon</code>
 * process.
 * <p>
 * <code>RuntimeLauncher</code> requires one argument
 * to the <code>main</code> method for launching, the
 * file path for the configuration file.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public abstract class RuntimeLauncher implements Daemon {
	/**
	 * The <code>String</code> path to runtime environment
	 * configuration file.
	 */
	private String configPath;
	/**
	 * The <code>IRuntime</code> instance.
	 */
	private IRuntime runtime;

	/**
	 * Constructor of <code>RuntimeLauncher</code>.
	 */
	public RuntimeLauncher() {
		super();
	}

	/**
	 * Constructor of <code>RuntimeLauncher</code>.
	 * @param configPath The <code>String</code> path to
	 * server runtime environment configuration file.
	 */
	public RuntimeLauncher(final String configPath) {
		this.configPath = configPath;
	}

	@Override
	public void init(final DaemonContext context) throws DaemonInitException, Exception {
		final String[] args = context.getArguments();
		if (args == null || args.length < 1) throw new IllegalArgumentException("Runtime environment configuration file not specified.");
		this.configPath = args[0];
	}

	@Override
	public void start() throws Exception {
		this.runtime = this.launch();
	}

	@Override
	public void stop() throws Exception {
		this.runtime.shutdownAwait();
	}

	@Override
	public void destroy() {}

	/**
	 * Launch a runtime environment instance.
	 * @return The launched <code>IRuntime</code>.
	 * @throws Exception If any launching procedure
	 * failed.
	 */
	public IRuntime launch() throws Exception {
		// Load configuration.
		if (this.configPath == null) throw new IllegalArgumentException("Configruation file path must be specified.");
		final ConfigImporter importer = new ConfigImporter();
		final Configuration config = importer.load(new File(this.configPath));
		// Set environment values.
		this.setEnvironmentValues(config);
		// Create runtime.
		final IRuntime runtime = this.constructRuntime(config);
		try {
			// Activate.
			runtime.activate();
			// Auto-deploy modules if there is a configuration file.
			final String autodeployPath = config.getStringValue(KCRuntime.AutoDeployFile);
			if (autodeployPath != null) this.deployModules(autodeployPath, runtime);
			// Return runtime.
			return runtime;
		} catch (Exception e) {
			// Log exception.
			FileLogger.getLogger(this.getClass()).exception(e);
			// Shutdown runtime.
			runtime.shutdown();
			throw e;
		}
	}

	/**
	 * Set the initial values of the runtime environment
	 * based on the configuration data.
	 * @param config The loaded configuration data.
	 */
	private void setEnvironmentValues(final Configuration config) {
		// Logging directory.
		final String directory = config.getStringValue(KCRuntime.LoggingDirectory);
		CLogging.Directory.setValue(directory);
	}

	/**
	 * Construct the server runtime environment instance
	 * based on the loaded configuration data.
	 * @param config The loaded configuration data.
	 * @return The <code>IRuntime</code> instance.
	 * @throws IOException If any file processing failed.
	 * @throws ClassNotFoundException If custom exception
	 * handler class cannot be found in the JAR file.
	 * @throws IllegalAccessException If custom exception
	 * handler implementation default constructor cannot
	 * be accessed.
	 * @throws InstantiationException If custom exception
	 * handler implementation cannot be instantiated.
	 */
	@SuppressWarnings("unchecked")
	private IRuntime constructRuntime(final Configuration config) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		// Extract configuration values from configuration importer.
		final Integer executorCount = config.getIntegerValue(KCRuntime.ExecutorCount);
		final String exceptionJarLocation = config.getStringValue(KCRuntime.ExceptionHandlerJarLocation);
		final String exceptionClassname = config.getStringValue(KCRuntime.ExceptionHandlerClassname);
		// Construct runtime.
		if (exceptionJarLocation != null && exceptionClassname != null) {
			final File jarFile = new File(exceptionJarLocation);
			final URLClassLoader classloader = new URLClassLoader(new URL[] {jarFile.toURI().toURL()});
			final Class<IExceptionHandler> c = (Class<IExceptionHandler>)classloader.loadClass(exceptionClassname);
			final IExceptionHandler exceptionHandler = c.newInstance();
			return this.newRuntime(executorCount, exceptionHandler, config);
		} else {
			return this.newRuntime(executorCount, config);
		}
	}
	
	/**
	 * Create a new runtime based on given arguments.
	 * @param count The <code>int</code> number of
	 * foreground assist executor threads to be used
	 * for notifying processors and executing other
	 * foreground tasks.
	 * @param config The loaded configuration data.
	 * @return The <code>IRuntime</code> instance.
	 */
	protected abstract IRuntime newRuntime(final int count, final Configuration config);

	/**
	 * Create a new runtime based on given arguments.
	 * @param count The <code>int</code> number of
	 * foreground assist executor threads to be used
	 * for notifying processors and executing other
	 * foreground tasks.
	 * @param exceptionHandler The instance of the
	 * <code>IExceptionHandler</code> used for execution
	 * service to handle exceptions.
	 * @param config The loaded configuration data.
	 * @return The <code>IRuntime</code> instance.
	 */
	protected abstract IRuntime newRuntime(final int count, final IExceptionHandler exceptionHandler, final Configuration config);
	
	/**
	 * Deploy any modules that are defined in the auto-
	 * deployment configuration file.
	 * @param path The <code>String</code> file path to
	 * the auto-deployment configuration file.
	 * @param runtime The <code>IRuntime</code> instance.
	 * @throws Exception If any processing failed.
	 */
	private void deployModules(final String path, final IRuntime runtime) throws Exception {
		// Parse out module configurations.
		final List<ModuleConfig> modules = this.parseModuleConfigs(path);
		// Deploy all modules.
		final int size = modules.size();
		for (int i = 0; i < size; i++) {
			final ModuleConfig module = modules.get(i);
			this.deployModule(module, runtime);
		}
	}

	/**
	 * Parse the auto-deploy configuration file and
	 * retrieve all the module configurations.
	 * @param path The <code>String</code> file path to
	 * the auto-deployment configuration file.
	 * @return The <code>List</code> of all the auto-
	 * deploy <code>ModuleConfig</code>.
	 * @throws IOException If file parsing failed.
	 */
	private List<ModuleConfig> parseModuleConfigs(final String path) throws IOException {
		final List<ModuleConfig> list = new ArrayList<ModuleConfig>();
		final XMLParser parser = new XMLParser();
		parser.process(new File(path));
		final XMLTag root = parser.getRoot();
		if (root.getName().equals(KCAutoDeploy.RootTag.value)) {
			// Parse all children tags as module tags.
			final Iterable<XMLTag> moduleTags = root.getChildren();
			for (final XMLTag moduleTag : moduleTags) {
				if (moduleTag.getName().equals(KCAutoDeploy.ModuleTag.value)) {
					final String jarlocation = moduleTag.getValue(KCAutoDeploy.JarLocation.value);
					final String classname = moduleTag.getValue(KCAutoDeploy.Classname.value);
					final boolean jarlocal = Boolean.valueOf(moduleTag.getValue(KCAutoDeploy.JarLocal.value));
					final String configPath = moduleTag.getValue(KCAutoDeploy.ConfigPath.value);
					final ModuleConfig module = new ModuleConfig(jarlocation, classname, jarlocal, configPath);
					list.add(module);
				}
			}
		}
		return list;
	}

	/**
	 * Deploy the module defined by the given module
	 * configuration.
	 * @param config The <code>ModuleConfig</code>.
	 * @param runtime The <code>IRuntime</code> instance.
	 * @throws Exception If any processing failed.
	 */
	@SuppressWarnings("unchecked")
	private void deployModule(final ModuleConfig config, final IRuntime runtime) throws Exception {
		// Load and instantiate module.
		final URL jarurl = config.local ? new File(config.jarLocation).toURI().toURL() : new URL(config.jarLocation);
		final URLClassLoader loader = new URLClassLoader(new URL[] {jarurl});
		final Class<? extends IModule> moduleclass = (Class<? extends IModule>)loader.loadClass(config.classname);
		// Assign based on configuration.
		if (config.configLocation != null) {
			final URL configURL = config.local ? new File(config.configLocation).toURI().toURL() : new URL(config.configLocation);
			final InputStream configStream = configURL.openStream();
			runtime.add(moduleclass, configStream);
		} else {
			runtime.add(moduleclass);
		}
	}

	/**
	 * <code>ModuleConfig</code> defines the immutable
	 * data structure representing a configuration of
	 * a module instance.
	 *
	 * @author Yi Wang (Neakor)
	 * @version 1.0.0
	 */
	private class ModuleConfig {
		/**
		 * The <code>String</code> module JAR file location.
		 */
		private final String jarLocation;
		/**
		 * The <code>String</code> fully qualified class
		 * name of the module implementation.
		 */
		private final String classname;
		/**
		 * The <code>boolean</code> value indicates if the
		 * JAR file is on the local disk of the machine where
		 * the server node runs.
		 */
		private final boolean local;
		/**
		 * The <code>String</code> module configuration file
		 * location.
		 */
		private final String configLocation;

		/**
		 * Constructor of <code>ModuleConfig</code>.
		 * @param jarLocation The <code>String</code>
		 * module JAR file location.
		 * @param classname The <code>String</code>
		 * fully qualified class name of the module
		 * implementation.
		 * @param local The <code>boolean</code> value
		 * indicates if the JAR file is on the local
		 * disk of the machine where the server node runs.
		 * @param configLocation The <code>String</code>
		 * module configuration file location.
		 */
		private ModuleConfig(final String jarLocation, final String classname, final boolean local, final String configLocation) {
			this.jarLocation = jarLocation;
			this.classname = classname;
			this.local = local;
			this.configLocation = configLocation;
		}
	}
}
