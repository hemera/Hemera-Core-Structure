package hemera.core.structure.interfaces;

import java.io.File;
import java.util.List;

import org.w3c.dom.Document;

import hemera.core.execution.interfaces.IExecutionService;
import hemera.core.structure.interfaces.runtime.IRuntimeHandle;

/**
 * <code>IModule</code> defines the interface of an
 * application-level container unit that is responsible
 * for providing a specific functionality or feature of
 * the application environment. It is responsible for
 * the construction and maintenance of a set of
 * <code>IProcessor</code> instances, along with the
 * corresponding application state information data.
 * <p>
 * <code>IModule</code> is designed to be a completely
 * self-contained, enclosed unit that does not require
 * any external assistance to provide the functionality
 * it is responsible for. It internally maintains the
 * smallest subset of the application state data just
 * enough to allow the corresponding operations to be
 * performed completely within the module itself. It
 * can be considered as the smallest grouping of state
 * information, and logic to support proper operations
 * of the responsible functionality or feature.
 * <p>
 * An instance of <code>IModule</code> is defined as a
 * container REST resource that can be accessed at the
 * defined path via HTTP requests. The module delegates
 * the actual request processing and response generation
 * to its processors. Each processor is a sub-resource
 * of the module container resource. A typical access
 * should be made at <code>/module/processor</code>.
 * <p>
 * <code>IModule</code> defines the injection methods
 * to allow hosting runtime environment to inject the
 * provided services and utility units into the module,
 * thus allowing the module instance to utilize these
 * provided functionalities at runtime. All the injection
 * methods are guaranteed to be invoked only once right
 * before module's customization stage. The method
 * implementations should typically just store injected
 * instance into a class field for runtime usage.
 * <p>
 * An <code>IModule</code> consists of five life-stages,
 * instantiation, customization, initialization,
 * activation and disposal. The runtime environment is
 * responsible for managing the life-cycles of hosted
 * modules and invoking the proper methods for each stage.
 * <p>
 * The instantiation stage occurs when a module class is
 * added to the runtime environment. Runtime environment
 * uses dynamic class loading mechanisms to create a new
 * instance of the module class. This stage allows the
 * module to properly instantiate internal structures
 * used to maintain the state information of the module.
 * <p>
 * Customization stage is an optional stage that allows
 * the module unit to set initial values to various
 * fields based on an external data store, typically a
 * disk configuration file is used. The runtime passes
 * in a <code>Configuration</code> instance containing
 * the module's configuration key-value pairs.
 * <p>
 * The initialization stage allows the module to properly
 * initialize all internal structures that may require
 * separate construction and initialization processes.
 * During the initialization stage, processors are
 * instantiated and registered. Optionally, modules may
 * perform any procedures that are required to be
 * completed before the module is activated.
 * <p>
 * The activation stage allows the module to activate
 * all of its internal components and submit initial
 * execution tasks if necessary.
 * <p>
 * Finally the disposal stage occurs if a module is
 * removed from the runtime environment or if runtime
 * environment is shutdown entirely. This allows module
 * to dispose its internal components, cease execution
 * of all the tasks that the module keeps track of,
 * and properly dispose all allocated system resources.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IModule extends IRESTResource {
	
	/**
	 * Inject the runtime execution service to this module
	 * instance to provide task execution support.
	 * <p>
	 * This method is guaranteed to be invoked only once
	 * right before the module's customization stage. The
	 * implementation should typically just store injected
	 * instance into a class field for runtime usage.
	 * @param service The <code>IExecutionService</code>
	 * instance provided by the runtime.
	 */
	public void inject(final IExecutionService service);
	
	/**
	 * Inject the given runtime handle into this module
	 * unit to allow the module to notify system runtime
	 * environment upon user requests.
	 * <p>
	 * This method is guaranteed to be invoked only once
	 * right before the module's customization stage. The
	 * implementation should typically just store injected
	 * instance into a class data field for runtime usage.
	 * @param handle The <code>IRuntimeHandle</code>
	 * instance.
	 */
	public void inject(final IRuntimeHandle handle);
	
	/**
	 * Inject the module resource files into this module.
	 * <p>
	 * This method is guaranteed to be invoked only once
	 * right before the module's customization stage. The
	 * implementation should typically just store injected
	 * instance into a class data field for runtime usage.
	 * @param resources The <code>List</code> of all the
	 * module resource <code>File</code>. <code>null</code>
	 * if the module does not have any resource files.
	 */
	public void inject(final List<File> resources);
	
	/**
	 * Customize the initial values of various data
	 * fields with the values in given configuration.
	 * <p>
	 * This is the second stage of the module life-
	 * cycle, after instantiation. All services have
	 * been properly injected into the module.
	 * <p>
	 * This method should provide all the necessary
	 * implementation to confirm with specified module
	 * customization life-cycle design.
	 * <p>
	 * This method does not provide any thread-safety
	 * guarantees as it is only meant to be invoked
	 * once by the module initialization thread. Any
	 * external invocations should be avoided.
	 * @param config The XML <code>Document</code>
	 * that holds all the loaded configuration data.
	 * @throws Exception If any customization logic
	 * failed.
	 */
	public void customize(final Document config) throws Exception;

	/**
	 * Initialize the module unit and all the internal
	 * module components. This is the third stage of
	 * the module life-cycle, after customization.
	 * <p>
	 * This method should provide all the necessary
	 * implementation to confirm with specified module
	 * initialization life-cycle design.
	 * <p>
	 * This method does not provide any thread-safety
	 * guarantees as it is only meant to be invoked
	 * once by the module initialization thread. All
	 * external invocations should be avoided.
	 * @throws Exception If any initialization logic
	 * failed.
	 */
	public void initialize() throws Exception;
	
	/**
	 * Activate the module along with all its internal
	 * components. This is the fourth stage of module
	 * life-cycle, after initialization.
	 * <p>
	 * This method should provide all the necessary
	 * implementation to confirm with specified module
	 * activation life-cycle design.
	 * <p>
	 * This method does not provide any thread-safety
	 * guarantees as it is only meant to be invoked
	 * once by the module initialization thread. Any
	 * external invocations should be avoided.
	 * @throws Exception If any activation logic failed.
	 */
	public void activate() throws Exception;
	
	/**
	 * Dispose the resources used by the module and its
	 * internal structures.
	 * <p>
	 * This method should provide all the necessary
	 * implementation to confirm with specified module
	 * disposal life-cycle design.
	 * <p>
	 * This method does not provide any thread-safety
	 * guarantees as it is only meant to be invoked
	 * once by the module initialization thread. Any
	 * external invocations should be avoided.
	 * @throws Exception If the process failed.
	 */
	public void dispose() throws Exception;
	
	/**
	 * Retrieve all the processor instances that this
	 * module utilizes.
	 * <p>
	 * The returned value is used in the initialization
	 * process once. The values should not change from
	 * time to time as the processors should be defined
	 * at the initialization time of a module.
	 * @return The <code>Iterable</code> of all the
	 * <code>IProcessor</code> instances.
	 */
	public Iterable<IProcessor<?, ?>> getProcessors();
	
	/**
	 * Retrieve the processor defined for the given
	 * REST access path.
	 * <p>
	 * The returned value is set in the initialization
	 * process once. The value should not change from
	 * time to time as the processors should be defined
	 * at the initialization time of a module.
	 * @param path The <code>String</code> REST access
	 * path to check.
	 * @return The <code>IProcessor</code> instance.
	 */
	public IProcessor<?, ?> getProcessor(final String path);
}
