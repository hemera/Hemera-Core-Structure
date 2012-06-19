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
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IRequest {

	/**
	 * Parse and validate the given HTTP arguments to
	 * finalize the request instance.
	 * @param arguments The <code>Map</code> contents of
	 * a HTTP request to be processed. The values in the
	 * map are either of type <code>String</code>, or of
	 * type <code>byte</code> array.
	 */
	public void parse(final Map<String, Object> arguments);
}
