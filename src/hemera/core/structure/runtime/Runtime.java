package hemera.core.structure.runtime;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.w3c.dom.Document;

import hemera.core.execution.interfaces.IExecutionService;
import hemera.core.structure.enumn.EHttpMethod;
import hemera.core.structure.interfaces.IResource;
import hemera.core.structure.interfaces.runtime.IRuntime;
import hemera.core.structure.interfaces.runtime.util.IRuntimeHandle;
import hemera.core.utility.FileUtils;
import hemera.core.utility.logging.FileLogger;
import hemera.core.utility.uri.RESTURI;

/**
 * <code>Runtime</code> defines the abstraction of the
 * execution and structure environment for all the
 * running resources on a physical or virtual machine.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.5
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
	 * The <code>Lock</code> used to synchronize the
	 * <code>activate</code> and <code>shutdown</code>
	 * methods.
	 */
	private final Lock lock;
	/**
	 * The <code>ConcurrentMap</code> of HTTP path
	 * <code>String</code> to <code>IResource</code>
	 * instance.
	 */
	private final ConcurrentMap<String, IResource> resources;
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
		// Internal fields.
		this.lock = new ReentrantLock();
		// Use default concurrency level since the number of
		// concurrent updating threads shouldn't be too large.
		this.resources = new ConcurrentHashMap<String, IResource>();
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
			this.logger.info("Runtime environment activated.");
		} catch (Exception e) {
			this.logger.severe("Runtime activation failed.");
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
		this.shutdown(false, 0, null);
	}

	@Override
	public final void shutdownAwait(final long time, final TimeUnit unit) {
		this.shutdown(true, time, unit);
	}

	/**
	 * Shutdown the runtime and wait based on given
	 * boolean value.
	 * @param wait <code>true</code> if wait before
	 * returning. <code>false</code> otherwise.
	 * @param time The <code>Long</code> wait time value.
	 * A value less than or equal to 0 means no waiting
	 * and issue forced shutdown immediately.
	 * @param unit The <code>TimeUnit</code> of the value.
	 */
	private void shutdown(final boolean wait, final long time, final TimeUnit unit) {
		this.lock.lock();
		try {
			if (!this.activated) return;
			this.logger.info("Runtime shutting down...");
			// Dispose all resources.
			for(final IResource resource : this.resources.values()) {
				try {
					this.logger.info("Disposing " + resource.getClass().getName() + "...");
					resource.dispose();
				} catch (final Exception e) {
					this.logger.severe("Failed to dispose resource: " + resource.getClass().getName());
					this.logger.exception(e);
				}
			}
			// Subclass shutdown.
			try {
				this.logger.info("Shutting down components...");
				this.shutdownComponents();
			} catch (final Exception e) {
				this.logger.severe("Failed to shutdown runtime type specific procedures");
				this.logger.exception(e);
			}
			// Shutdown services.
			try {
				this.logger.info("Shutting down execution service...");
				if (wait) this.service.forceShutdown(time, unit);
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
	 * All the hosted resources have been disposed at
	 * the time this method is invoked, though all
	 * the basic services are still active. Services
	 * are shut down after the completion of this
	 * method.
	 * @throws Exception If any shutdown procedure
	 * failed.
	 */
	protected abstract void shutdownComponents() throws Exception;

	@Override
	public final boolean add(final String applicationPath, final Class<? extends IResource> resourceClass,
			final InputStream configStream, final List<File> resources) throws Exception {
		try {
			this.statusCheck();
			// Cache resource instance and check for duplicate.
			final IResource resource = this.addResource(applicationPath, resourceClass);
			if (resource == null) return false;
			//  Injection services.
			this.injectServices(resource);
			// Inject resources.
			resource.inject(resources);
			// Customization.
			if (configStream != null) {
				final Document config = FileUtils.instance.readAsDocument(configStream);
				resource.customize(config);
			}
			// Initialization.
			resource.initialize();
			// Activation.
			resource.activate();
			// Logging.
			final StringBuilder builder = new StringBuilder();
			builder.append("Resource ").append(resourceClass.getName()).append(" deployed at path: ");
			if (applicationPath != null) builder.append("/").append(applicationPath);
			if (resource.getPath() != null) builder.append("/").append(resource.getPath());
			builder.append("/");
			this.logger.info(builder.toString());
			return true;
		} catch (final Exception e) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Deploying resource ").append(resourceClass.getName()).append(" failed.");
			this.logger.severe(builder.toString());
			throw e;
		}
	}

	/**
	 * Add an instance of the given resource class into
	 * the resources store if it is not already added.
	 * All duplicates are ignored and logged.
	 * @param applicationPath The <code>String</code>
	 * optional application path.
	 * @param resourceClass The <code>Class</code> of the
	 * resource to be added and hosted by the runtime.
	 * @return The added <code>IResource</code> instance if
	 * the insertion is successful. <code>null</code> if
	 * the resource defined REST path already exists.
	 * @throws IllegalAccessException If instantiation
	 * failed.
	 * @throws InstantiationException If instantiation
	 * failed.
	 */
	private IResource addResource(final String applicationPath, final Class<? extends IResource> resourceClass)
			throws InstantiationException, IllegalAccessException {
		// Compose path.
		final StringBuilder pathBuilder = new StringBuilder();
		if (applicationPath != null) {
			pathBuilder.append(applicationPath);
		}
		final IResource resource = resourceClass.newInstance();
		final String resourcePath = resource.getPath();
		if (resourcePath != null) {
			pathBuilder.append(resourcePath);
		}
		final String path = pathBuilder.toString();
		// Early check.
		if (this.resources.containsKey(path)) return null;
		// Try to add.
		final IResource prev = this.resources.putIfAbsent(path, resource);
		final boolean succeeded = (prev == null);
		// Log duplicates.
		if (!succeeded) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Duplicate resource at path: ");
			if (applicationPath != null) builder.append("/").append(applicationPath);
			builder.append("/").append(path).append("/ Deployment aborted.");
			this.logger.severe(builder.toString());
			return null;
		}
		return resource;
	}

	/**
	 * Inject the basic services into the resource.
	 * <p>
	 * This method may be override to allow injection
	 * of additional runtime type specific services.
	 * @param resource The <code>IResource</code> to be
	 * injected with services.
	 */
	protected void injectServices(final IResource resource) {
		resource.inject(this.service);
		resource.inject(this.handle);
	}

	@Override
	public final boolean remove(final String path) throws Exception {
		this.statusCheck();
		// Remove from cache.
		final IResource resource = this.resources.remove(path);
		if (resource == null) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Removing resource at path: ").append(path);
			builder.append("failed. Runtime does not host the resource.");
			this.logger.warning(builder.toString());
			return false;
		}
		try {
			resource.dispose();
			// Logging.
			final StringBuilder builder = new StringBuilder();
			builder.append("Resource at path: ").append(path);
			builder.append(" removed from runtime environment.");
			this.logger.info(builder.toString());
			return true;
		} catch (Exception e) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Removing resource at path: ").append(path);
			builder.append(" failed.");
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
	public IResource getResource(final RESTURI uri, final EHttpMethod method) {
		// If no URI elements, then must be root resource.
		if (uri.elements.isEmpty()) {
			return this.resources.get("");
		}
		// If URI has elements, and there is a root resource, make sure the
		// resource has the processor to handle it, since the elements may
		// be meant for a different resource or for processor paths of the
		// root resource.
		final IResource rootResource = this.resources.get("");
		if (rootResource != null) {
			final String[] path = uri.getElementArray();
			final Object processor = rootResource.getProcessor(path, method);
			if (processor != null) {
				return rootResource;
			}
		}
		// Poll each element from URI.
		final StringBuilder builder = new StringBuilder();
		while (!uri.elements.isEmpty()) {
			final String element = uri.elements.poll();
			builder.append(element);
			final IResource resource = this.resources.get(builder.toString());
			if (resource != null) {
				return resource;
			}
		}
		// Nothing found.
		return null;
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
