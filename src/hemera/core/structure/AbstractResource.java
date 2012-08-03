package hemera.core.structure;

import java.io.File;
import java.util.List;

import hemera.core.execution.interfaces.IExecutionService;
import hemera.core.structure.interfaces.IResource;
import hemera.core.structure.interfaces.runtime.util.IRuntimeHandle;
import hemera.core.utility.logging.FileLogger;

/**
 * <code>AbstractResource</code> defines abstraction of
 * a resource unit within the application system. It is
 * the container for various processors that form a
 * set of features and functionalities.
 * <p>
 * <code>AbstractResource</code> only provides the most
 * commonly shared implementations among all resources.
 * It defines some of the resource life-cycle stages.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public abstract class AbstractResource implements IResource {
	/**
	 * The <code>FileLogger</code> instance.
	 */
	protected final FileLogger logger;
	/**
	 * The <code>IExecutionService</code> instance.
	 * <p>
	 * This reference is properly set with the proper
	 * instance after the instantiation stage of the
	 * resource unit but before the initialization stage.
	 */
	protected IExecutionService execution;
	/**
	 * The <code>IRuntimeHandle</code> instance.
	 * <p>
	 * This reference is properly set with the proper
	 * instance after the instantiation stage of the
	 * resource unit but before the initialization stage.
	 */
	protected IRuntimeHandle runtimeHandle;
	/**
	 * The <code>List</code> of all the resources
	 * <code>File</code>. <code>null</code> if this
	 * resource does not have any resource files.
	 */
	protected List<File> resources;
	
	/**
	 * Constructor of <code>AbstractResource</code>.
	 */
	protected AbstractResource() {
		this.logger = FileLogger.getLogger(this.getClass());
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
	public final void inject(final List<File> resources) {
		this.resources = resources;
	}
}
