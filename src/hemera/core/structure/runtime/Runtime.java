package hemera.core.structure.runtime;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.w3c.dom.Document;

import hemera.core.execution.interfaces.IExecutionService;
import hemera.core.structure.interfaces.IModule;
import hemera.core.structure.interfaces.runtime.IProcessorRegistry;
import hemera.core.structure.interfaces.runtime.IRuntime;
import hemera.core.structure.interfaces.runtime.util.IRuntimeHandle;
import hemera.core.utility.FileUtils;
import hemera.core.utility.logging.FileLogger;

/**
 * <code>Runtime</code> defines the abstraction of the
 * execution and structure environment for all the
 * running modules on a physical or virtual machine.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public abstract class Runtime implements IRuntime {
	/**
	 * The <code>FileLogger</code> for runtime.
	 */
	protected final FileLogger logger;
	/**
	 * The <code>IExecutionService</code> instance.
	 */
	protected final IExecutionService service;
	/**
	 * The <code>IRuntimeHandle</code> instance.
	 */
	protected final IRuntimeHandle handle;
	/**
	 * The <code>ProcessorRegistry</code> instance.
	 */
	private final ProcessorRegistry registry;
	/**
	 * The <code>Lock</code> used to synchronize the
	 * <code>activate</code> and <code>shutdown</code>
	 * methods.
	 */
	private final Lock lock;
	/**
	 * The <code>ConcurrentMap</code> of REST path
	 * <code>String</code> to <code>IModule</code>
	 * instance.
	 */
	private final ConcurrentMap<String, IModule> modules;
	/**
	 * The <code>boolean</code> flag indicating if the
	 * runtime environment has been activated.
	 * <p>
	 * This field is guarded by the <code>activate</code>
	 * and <code>shutdown</code> method lock.
	 */
	private boolean activated;

	/**
	 * Constructor of <code>Runtime</code>.
	 * @param service The <code>IExecutionService</code>
	 * used to dispatch request processing.
	 */
	protected Runtime(final IExecutionService service) {
		this.logger = FileLogger.getLogger(Runtime.class);
		this.service = service;
		this.handle = new RuntimeHandle();
		this.registry = new ProcessorRegistry();
		// Internal fields.
		this.lock = new ReentrantLock();
		// Use default concurrency level since the number of
		// concurrent updating threads shouldn't be too large.
		this.modules = new ConcurrentHashMap<String, IModule>();
		this.activated = false;
	}

	@Override
	public final void activate() {
		this.lock.lock();
		try {
			if (this.activated) return;
			this.logger.info("Activating runtime environment...");
			// Basic services.
			this.service.activate();
			// Perform type specific components activation.
			this.activateComponents();
			// Set flag.
			this.activated = true;
			this.logger.info("Runtime environment activated");
		} catch (Exception e) {
			this.logger.severe("Runtime activation failed");
			this.logger.exception(e);
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * Perform the runtime type specific components
	 * activation process.
	 * <p>
	 * This method is guaranteed to be invoked once
	 * and once only under the activation lock. This
	 * is the last step of the runtime environment
	 * activation process, after all the services
	 * have been activated.
	 * @throws Exception If any procedures failed.
	 */
	protected abstract void activateComponents() throws Exception;

	@Override
	public final void shutdown() {
		this.shutdown(false);
	}

	@Override
	public final void shutdownAwait() {
		this.shutdown(true);
	}

	/**
	 * Shutdown the runtime and wait based on given
	 * boolean value.
	 * @param wait <code>true</code> if wait before
	 * returning. <code>false</code> otherwise.
	 */
	private void shutdown(final boolean wait) {
		this.lock.lock();
		try {
			if (!this.activated) return;
			this.logger.info("Runtime shutting down...");
			// Dispose all modules.
			for(final IModule module : this.modules.values()) {
				try {
					module.dispose();
				} catch (final Exception e) {
					this.logger.severe("Failed to dispose module: " + module.getClass().getName());
					this.logger.exception(e);
				}
			}
			// Subclass shutdown.
			try {
				this.shutdownComponents();
			} catch (final Exception e) {
				this.logger.severe("Failed to shutdown runtime type specific procedures");
				this.logger.exception(e);
			}
			// Shutdown services.
			try {
				if (wait) this.service.shutdownAndWait();
				else this.service.shutdown();
			} catch (final Exception e) {
				this.logger.severe("Failed to shutdown execution service");
			}
			// Set flag.
			this.activated = false;
			this.logger.info("Runtime shutdown completed");
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * Perform the runtime type specific components
	 * shutdown logic.
	 * <p>
	 * All the hosted modules have been disposed at
	 * the time this method is invoked, though all
	 * the basic services are still active. Services
	 * are shut down after the completion of this
	 * method.
	 * @throws Exception If any shutdown procedure
	 * failed.
	 */
	protected abstract void shutdownComponents() throws Exception;

	@Override
	public final boolean add(final Class<? extends IModule> moduleclass, final InputStream configStream, final List<File> resources) throws Exception {
		try {
			this.statusCheck();
			// Cache module instance and check for duplicate.
			final IModule module = this.addCache(moduleclass);
			if (module == null) return false;
			//  Injection services.
			this.injectServices(module);
			// Inject resources.
			module.inject(resources);
			// Customization.
			if (configStream != null) {
				final Document config = FileUtils.instance.readAsDocument(configStream);
				module.customize(config);
			}
			// Initialization.
			module.initialize();
			// Activation.
			module.activate();
			// Register processors after module is fully activated
			// so incoming requests can be processed properly.
			this.registry.register(module);
			// Logging.
			final StringBuilder builder = new StringBuilder();
			builder.append("Addition of module: ").append(moduleclass.getName());
			builder.append(" succeeded");
			this.logger.info(builder.toString());
			return true;
		} catch (final Exception e) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Addition of module: ").append(moduleclass.getName());
			builder.append(" failed");
			this.logger.severe(builder.toString());
			throw e;
		}
	}

	/**
	 * Add an instance of the given module class into
	 * the module cache store if it is not already added.
	 * All duplicates are ignored and logged.
	 * @param moduleclass The <code>Class</code> of the
	 * module to be added and hosted by the runtime.
	 * @return The added <code>IModule</code> instance if
	 * the insertion is successful. <code>null</code> if
	 * the module defined REST path already exists.
	 * @throws IllegalAccessException If instantiation
	 * failed.
	 * @throws InstantiationException If instantiation
	 * failed.
	 */
	private IModule addCache(final Class<? extends IModule> moduleclass) throws InstantiationException, IllegalAccessException {
		final IModule module = moduleclass.newInstance();
		final String path = module.getPath();
		// Early check.
		if (this.modules.containsKey(path)) return null;
		// Try to add.
		final IModule prev = this.modules.putIfAbsent(path, module);
		final boolean succeeded = (prev == null);
		// Log duplicates.
		if (!succeeded) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Duplicate addition of module at REST path: ").append(path);
			this.logger.warning(builder.toString());
			return null;
		}
		return module;
	}

	/**
	 * Inject the basic services into the module.
	 * <p>
	 * This method may be override to allow injection
	 * of additional runtime type specific services.
	 * @param module The <code>IModule</code> to be
	 * injected with services.
	 */
	protected void injectServices(final IModule module) {
		module.inject(this.service);
		module.inject(this.handle);
	}

	@Override
	public final boolean remove(final String path) throws Exception {
		this.statusCheck();
		// Remove from cache.
		final IModule module = this.modules.remove(path);
		if (module == null) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Removing module at REST path: ").append(path);
			builder.append("failed. Runtime does not host the module");
			this.logger.warning(builder.toString());
			return false;
		}
		try {
			module.dispose();
			// Logging.
			final StringBuilder builder = new StringBuilder();
			builder.append("Module at REST path: ").append(path);
			builder.append(" removed from runtime environment");
			this.logger.info(builder.toString());
			return true;
		} catch (Exception e) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Removing of module at REST path: ").append(path);
			builder.append(" failed");
			this.logger.severe(builder.toString());
			throw e;
		}
	}

	/**
	 * Check the status of this runtime and throw
	 * exceptions if current status is invalid.
	 */
	private void statusCheck() {
		if (!this.activated) {
			throw new IllegalStateException("Runtime has not yet been activated.");
		}
	}

	@Override
	public IModule getModule(final String path) {
		return this.modules.get(path);
	}
	
	@Override
	public IProcessorRegistry getProcessorRegistry() {
		return this.registry;
	}

	/**
	 * <code>RuntimeHandle</code> defines the implementation
	 * of a runtime specific handle unit that conforms to
	 * <code>IRuntimeHandle</code> and performs the proper
	 * invocations on runtime instance.
	 *
	 * @author Yi Wang (Neakor)
	 * @version 1.0.0
	 */
	private class RuntimeHandle implements IRuntimeHandle {

		@Override
		public void shutdown() {
			Runtime.this.shutdown();
		}
	}
}
