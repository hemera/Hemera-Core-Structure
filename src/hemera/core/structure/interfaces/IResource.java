package hemera.core.structure.interfaces;

import java.io.File;
import java.util.List;

import org.w3c.dom.Document;

import hemera.core.execution.interfaces.IExecutionService;
import hemera.core.structure.enumn.EHttpMethod;
import hemera.core.structure.interfaces.runtime.util.IRuntimeHandle;

/**
 * <code>IResource</code> defines the interface of an
 * application resource that can be accessed via a set
 * of <code>IProcessor</code> each corresponding to a
 * particular HTTP method to maintain and change state
 * information.
 * <p>
 * <code>IResource</code> is designed to be a completely
 * self-contained, enclosed unit that does not require
 * any external assistance to provide the functionality
 * it is responsible for. It internally maintains the
 * only the subset of the application state data that
 * only corresponds to the particular resource type.
 * <p>
 * An <code>IResource</code> consists of five life-cycle
 * stages: instantiation, customization, initialization,
 * activation and disposal. The runtime environment is
 * responsible for managing the life-cycles of hosted
 * resources and invoking the proper methods at every
 * stage.
 * <p>
 * The instantiation stage occurs when a resource class
 * is added to the runtime environment. The Runtime
 * environment uses dynamic class loading mechanisms to
 * create a new instance of the resource class. This
 * stage allows the resource to properly instantiate
 * internal structures used to maintain the state
 * information of the resource.
 * <p>
 * Customization stage is an optional stage that allows
 * the resource unit to set initial values to various
 * fields based on an external data store, typically a
 * disk configuration file is used. The runtime passes
 * in a XML <code>Document</code> instance containing
 * the resource's configuration values.
 * <p>
 * The initialization stage allows the resource unit
 * to properly initialize all the internal structures
 * that may require separate construction and
 * initialization processes. During the initialization
 * stage, processors are instantiated and registered.
 * Optionally, a resource may perform any procedures
 * that are required to be completed before the resource
 * is activated.
 * <p>
 * The activation stage allows the resource to activate
 * all of its internal components and submit initial
 * execution tasks if necessary.
 * <p>
 * Finally the disposal stage occurs if a resource is
 * removed from the runtime environment or if runtime
 * environment is shutdown entirely. This allows the
 * resource to dispose its internal components, cease
 * execution of all the tasks that the resource keeps
 * track of, and properly dispose all allocated system
 * resources.
 * <p>
 * <code>IResource</code> defines the injection methods
 * to allow hosting runtime environment to inject the
 * provided services and utility units into the resource,
 * thus allowing the resource instance to utilize these
 * provided functionalities at runtime. All the injection
 * methods are guaranteed to be invoked only once right
 * before resource's customization stage. The method
 * implementations should typically just store injected
 * instance into a class field for runtime usage.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IResource {
	
	/**
	 * Inject the runtime execution service to this
	 * resource to provide task execution support.
	 * <p>
	 * This method is guaranteed to be invoked only once
	 * right before the resource's customization stage.
	 * The implementation should typically just store
	 * injected instance into a class field for runtime
	 * usage.
	 * @param service The <code>IExecutionService</code>
	 * instance provided by the runtime.
	 */
	public void inject(final IExecutionService service);
	
	/**
	 * Inject the given runtime handle into this resource
	 * unit to allow the resource to notify system runtime
	 * environment upon user requests.
	 * <p>
	 * This method is guaranteed to be invoked only once
	 * right before the resource's customization stage.
	 * The implementation should typically just store
	 * injected instance into a class field for runtime
	 * usage.
	 * @param handle The <code>IRuntimeHandle</code>
	 * instance.
	 */
	public void inject(final IRuntimeHandle handle);
	
	/**
	 * Inject the external resource files into this
	 * resource.
	 * <p>
	 * This method is guaranteed to be invoked only once
	 * right before the resource's customization stage.
	 * The implementation should typically just store
	 * injected instance into a class field for runtime
	 * usage.
	 * @param resources The <code>List</code> of all the
	 * resources <code>File</code>. <code>null</code>
	 * if the resource does not have any resource files.
	 */
	public void inject(final List<File> resources);
	
	/**
	 * Customize the initial values of various data
	 * fields with the values in given configuration.
	 * <p>
	 * This is the second stage of the resource life-
	 * cycle, after instantiation. All services have
	 * been properly injected into the resource.
	 * <p>
	 * This method should provide all the necessary
	 * implementation to confirm with specified resource
	 * customization life-cycle design.
	 * <p>
	 * This method does not provide any thread-safety
	 * guarantees as it is only meant to be invoked
	 * once by the resource initialization thread. Any
	 * external invocations should be avoided.
	 * @param config The XML <code>Document</code>
	 * that holds all the loaded configuration data.
	 * @throws Exception If any customization logic
	 * failed.
	 */
	public void customize(final Document config) throws Exception;

	/**
	 * Initialize the resource unit and all the internal
	 * components. This is the third stage of resource
	 * life-cycle, after customization.
	 * <p>
	 * This method should provide all the necessary
	 * implementation to confirm with specified resource
	 * initialization life-cycle design.
	 * <p>
	 * This method does not provide any thread-safety
	 * guarantees as it is only meant to be invoked
	 * once by the resource initialization thread. All
	 * external invocations should be avoided.
	 * @throws Exception If any initialization logic
	 * failed.
	 */
	public void initialize() throws Exception;
	
	/**
	 * Activate the resource along with all its internal
	 * components. This is the fourth stage of resource
	 * life-cycle, after initialization.
	 * <p>
	 * This resource should provide all the necessary
	 * implementation to confirm with specified resource
	 * activation life-cycle design.
	 * <p>
	 * This method does not provide any thread-safety
	 * guarantees as it is only meant to be invoked
	 * once by the resource initialization thread. Any
	 * external invocations should be avoided.
	 * @throws Exception If any activation logic failed.
	 */
	public void activate() throws Exception;
	
	/**
	 * Dispose the resources used by the resource and
	 * its internal structures.
	 * <p>
	 * This method should provide all the necessary
	 * implementation to confirm with specified resource
	 * disposal life-cycle design.
	 * <p>
	 * This method does not provide any thread-safety
	 * guarantees as it is only meant to be invoked
	 * once by the resource initialization thread. Any
	 * external invocations should be avoided.
	 * @throws Exception If the process failed.
	 */
	public void dispose() throws Exception;
	
	/**
	 * Retrieve the HTTP access path for this resource.
	 * @return The <code>String</code> access path.
	 */
	public String getPath();
	
	/**
	 * Retrieve the processor responsible for given
	 * HTTP method.
	 * @param method The <code>EHttpMethod</code> value.
	 * @return The <code>IProcessor</code> instance.
	 * <code>null</code> if there are no processors
	 * provided for the method.
	 */
	public IProcessor<?, ?> getProcessor(final EHttpMethod method);
}
