package hemera.core.structure;

import hemera.core.structure.enumn.ERedirect;
import hemera.core.structure.interfaces.IProcessor;
import hemera.core.structure.interfaces.IRequest;
import hemera.core.structure.interfaces.IResponse;

/**
 * <code>AbstractProcessor</code> defines abstraction
 * of a processor unit that is responsible for the
 * logic processing of a particular type of requests.
 * This abstraction only provides the commonly shared
 * activeness implementation.
 * <p>
 * <code>AbstractProcessor</code> defines the default
 * activeness value to be <code>true</code>. In other
 * words, the processor instance is by default active.
 * At the same time, the processor defines its default
 * request redirecting behavior to be invoke only.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public abstract class AbstractProcessor<T extends IRequest, R extends IResponse> implements IProcessor<T, R> {
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
	}

	@Override
	public final R process(final T request) {
		if(!this.active) return null;
		return this.doProcess(request);
	}
	
	/**
	 * Perform the actual request processing logic with
	 * given request.
	 * <p>
	 * This method must provide necessary thread-safety
	 * guarantees with high concurrency capabilities to
	 * allow concurrent invocations.
	 * @param request The <code>T</code> request to be
	 * processed.
	 * @return The <code>R</code> processing result.
	 */
	protected abstract R doProcess(final T request);

	@Override
	public void setActive(final boolean active) {
		this.active = active;
	}
	
	@Override
	public String getRedirectURI(final T request) {
		return null;
	}

	@Override
	public String getRedirectURI(final T request, final R response) {
		return null;
	}

	@Override
	public ERedirect getRedirectBehavior(final T request) {
		return ERedirect.Invoke;
	}

	@Override
	public final boolean isActive() {
		return this.active;
	}
}
