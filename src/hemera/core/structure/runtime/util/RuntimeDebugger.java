package hemera.core.structure.runtime.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import hemera.core.environment.config.Configuration;
import hemera.core.environment.enumn.EEnvironment;
import hemera.core.environment.hbm.HBM;
import hemera.core.environment.hbm.HBMResource;
import hemera.core.environment.util.UEnvironment;
import hemera.core.structure.interfaces.IResource;
import hemera.core.structure.interfaces.runtime.IRuntime;
import hemera.core.structure.interfaces.runtime.util.IRuntimeLauncher;
import hemera.core.utility.FileUtils;

/**
 * <code>RuntimeDebugger</code> defines the utility unit
 * that allows a Hemera runtime environment to be run in
 * a contained JVM for debugging.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public class RuntimeDebugger {
	/**
	 * The <code>String</code> home directory.
	 */
	private final String homeDir;
	/**
	 * The <code>List</code> of <code>ResourceNode</code>.
	 */
	private final List<ResourceNode> resources;

	/**
	 * Constructor of <code>RuntimeDebugger</code>.
	 * @param homeDir The <code>String</code> path to
	 * the local Hemera home directory.
	 */
	public RuntimeDebugger(final String homeDir) {
		this.homeDir = FileUtils.instance.getValidDir(homeDir);
		this.resources = new ArrayList<ResourceNode>();
	}

	/**
	 * Start the debugger with added resources.
	 * @throws Exception If any launching logic failed.
	 */
	public void start() throws Exception {
		// Explicitly set the home directory so we don't use the current Jar.
		UEnvironment.instance.setInstalledHomeDir(this.homeDir);
		// Initialize and start runtime environment launcher.
		final IRuntimeLauncher launcher = this.initRuntimeLauncher();
		launcher.start();
		// Deploy added resources.
		final IRuntime runtime = launcher.getRuntime();
		this.deployResources(runtime);
	}

	/**
	 * Initialize the runtime environment launcher.
	 * @return The <code>IRuntimeLauncher</code>.
	 * @throws Exception If any process failed.
	 */
	@SuppressWarnings("unchecked")
	private IRuntimeLauncher initRuntimeLauncher() throws Exception {
		// Retrieve all the Jar files in binary directory.
		final String binDir = UEnvironment.instance.getInstalledBinDir();
		final List<File> binFiles = FileUtils.instance.getFiles(binDir, ".jar");
		// Create runtime environment launcher class loader using binary Jar files.
		final int size = binFiles.size();
		final URL[] binURLs = new URL[size];
		for (int i = 0; i < size; i++) {
			binURLs[i] = binFiles.get(i).toURI().toURL();
		}
		final URLClassLoader loader = new URLClassLoader(binURLs);
		// Instantiate runtime environment launcher.
		final Configuration config = UEnvironment.instance.getConfiguration(this.homeDir);
		final String launcherClassname = (config.runtime.launcher!=null) ? config.runtime.launcher : EEnvironment.DefaultLauncher.value;
		final Class<? extends IRuntimeLauncher> launcherClass = (Class<? extends IRuntimeLauncher>)loader.loadClass(launcherClassname);
		final IRuntimeLauncher launcher = launcherClass.newInstance();
		// Make sure launcher doesn't scan to deploy.
		launcher.setScanApps(false);
		// Pass in configuration file path.
		final String configPath = UEnvironment.instance.getConfigurationFile(this.homeDir);
		launcher.init(new LauncherContext(new String[] {configPath}));
		return launcher;
	}
	
	/**
	 * Deploy all the added resources with the given
	 * runtime.
	 * @param runtime The <code>IRuntime</code> to
	 * deploy resources with.
	 * @throws Exception If any processing failed.
	 */
	@SuppressWarnings("unchecked")
	private void deployResources(final IRuntime runtime) throws Exception {
		final int size = this.resources.size();
		for (int i = 0; i < size; i++) {
			final ResourceNode resource = this.resources.get(i);
			// Load and instantiate resource.
			final Class<? extends IResource> resourceclass = (Class<? extends IResource>)this.getClass().getClassLoader().loadClass(resource.classname);
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
			runtime.add(resourceclass, configStream, resources);
		}
	}

	/**
	 * Add all the resources defined in the HBM file at
	 * given path for debugging.
	 * @param hbmPath The <code>String</code> path to
	 * the HBM file.
	 * @throws IOException If reading file failed.
	 * @throws SAXException If parsing file failed.
	 * @throws ParserConfigurationException If
	 * parsing file failed.
	 */
	public void addHBM(final String hbmPath) throws IOException, SAXException, ParserConfigurationException {
		final Document document = FileUtils.instance.readAsDocument(new File(hbmPath));
		final HBM hbm = new HBM(document);
		final String sharedResourcesDir = (hbm.shared==null) ? null : hbm.shared.resourcesDir;
		final int size = hbm.resources.size();
		for (int i = 0; i < size; i++) {
			final HBMResource resource = hbm.resources.get(i);
			final ResourceNode node = new ResourceNode(resource.classname, resource.configFile,
					resource.resourcesDir, sharedResourcesDir);
			this.addResource(node);
		}
	}

	/**
	 * Add the given resource node for debugging.
	 * @param resource The <code>ResourceNode</code> to
	 * add.
	 */
	public void addResource(final ResourceNode resource) {
		this.resources.add(resource);
	}

	/**
	 * <code>LauncherContext</code> defines the immutable
	 * runtime environment launcher context structure.
	 *
	 * @author Yi Wang (Neakor)
	 * @version 1.0.0
	 */
	private class LauncherContext implements DaemonContext {
		/**
		 * The <code>String</code> array of arguments.
		 */
		private final String[] arguments;

		/**
		 * Constructor of <code>LauncherContext</code>.
		 * @param arguments The <code>String</code> array
		 * of arguments.
		 */
		private LauncherContext(final String[] arguments) {
			this.arguments = arguments;
		}

		@Override
		public String[] getArguments() {
			return this.arguments;
		}

		@Override
		public DaemonController getController() {
			return null;
		}
	}
}
