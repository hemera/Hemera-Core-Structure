package hemera.core.structure;

import java.util.Map;

import hemera.core.structure.AbstractProcessor;
import hemera.core.structure.interfaces.IRequestType;
import hemera.core.utility.logging.FileLogger;

/**
 * <code>LoggedProcessor</code> defines abstraction
 * of a logic processor unit that also logs its logic
 * procedures when necessary.
 * <p>
 * This abstraction provides exception handling by
 * directly log all thrown exceptions with its
 * <code>FileLogger</code> instance. This allows the
 * sub-classes to directly throw all exceptions
 * without implementing exception logging. However,
 * subclasses should still catch relevant exceptions
 * to provide proper exception handling.
 * <p>
 * In case of an exception occurs during the request
 * processing, this processor still returns a value
 * produced based on the contents of the request and
 * the exception occurred.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public abstract class LoggedProcessor<T extends IRequestType, R> extends AbstractProcessor<T, R> {
	/**
	 * The <code>FileLogger</code> instance.
	 */
	protected final FileLogger logger;
	
	/**
	 * Constructor of <code>LoggedProcessor</code>.
	 */
	protected LoggedProcessor() {
		this(true);
	}
	
	/**
	 * Constructor of <code>LoggedProcessor</code>.
	 * @param active <code>true</code> if processor
	 * should be set to default active right after it
	 * is fully constructed. <code>false</code> if by
	 * default the processor is inactive until external
	 * <code>setActive</code> invocation.
	 */
	protected LoggedProcessor(final boolean active) {
		super(active);
		this.logger = FileLogger.getLogger(this.getClass());
	}
	
	@Override
	protected R doProcess(final Map<String, Object> request) {
		try {
			return this.processRequest(request);
		} catch (final Exception e) {
			this.logger.exception(e);
			return this.exceptionResponse(request, e);
		}
	}
	
	/**
	 * Perform the actual request processing logic with
	 * given request data.
	 * <p>
	 * This method must provide necessary thread-safety
	 * guarantees with high concurrency capabilities to
	 * allow concurrent invocations.
	 * <p>
	 * This method allow exception throwing and parent
	 * <code>LoggedProcessor</code> will log exceptions,
	 * though implementations should still try to catch
	 * important exceptions to provide more detailed
	 * logging in addition to the exception stack-trace.
	 * @param request The <code>Map</code> contents of
	 * a request to be processed.
	 * @throws Exception If any processing logic failed.
	 * <code>LoggedProcessor</code> handles exceptions
	 * directly log them using the logger.
	 */
	protected abstract R processRequest(final Map<String, Object> request) throws Exception;
	
	/**
	 * Build return response value when the specified
	 * exception has occurred with given request data.
	 * @param request The <code>Map</code> contents of
	 * a request to be processed.
	 * @param e The <code>Exception</code> thrown during
	 * the processing of the request.
	 * @return The <code>R</code> response value.
	 */
	protected abstract R exceptionResponse(final Map<String, Object> request, final Exception e);
}
