package hemera.core.structure;

import java.util.Map;

import hemera.core.structure.interfaces.IProcessor;
import hemera.core.structure.interfaces.IRequestType;

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
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public abstract class AbstractProcessor<T extends IRequestType, R> implements IProcessor<T, R> {
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
	public R process(final Map<String, Object> request) {
		if(!this.active) return null;
		return this.doProcess(request);
	}
	
	/**
	 * Perform the actual message processing logic with
	 * given request data.
	 * <p>
	 * This method must provide necessary thread-safety
	 * guarantees with high concurrency capabilities to
	 * allow concurrent invocations.
	 * @param request The <code>Map</code> contents of
	 * a request to be processed.
	 * @return The <code>R</code> processing result.
	 */
	protected abstract R doProcess(final Map<String, Object> request);

	@Override
	public void setActive(final boolean active) {
		this.active = active;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}
}
