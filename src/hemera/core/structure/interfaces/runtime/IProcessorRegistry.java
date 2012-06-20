package hemera.core.structure.interfaces.runtime;

import hemera.core.structure.interfaces.IProcessor;

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
	 * @param path The <code>String</code> access path.
	 * @return The <code>IProcessor</code> instance. Or
	 * <code>null</code> if there is none.
	 */
	public IProcessor<?, ?> getProcessor(final String path);
}
