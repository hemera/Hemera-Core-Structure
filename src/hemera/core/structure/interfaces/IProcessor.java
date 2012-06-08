package hemera.core.structure.interfaces;

import java.util.Map;

/**
 * <code>IProcessor</code> defines the interface of a
 * single processing unit that belongs to a particular
 * module unit. The processor encapsulates a set of
 * logic operations that are performed in response to
 * a specific type of requests. The logic contained by
 * a processor unit should be self-contained and can
 * be expressed in a set of instructions or tasks.
 * <p>
 * <code>IProcessor</code> instances are constructed
 * by a particular module unit for a specific type of
 * requests. Its <code>process</code> method is invoked
 * when a request is received to perform its logic
 * operations based on the contents of the request. It
 * can be considered as a controller unit that alters
 * the state of its module unit.
 * <p>
 * Processor implementations must provide the necessary
 * thread-safety guarantees, preferably with a high
 * level of concurrency capabilities, since concurrent
 * invocations may occur on the same instance.
 * <p>
 * <code>IProcessor</code> instances can be deactivated
 * at runtime to pause request processing. This would
 * cause the operations triggered by the request type
 * to be temporarily disabled for the entire module unit.
 * <p>
 * <code>IProcessor</code> is designed to be used in
 * corporation with a single type of requests. This is
 * defined by the generic type casting. This request
 * type defines a set of data needed to perform the
 * logic the processor contains. A request passed into
 * a processor is expressed as a <code>Map</code> of
 * <code>String</code> key to <code>Object</code> values.
 * <p>
 * <code>IProcessor</code> returns a result value after
 * the execution of <code>process</code> method with
 * a received request. This result type is defined by
 * the processor class generic type casting.
 * <p>
 * Typically, processor instances are executed within
 * a <code>IResultTask</code> by the execution service.
 * This implies that the processor <code>process</code>
 * logic cannot contain long-blocking operations. In
 * order to still perform such operations when needed,
 * implementations should submit separate background
 * tasks within the <code>process</code> method to the
 * execution service for execution.
 * <p>
 * @param T The request type that this processor is
 * responsible for processing.
 * @param R The processing return value type that this
 * processor produces.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IProcessor<T extends IRequestType, R> {

	/**
	 * Process the given request and perform logic based
	 * on the specified contents.
	 * <p>
	 * Typically, this method is invoked within a fore-
	 * ground result task by the execution service. This
	 * implies that the contained logic cannot contain
	 * any long-blocking operations. In order to still
	 * perform such operations when needed, background
	 * tasks can be submitted to the service within the
	 * process logic.
	 * <p>
	 * This method must provide necessary thread-safety
	 * guarantees with high concurrency capabilities to
	 * allow concurrent invocations.
	 * @param request The <code>Map</code> contents of
	 * a request to be processed.
	 * @return The <code>R</code> processing result. Or
	 * <code>null</code> if the processor is inactive.
	 */
	public R process(final Map<String, Object> request);

	/**
	 * Set the activeness of the processor instance.
	 * <p>
	 * Typically this method should be invoked on all
	 * processor instances that are registered to the
	 * same receiving protocol.
	 * <p>
	 * This method guarantees result memory visibility.
	 * As soon as the activeness is set, newly received
	 * message instances are ignored.
	 * @param active <code>true</code> if the instance
	 * should be set active. <code>false</code> if the
	 * instance is to be set to inactive.
	 */
	public void setActive(final boolean active);

	/**
	 * Retrieve the request type that this processor is
	 * responsible for processing.
	 * @return The <code>T</code> of the request type.
	 */
	public T getRequestType();

	/**
	 * Check if this processor unit is active.
	 * <p>
	 * This method reflects the most up-to-date state.
	 * @return <code>true</code> if the instance is
	 * currently active. <code>false</code> otherwise.
	 */
	public boolean isActive();
}
