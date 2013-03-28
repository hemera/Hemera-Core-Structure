package hemera.core.structure.runtime.util;

import hemera.core.environment.config.Configuration;
import hemera.core.environment.enumn.EEnvironment;
import hemera.core.environment.ham.HAM;
import hemera.core.environment.ham.HAMResource;
import hemera.core.environment.util.UEnvironment;
import hemera.core.execution.AbstractServiceListener;
import hemera.core.execution.assisted.AssistedService;
import hemera.core.execution.exception.FileExceptionHandler;
import hemera.core.execution.interfaces.IExceptionHandler;
import hemera.core.execution.interfaces.IExecutionService;
import hemera.core.execution.listener.FileServiceListener;
import hemera.core.execution.scalable.ScalableService;
import hemera.core.structure.interfaces.IResource;
import hemera.core.structure.interfaces.runtime.IRuntime;
import hemera.core.structure.interfaces.runtime.util.IRuntimeLauncher;
import hemera.core.utility.FileUtils;
import hemera.core.utility.data.TimeData;
import hemera.core.utility.logging.LoggingConfig;
import hemera.core.utility.logging.FileLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <code>RuntimeLauncher</code> defines the abstraction
 * of an utility unit that provides the functionality to
 * launch a runtime environment instance based on user
 * provided configuration as a UNIX <code>Daemon</code>
 * process.
 * <p>
 * <code>RuntimeLauncher</code> requires one argument
 * to the <code>init</code> method for launching, the
 * file path for the runtime configuration file.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.1
 */
public abstract class RuntimeLauncher implements IRuntimeLauncher {
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
	 * The <code>boolean</code> scan for applications
	 * flag. The default value is <code>true</code>.
	 */
	private boolean scanApps;

	/**
	 * Constructor of <code>RuntimeLauncher</code>.
	 */
	protected RuntimeLauncher() {
		this.scanApps = true;
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
		this.runtime.shutdownAwait(10, TimeUnit.SECONDS);
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
		final Document document = FileUtils.instance.readAsDocument(new File(this.configPath));
		final Configuration config = new Configuration(document);
		// Set logging configuration.
		LoggingConfig.Enabled.setValue(config.runtime.logging.enabled);
		LoggingConfig.Directory.setValue(config.runtime.logging.directory);
		LoggingConfig.FileSize.setValue(config.runtime.logging.fileSize);
		LoggingConfig.FileCount.setValue(config.runtime.logging.fileCount);
		// Create runtime.
		final IExecutionService service = this.newExecutionService(config);
		final IRuntime runtime = this.newRuntime(service, config);
		try {
			// Activate.
			runtime.activate();
			// Scan and deploy resources.
			if (this.scanApps) this.scanAndDeployResources(runtime);
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
	 * Create a new execution service listener instance
	 * based on configuration values.
	 * @param config The <code>Configuration</code> for
	 * the runtime environment.
	 * @return The <code>IServiceListener</code> instance.
	 * @throws IOException If any file processing failed.
	 * @throws ClassNotFoundException If custom service
	 * listener class cannot be found in the JAR file.
	 * @throws IllegalAccessException If custom service
	 * listener implementation default constructor cannot
	 * be accessed.
	 * @throws InstantiationException If custom service
	 * listener implementation cannot be instantiated.
	 */
	@SuppressWarnings("unchecked")
	private AbstractServiceListener newServiceListener(final Configuration config) throws IOException, ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		final String listenerJarLocation = config.runtime.execution.listener.jarLocation;
		final String listenerClassname = config.runtime.execution.listener.classname;
		if (listenerJarLocation != null && listenerClassname != null) {
			final File jarFile = new File(listenerJarLocation);
			final URLClassLoader classloader = new URLClassLoader(new URL[] {jarFile.toURI().toURL()});
			final Class<? extends AbstractServiceListener> c = (Class<? extends AbstractServiceListener>)classloader.loadClass(listenerClassname);
			return c.newInstance();
		} else {
			return new FileServiceListener();
		}
	}

	/**
	 * Create a new execution service based on runtime
	 * configuration.
	 * @param config The <code>Configuration</code> for
	 * the runtime environment.
	 * @return The <code>IExecutionService</code> instance.
	 * @throws IOException If any file processing failed.
	 * @throws ClassNotFoundException If custom class
	 * cannot be found in the JAR file.
	 * @throws IllegalAccessException If custom class
	 * implementations default constructor cannot be
	 * accessed.
	 * @throws InstantiationException If custom class
	 * implementations cannot be instantiated.
	 */
	private IExecutionService newExecutionService(final Configuration config) throws IOException, ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		final IExceptionHandler handler = this.newExceptionHandler(config);
		final AbstractServiceListener listener = this.newServiceListener(config);
		// Create service based on configuration.
		final boolean useScalableService = config.runtime.execution.useScalableService;
		if (!useScalableService) {
			final int executorCount = config.runtime.execution.assisted.executorCount;
			final int executorBufferSize = config.runtime.execution.assisted.maxBufferSize;
			final TimeData idletime = new TimeData(config.runtime.execution.assisted.idleTime);
			return new AssistedService(handler, listener, executorCount, executorBufferSize, idletime.value, idletime.unit);
		} else {
			final int executorMin = config.runtime.execution.scalable.minExecutor;
			final int executorMax = config.runtime.execution.scalable.maxExecutor;
			final TimeData timeout = new TimeData(config.runtime.execution.scalable.timeout);
			return new ScalableService(handler, listener, executorMin, executorMax, timeout.value, timeout.unit);
		}
	}

	/**
	 * Create a new exception handler instance based on
	 * configuration values.
	 * @param config The <code>Configuration</code> for
	 * the runtime environment.
	 * @return The <code>IExceptionHandler</code> instance.
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
	private IExceptionHandler newExceptionHandler(final Configuration config) throws IOException, ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		final String exceptionJarLocation = config.runtime.execution.handler.jarLocation;
		final String exceptionClassname = config.runtime.execution.handler.classname;
		if (exceptionJarLocation != null && exceptionClassname != null) {
			final File jarFile = new File(exceptionJarLocation);
			final URLClassLoader classloader = new URLClassLoader(new URL[] {jarFile.toURI().toURL()});
			final Class<IExceptionHandler> c = (Class<IExceptionHandler>)classloader.loadClass(exceptionClassname);
			return c.newInstance();
		} else {
			return new FileExceptionHandler();
		}
	}

	/**
	 * Create a new runtime based on given arguments.
	 * @param service The <code>IExecutionService</code>
	 * used to dispatch request processing.
	 * @param config The loaded configuration data.
	 * @return The <code>IRuntime</code> instance.
	 */
	protected abstract IRuntime newRuntime(final IExecutionService service, final Configuration config);

	/**
	 * Scan the applications directory and deploy all of
	 * the resources.
	 * @param runtime The <code>IRuntime</code> instance.
	 * @throws Exception If any processing failed.
	 */
	private void scanAndDeployResources(final IRuntime runtime) throws Exception {
		final String appsDir = UEnvironment.instance.getInstalledAppsDir();
		// Scan all HAM files.
		final List<File> hamFiles = FileUtils.instance.getFiles(appsDir, EEnvironment.HAMExtension.value);
		// Parse out resource configurations.
		final List<JarResourceNode> resources = new ArrayList<JarResourceNode>();
		final int hamSize = hamFiles.size();
		for (int i = 0; i < hamSize; i++) {
			final File hamFile = hamFiles.get(i);
			resources.addAll(this.parseResourceNodes(hamFile));
		}
		// Deploy all resources.
		final int size = resources.size();
		for (int i = 0; i < size; i++) {
			final JarResourceNode resource = resources.get(i);
			this.deployResource(resource, runtime);
		}
	}

	/**
	 * Parse the HAM file and retrieve the application
	 * resource nodes.
	 * @param hamFile The HAM <code>File</code>.
	 * @return The <code>List</code> of all the auto-
	 * deploy <code>JarResourceNode</code>.
	 * @throws IOException If file parsing failed.
	 * @throws ParserConfigurationException If XML
	 * parsing failed.
	 * @throws SAXException If XML parsing failed. 
	 */
	private List<JarResourceNode> parseResourceNodes(final File hamFile) throws IOException, SAXException, ParserConfigurationException {
		// Parse HAM file.
		final Document document = FileUtils.instance.readAsDocument(hamFile);
		final HAM ham = new HAM(document);
		final String sharedResourcesDir = (ham.shared==null) ? null : ham.shared.resourcesDir;
		// Parse all resources.
		final String appDir = UEnvironment.instance.getApplicationDir(ham.applicationName);
		final int size = ham.resources.size();
		final List<JarResourceNode> list = new ArrayList<JarResourceNode>(size);
		for (int i = 0; i < size; i++) {
			final HAMResource hamResource = ham.resources.get(i);
			final StringBuilder jarPath = new StringBuilder();
			jarPath.append(appDir).append(hamResource.classname).append(File.separator);
			jarPath.append(hamResource.classname).append(".jar");
			final JarResourceNode resource = new JarResourceNode(hamResource.classname, hamResource.configFile,
					hamResource.resourcesDir, sharedResourcesDir, jarPath.toString());
			list.add(resource);
		}
		return list;
	}

	/**
	 * Deploy the resource defined by the given resource
	 * node.
	 * @param resource The <code>JarResourceNode</code>.
	 * @param runtime The <code>IRuntime</code> instance.
	 * @throws Exception If any processing failed.
	 */
	@SuppressWarnings("unchecked")
	private void deployResource(final JarResourceNode resource, final IRuntime runtime) throws Exception {
		// Load and instantiate resource.
		final URL jarurl = new File(resource.jarLocation).toURI().toURL();
		final URLClassLoader loader = new URLClassLoader(new URL[] {jarurl});
		final Class<? extends IResource> resourceClass = (Class<? extends IResource>)loader.loadClass(resource.classname);
		// Add resource.
		InputStream configStream = null;
		if (resource.configLocation != null) {
			final URL configURL = new File(resource.configLocation).toURI().toURL();
			configStream = configURL.openStream();
		}
		final List<File> resources = (resource.resourcesDir==null&&resource.sharedResourcesDir==null) ? null : new ArrayList<File>();
		if (resource.resourcesDir != null) {
			resources.addAll(FileUtils.instance.getFiles(resource.resourcesDir));
		}
		if (resource.sharedResourcesDir != null) {
			resources.addAll(FileUtils.instance.getFiles(resource.sharedResourcesDir));
		}
		runtime.add(resourceClass, configStream, resources);
	}
	
	@Override
	public void setScanApps(final boolean scan) {
		this.scanApps = scan;
	}
	
	@Override
	public IRuntime getRuntime() {
		return this.runtime;
	}
}
