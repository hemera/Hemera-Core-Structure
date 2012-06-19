package hemera.core.structure;

import java.util.HashMap;
import java.util.Map;

import hemera.core.execution.interfaces.IExecutionService;
import hemera.core.structure.interfaces.IModule;
import hemera.core.structure.interfaces.IProcessor;
import hemera.core.structure.interfaces.runtime.IRuntimeHandle;
import hemera.core.utility.logging.FileLogger;

/**
 * <code>AbstractModule</code> defines abstraction of
 * a module unit within the application system. It is
 * the container for various processors that form a
 * set of features and functionalities.
 * <p>
 * <code>AbstractModule</code> only provides the most
 * commonly shared implementations among all modules.
 * It defines some of the module life-cycle stages.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public abstract class AbstractModule implements IModule {
	/**
	 * The <code>FileLogger</code> instance.
	 */
	protected final FileLogger logger;
	/**
	 * The <code>Map</code> of <code>String</code>
	 * REST path to <code>IProcessor</code> instances.
	 * <p>
	 * The contents of this map should not change once
	 * the initialization stage completes, thus thread
	 * safety is not a concern.
	 */
	private final Map<String, IProcessor<?, ?>> processors;
	/**
	 * The <code>IExecutionService</code> instance.
	 * <p>
	 * This reference is properly set with the proper
	 * instance after the instantiation stage of the
	 * module unit but before the initialization stage.
	 */
	protected IExecutionService execution;
	/**
	 * The <code>IRuntimeHandle</code> instance.
	 * <p>
	 * This reference is properly set with the proper
	 * instance after the instantiation stage of the
	 * module unit but before the initialization stage.
	 */
	protected IRuntimeHandle runtimeHandle;
	
	/**
	 * Constructor of <code>AbstractModule</code>.
	 */
	protected AbstractModule() {
		this.logger = FileLogger.getLogger(this.getClass());
		this.processors = new HashMap<String, IProcessor<?, ?>>();
	}
	
	@Override
	public final void inject(final IExecutionService service) {
		this.execution = service;
	}
	
	@Override
	public final void inject(final IRuntimeHandle handle) {
		this.runtimeHandle = handle;
	}
	
	@Override
	public void initialize() throws Exception {
		// Build module specific components.
		this.buildComponents();
		// Build processors.
		final IProcessor<?, ?>[] processors = this.buildProcessors();
		if (processors != null) {
			for (int i = 0; i < processors.length; i++) {
				IProcessor<?, ?> processor = processors[i];
				final String path = processor.getPath();
				final Object existing = this.processors.put(path, processor);
				// Override processor.
				if (existing != null) {
					final StringBuilder builder = new StringBuilder();
					builder.append("There are more than one processor defined at the same REST path:");
					builder.append(path).append(".");
					this.logger.warning(builder.toString());
				}
			}
		}
	}
	
	/**
	 * Construct module specific internal components.
	 * <p>
	 * This method is invoked before all processors are
	 * constructed. This is part of module initialization
	 * stage.
	 * @throws Exception If any construction failed.
	 */
	protected abstract void buildComponents() throws Exception;

	/**
	 * Construct all the processor units.
	 * <p>
	 * All runtime injected services have been properly
	 * stored at this point. They can directly passed
	 * onto processor structures.
	 * @return The array of all <code>IProcessor</code>
	 * instances. <code>null</code> if this module does
	 * not utilize any processors.
	 */
	protected abstract IProcessor<?, ?>[] buildProcessors();
	
	@Override
	public void dispose() throws Exception {
		// Deactivate all processors.
		final Iterable<String> keys = this.processors.keySet();
		for (final String key : keys) {
			final IProcessor<?, ?> processor = this.processors.get(key);
			processor.setActive(false);
		}
		// Dispose module specific components.
		this.disposeComponents();
	}
	
	/**
	 * Dispose the module specific internal components.
	 * <p>
	 * This method is invoked after all the processors
	 * have been deactivated.
	 * @throws Exception If any disposal failed.
	 */
	protected abstract void disposeComponents() throws Exception;
	
	@Override
	public Iterable<IProcessor<?, ?>> getProcessors() {
		return this.processors.values();
	}
	
	@Override
	public IProcessor<?, ?> getProcessor(final String path) {
		return this.processors.get(path);
	}
}
