package hemera.core.structure.interfaces.runtime;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import hemera.core.structure.interfaces.IResource;
import hemera.core.structure.interfaces.IResourceRegistry;

/**
 * <code>IRuntime</code> defines the interface of the
 * system environment container that hosts a set of
 * resource instances that can be accessed via defined
 * REST access paths forming an application. It provides
 * a set of essential hardware abstraction services to
 * the hosted resource instances.
 * <p>
 * <code>IRuntime</code> fully handles the life-cycle
 * of added resource instances. It follows the resource
 * defined five-stage life-cycle design and invokes
 * appropriate methods for each of the stages. More
 * specifically, upon successful adding, a resource
 * instance is instantiated, customized, initialized,
 * then activated. And all added instances are disposed
 * when removed or during runtime shutdown.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IRuntime extends IResourceRegistry {

	/**
	 * Activate the runtime environment host.
	 * <p>
	 * This method activates all the internal services
	 * provided by the runtime environment for resource
	 * handling and utilization.
	 * <p>
	 * This method utilizes synchronization to ensure
	 * that only a single invocation is performed. It
	 * blocks all concurrent duplicate invocations and
	 * only allow a single one to execute. This method
	 * shares the same lock as the <code>shutdown</code>
	 * method.
	 */
	public void activate();
	
	/**
	 * Shutdown the runtime environment along with all
	 * of its services, and also dispose all of the
	 * maintained resource instances.
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
	 * maintained resource instances.
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
	 * Add an instance of the given resource class to the
	 * runtime environment with its defined REST path as
	 * its unique identifier, and the specified
	 * configuration stream for resource customization.
	 * <p>
	 * This method provides the necessary thread-safety
	 * guarantees with high concurrency capabilities.
	 * @param resourceClass The <code>Class</code> of the
	 * resource to be added and hosted by the runtime.
	 * @param configStream The optional configuration
	 * <code>InputStream</code> used to customize the
	 * given resource. <code>null</code> if the resource
	 * does not need to be customized.
	 * @param resources The optional <code>List</code>
	 * of resources <code>File</code>. If the resource
	 * does not have any resources, <code>null</code>.
	 * @return <code>true</code> if resource instance is
	 * successfully added and hosted. <code>false</code>
	 * if there already exists a resource defined at the
	 * REST path.
	 * @throws Exception If any procedure failed.
	 */
	public boolean add(final Class<? extends IResource> resourceClass, final InputStream configStream, final List<File> resources) throws Exception;
	
	/**
	 * Remove the resource instance with with given REST
	 * path from the runtime environment and dispose it.
	 * <p>
	 * This method automatically invokes the resource's
	 * <code>dispose</code> method to perform disposal.
	 * @param path The <code>String</code> REST access
	 * path to check.
	 * @return <code>true</code> if such a resource exists
	 * and is successfully removed and disposed.
	 * <code>false</code> if there is no such resource.
	 * @throws Exception If resource disposal failed.
	 */
	public boolean remove(final String path) throws Exception;
}
