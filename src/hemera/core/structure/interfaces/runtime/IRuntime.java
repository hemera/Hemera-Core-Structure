package hemera.core.structure.interfaces.runtime;

import java.io.InputStream;

import hemera.core.structure.interfaces.IModule;

/**
 * <code>IRuntime</code> defines the interface of the
 * system environment container that hosts a set of
 * module instances forming an application. It provides
 * a set of essential hardware abstraction services to
 * the hosted module instances.
 * <p>
 * <code>IRuntime</code> fully handles the life-cycle
 * of added module instances. It follows module defined
 * five-stage life-cycle design and invokes appropriate
 * methods for each of the stages. Specifically, upon
 * successful adding, a module instance is instantiated,
 * customized, initialized, then activated. And all added
 * instances are disposed when removed or during runtime
 * shutdown.
 * <p>
 * A single instance of <code>IRuntime</code> should
 * be allocated for a single physical or virtual
 * machine, as its internal services are automatically
 * adjusted to fully utilize the underlying hardware.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IRuntime {

	/**
	 * Activate the runtime environment host.
	 * <p>
	 * This method activates all the internal services
	 * provided by the runtime environment for module
	 * handling and utilization.
	 * <p>
	 * This method utilizes synchronization to ensure
	 * that only a single invocation is performed. It
	 * blocks all concurrent duplicate invocations and
	 * only allow a single one to execute. This method
	 * shares the same lock as the <code>shutdown</code>
	 * method.
	 * @throws Exception If any activation procedure
	 * failed.
	 */
	public void activate() throws Exception;
	
	/**
	 * Shutdown the runtime environment along with all
	 * of its services, and also dispose all of the
	 * maintained module instances.
	 * <p>
	 * This method utilizes synchronization to ensure
	 * that only a single invocation is performed. It
	 * blocks all concurrent duplicate invocations and
	 * only allow a single one to execute. This method
	 * shares the same lock as the <code>activate</code>
	 * method.
	 * <p>
	 * This method does not wait for all services to
	 * completely shutdown before returning, making it
	 * possible to invoke this method within a processor
	 * thread.
	 * <p>
	 * This method internally catches all exceptions at
	 * every single procedure step to ensure each shut
	 * down step is properly executed. It is guaranteed
	 * that this method does not throw any exceptions.
	 */
	public void shutdown();
	
	/**
	 * Shutdown the runtime environment along with all
	 * of its services, and also dispose all of the
	 * maintained module instances.
	 * <p>
	 * This method utilizes synchronization to ensure
	 * that only a single invocation is performed. It
	 * blocks all concurrent duplicate invocations and
	 * only allow a single one to execute. This method
	 * shares the same lock as the <code>activate</code>
	 * method.
	 * <p>
	 * This method waits for all services to completely
	 * shutdown before returning.
	 * <p>
	 * This method internally catches all exceptions at
	 * every single procedure step to ensure each shut
	 * down step is properly executed. It is guaranteed
	 * that this method does not throw any exceptions.
	 */
	public void shutdownAwait();
	
	/**
	 * Add an instance of the given module class to the
	 * runtime environment with its implementation class
	 * name as its unique identifier.
	 * <p>
	 * Adding modules using this method will skip the
	 * module's customization stage.
	 * <p>
	 * This method provides the necessary thread-safety
	 * guarantees with high concurrency capabilities.
	 * @param moduleclass The <code>Class</code> of the
	 * module to be added and hosted by the runtime.
	 * @return <code>true</code> if module instance is
	 * successfully added and hosted. <code>false</code>
	 * if there already exists an instance of the same
	 * class.
	 * @throws Exception If any procedure failed.
	 */
	public boolean add(final Class<? extends IModule> moduleclass) throws Exception;
	
	/**
	 * Add an instance of the given module class to the
	 * runtime environment with its implementation class
	 * name as its unique identifier, and the specified
	 * configuration stream for module customization.
	 * <p>
	 * This method provides the necessary thread-safety
	 * guarantees with high concurrency capabilities.
	 * @param moduleclass The <code>Class</code> of the
	 * module to be added and hosted by the runtime.
	 * @param configStream The optional configuration
	 * <code>InputStream</code> used to customize the
	 * given module. <code>null</code> if the module
	 * does not need to be customized.
	 * @return <code>true</code> if module instance is
	 * successfully added and hosted. <code>false</code>
	 * if there already exists an instance of the same
	 * class.
	 * @throws Exception If any procedure failed.
	 */
	public boolean add(final Class<? extends IModule> moduleclass, final InputStream configStream) throws Exception;
	
	/**
	 * Remove the module instance with with given class
	 * from the runtime environment and dispose it.
	 * <p>
	 * This method automatically invokes the module's
	 * <code>dispose</code> method to perform disposal.
	 * @param moduleclass The <code>Class</code> of the
	 * module to be removed from the runtime.
	 * @return <code>true</code> if such a module exists
	 * and is successfully removed and disposed.
	 * <code>false</code> if there is no such module.
	 * @throws Exception If module disposal failed.
	 */
	public boolean remove(final Class<? extends IModule> moduleclass) throws Exception;
	
	/**
	 * Retrieve the module with given class.
	 * @param moduleclass The <code>Class</code> of the
	 * module to retrieve.
	 * @return The <code>IModule</code> instance. Or
	 * <code>null</code> if there is no such module.
	 */
	public IModule getModule(final Class<? extends IModule> moduleclass);
}
