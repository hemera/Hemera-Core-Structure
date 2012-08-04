package hemera.core.structure;

import hemera.core.structure.enumn.ERedirect;
import hemera.core.structure.interfaces.IProcessor;
import hemera.core.structure.interfaces.IRequest;
import hemera.core.structure.interfaces.IResponse;
import hemera.core.utility.logging.FileLogger;

/**
 * <code>AbstractProcessor</code> defines abstraction
 * of a processor unit that is responsible for the
 * logic processing of a particular type of requests.
 * This abstraction only provides the commonly shared
 * implementation.
 * <p>
 * <code>AbstractProcessor</code> defines the default
 * activeness value to be <code>true</code>. In other
 * words, the processor instance is by default active.
 * At the same time, the processor defines its default
 * request redirecting behavior to be invoke only.
 * <p>
 * This abstraction provides exception handling by
 * directly log all thrown exceptions with its
 * <code>FileLogger</code> instance. This allows the
 * sub-classes to directly throw all exceptions
 * without implementing exception logging. However,
 * subclasses should still catch relevant exceptions
 * to provide proper exception handling in addition
 * to the provided logging.
 * <p>
 * In case of an exception occurs during the request
 * processing, this processor still returns a value
 * produced based on the contents of the request and
 * the exception occurred.
 * <p>
 * @param RQ The request type that this processor is
 * responsible for processing.
 * @param RS The response type that this processor
 * returns after processing a request.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public abstract class AbstractProcessor<RQ extends IRequest, RS extends IResponse> implements IProcessor<RQ, RS> {
	/**
	 * The <code>FileLogger</code> instance.
	 */
	protected final FileLogger logger;
	/**
	 * The <code>Boolean</code> activeness flag.
	 * <p>
	 * This value guarantees its memory visibility,
	 * since the writing thread may be different from
	 * the reading thread.
	 */
	protected volatile boolean active;
	
	/**
	 * Constructor of <code>AbstractProcessor</code>.
	 */
	protected AbstractProcessor() {
		this(true);
	}
	
	/**
	 * Constructor of <code>AbstractProcessor</code>.
	 * @param active <code>true</code> if processor
	 * should be set to default active right after it
	 * is fully constructed. <code>false</code> if by
	 * default the processor is inactive until external
	 * <code>setActive</code> invocation.
	 */
	protected AbstractProcessor(final boolean active) {
		this.active = active;
		this.logger = FileLogger.getLogger(this.getClass());
	}

	@Override
	public final RS process(final RQ request) {
		if (!this.active) return null;
		try {
			return this.processRequest(request);
		} catch (final Exception e) {
			this.logger.exception(e);
			return this.exceptionResponse(request, e);
		}
	}
	
	/**
	 * Perform the actual request processing logic with
	 * given request.
	 * <p>
	 * This method must provide necessary thread-safety
	 * guarantees with high concurrency capabilities to
	 * allow concurrent invocations.
	 * <p>
	 * This method allow exception throwing and parent
	 * implementation will log the exceptions, although
	 * implementations should still try to catch
	 * important exceptions to a provide more detailed
	 * and relevant handling.
	 * @param request The <code>RQ</code> request to be
	 * processed.
	 * @throws Exception If any processing logic failed.
	 */
	protected abstract RS processRequest(final RQ request) throws Exception;
	
	/**
	 * Build and return a response value when the
	 * specified exception has occurred during the
	 * processing of given request.
	 * @param request The <code>RQ</code> request that
	 * caused the exception.
	 * @param e The <code>Exception</code> thrown
	 * during the processing of the request.
	 * @return The <code>RS</code> response value.
	 */
	protected abstract RS exceptionResponse(final RQ request, final Exception e);

	@Override
	public void setActive(final boolean active) {
		this.active = active;
	}
	
	@Override
	public String getRedirectURI(final RQ request) {
		return null;
	}

	@Override
	public String getRedirectURI(final RQ request, final RS response) {
		return null;
	}

	@Override
	public ERedirect getRedirectBehavior(final RQ request) {
		return ERedirect.Invoke;
	}

	@Override
	public final boolean isActive() {
		return this.active;
	}
}
