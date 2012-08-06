package hemera.core.structure.interfaces;

import java.util.Map;

/**
 * <code>IRequest</code> defines the interface of a
 * type of requests that a processor implementation
 * is responsible for processing.
 * <p>
 * <code>IRequest</code> instances are instantiated
 * via the <code>Class</code> <code>newInstance</code>
 * method by retrieving the class object from the
 * responsible processor instance. When a new HTTP
 * request is received to access the processor, a new
 * instance of the request is instantiated, then the
 * HTTP arguments are given to the request instance
 * for parsing and validating. The finalized request
 * instance is passed to the processor for processing.
 * <p>
 * Parsing arguments may throw any type of exceptions
 * if an error occurs or an invalid value is received.
 * This is a validation process that protects the
 * server logic from processing invalid values.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IRequest {

	/**
	 * Parse and validate the given HTTP arguments to
	 * finalize the request instance.
	 * @param id The <code>long</code> resource ID that
	 * is being requested. <code>Long.MIN_VALUE</code>
	 * if no ID is specified.
	 * @param action The <code>String</code> requesting
	 * action. <code>null</code> if no action is specified.
	 * @param arguments The <code>Map</code> contents of
	 * a HTTP request to be processed. The values in the
	 * map are either of type <code>String</code>, or of
	 * type <code>byte</code> array.
	 * @throws Exception If an error occurred or an
	 * invalid value is received, thus rejected by the
	 * request.
	 */
	public void parse(final long id, final String action, final Map<String, Object> arguments) throws Exception;
}
