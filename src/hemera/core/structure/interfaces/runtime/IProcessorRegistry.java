package hemera.core.structure.interfaces.runtime;

import hemera.core.structure.interfaces.IProcessor;
import hemera.core.structure.interfaces.IRequest;
import hemera.core.structure.interfaces.IResponse;

/**
 * <code>IProcessorRegistry</code> defines the interface
 * of a read-only repository for all the processors of
 * all the modules that are currently hosted by the
 * runtime environment. It provides a read-only interface
 * to allow retrieval of processors based on specified
 * REST access paths.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IProcessorRegistry {

	/**
	 * Retrieve the processor defined at the specified
	 * REST access path.
	 * @param T The <code>IRequest</code> type.
	 * @param R The <code>IResponse</code> type.
	 * @param path The <code>String</code> access path.
	 * @return The <code>IProcessor</code> instance. Or
	 * <code>null</code> if there is none.
	 */
	public <T extends IRequest, R extends IResponse> IProcessor<T, R> getProcessor(final String path);
}
