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
import hemera.core.environment.hbm.HBMModule;
import hemera.core.environment.util.UEnvironment;
import hemera.core.structure.interfaces.IModule;
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
	 * The <code>List</code> of <code>ModuleNode</code>.
	 */
	private final List<ModuleNode> modules;

	/**
	 * Constructor of <code>RuntimeDebugger</code>.
	 * @param homeDir The <code>String</code> path to
	 * the local Hemera home directory.
	 */
	public RuntimeDebugger(final String homeDir) {
		this.homeDir = FileUtils.instance.getValidDir(homeDir);
		this.modules = new ArrayList<ModuleNode>();
	}

	/**
	 * Start the debugger with added modules.
	 * @throws Exception If any launching logic failed.
	 */
	public void start() throws Exception {
		// Explicitly set the home directory so we don't use the current Jar.
		UEnvironment.instance.setInstalledHomeDir(this.homeDir);
		// Initialize and start runtime environment launcher.
		final IRuntimeLauncher launcher = this.initRuntimeLauncher();
		launcher.start();
		// Deploy added modules.
		final IRuntime runtime = launcher.getRuntime();
		this.deployModules(runtime);
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
	 * Deploy all the added modules with the given
	 * runtime.
	 * @param runtime The <code>IRuntime</code> to
	 * deploy modules with.
	 * @throws Exception If any processing failed.
	 */
	@SuppressWarnings("unchecked")
	private void deployModules(final IRuntime runtime) throws Exception {
		final int size = this.modules.size();
		for (int i = 0; i < size; i++) {
			final ModuleNode module = this.modules.get(i);
			// Load and instantiate module.
			final Class<? extends IModule> moduleclass = (Class<? extends IModule>)this.getClass().getClassLoader().loadClass(module.classname);
			// Add module.
			InputStream configStream = null;
			if (module.configLocation != null) {
				final URL configURL = new File(module.configLocation).toURI().toURL();
				configStream = configURL.openStream();
			}
			runtime.add(moduleclass, configStream, module.resourcesDir);
		}
	}

	/**
	 * Add all the modules defined in the HBM file at
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
		final int size = hbm.modules.size();
		for (int i = 0; i < size; i++) {
			final HBMModule module = hbm.modules.get(i);
			final ModuleNode node = new ModuleNode(module.classname, module.configFile, module.resourcesDir);
			this.addModule(node);
		}
	}

	/**
	 * Add the given module node for debugging.
	 * @param module The <code>ModuleNode</code> to
	 * add.
	 */
	public void addModule(final ModuleNode module) {
		this.modules.add(module);
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
