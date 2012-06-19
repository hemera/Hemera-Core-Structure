package hemera.core.structure.interfaces;

import hemera.core.structure.enumn.ERedirect;

/**
 * <code>IProcessor</code> defines the interface of a
 * single processing unit that belongs to a particular
 * module unit. The processor encapsulates a set of
 * logic operations that are performed in response to
 * a specific type of requests. The logic contained by
 * a processor unit should be self-contained and can
 * be expressed in a set of instructions or tasks.
 * <p>
 * An <code>IProcessor</code> instance is defined as a
 * REST resource contained within its parent module
 * container resource. The processor should be accessed
 * via HTTP requests at <code>/module/processor</code>.
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
 * invocations may occur on the same instance when
 * concurrent requests are received for this processor.
 * <p>
 * <code>IProcessor</code> instances can be deactivated
 * at runtime to pause request processing. This would
 * cause the operations triggered by the request type
 * to be temporarily disabled for the entire module unit.
 * <p>
 * <code>IProcessor</code> is designed to be used in
 * corporation with a single type of requests. This is
 * defined by the request class type. This request type
 * defines a set of data needed to perform the logic the
 * processor contains. A request passed into a processor
 * is parsed from a HTTP request with its contents
 * validated and parsed into the request implementation
 * instance variables of the corresponding type.
 * <p>
 * <code>IProcessor</code> returns a response after the
 * execution of <code>process</code> method with a
 * received request. This result type is defined by the
 * processor's response class. The response can be
 * converted into JSON format for HTTP return.
 * <p>
 * <code>IProcessor</code> defines request redirecting
 * behavior when a request is received. This allows the
 * processor to redirect the requesting client based on
 * the request data, either before or after the request
 * is processed.
 * <p>
 * @param T The request type that this processor is
 * responsible for processing.
 * @param R The response type that this processor returns
 * after processing a request.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IProcessor<T extends IRequest, R extends IResponse> extends IRESTResource {

	/**
	 * Process the given request and perform logic based
	 * on its contents.
	 * <p>
	 * This method must provide necessary thread-safety
	 * guarantees with high concurrency capabilities to
	 * allow concurrent invocations to process requests
	 * received concurrently.
	 * @param request The <code>T</code> request to be
	 * processed.
	 * @return The <code>R</code> processing result. Or
	 * <code>null</code> if the processor is inactive.
	 */
	public R process(final T request);

	/**
	 * Set the activeness of the processor instance.
	 * <p>
	 * This method guarantees result memory visibility.
	 * As soon as the activeness is set, newly received
	 * requests are ignored.
	 * @param active <code>true</code> if the instance
	 * should be set active. <code>false</code> if the
	 * instance is to be set to inactive.
	 */
	public void setActive(final boolean active);

	/**
	 * Retrieve the request class that this processor
	 * is responsible for processing.
	 * @return The <code>Class</code> of <code>T</code>
	 * the request.
	 */
	public Class<T> getRequestType();
	
	/**
	 * Build the redirect URI based on given request.
	 * This method is only invoked if the redirect
	 * behavior is redirect-before-invoke.
	 * @param request The <code>T</code> request to be
	 * processed.
	 * @return The <code>String</code> redirect URI.
	 */
	public String getRedirectURI(final T request);
	
	/**
	 * Build the redirect URI based on given request
	 * and response. This method is only invoked if the
	 * redirect behavior is redirect-after-invoke.
	 * @param request The <code>T</code> request to be
	 * processed.
	 * @param response The <code>R</code> response.
	 * @return The <code>String</code> redirect URI.
	 */
	public String getRedirectURI(final T request, final R response);

	/**
	 * Determine the redirecting behavior based on the
	 * given request.
	 * @param request The <code>T</code> request to be
	 * processed.
	 * @return The <code>ERedirect</code> enumeration.
	 */
	public ERedirect getRedirectBehavior(final T request);

	/**
	 * Check if this processor unit is active.
	 * <p>
	 * This method reflects the most up-to-date state.
	 * @return <code>true</code> if the instance is
	 * currently active. <code>false</code> otherwise.
	 */
	public boolean isActive();
}
