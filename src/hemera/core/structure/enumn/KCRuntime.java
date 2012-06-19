package hemera.core.structure.enumn;

/**
 * <code>KCRuntime</code> defines all the keys used
 * for runtime environment configuration.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public enum KCRuntime {
	/**
	 * The key for <code>boolean</code> flag indicating
	 * if the runtime should instead use the optional
	 * <code>IScalableService</code>. By default, runtime
	 * environments use <code>IAssistedService</code>.
	 */
	UseScalableService,
	/**
	 * The key for <code>String</code> logging directory
	 * path.
	 */
	LoggingDirectory,
	/**
	 * The key for the optional <code>String</code>
	 * exception handler JAR file location. By default,
	 * runtime environments use the internal file-based
	 * <code>FileExceptionHandler</code>.
	 */
	ExceptionHandlerJarLocation,
	/**
	 * The key for the optional <code>String</code>
	 * exception handler fully qualified class name.
	 */
	ExceptionHandlerClassname,
	/**
	 * The key for the optional <code>String</code>
	 * execution service listener JAR file location. By
	 * default, runtime environments have no listeners.
	 */
	ExecutionListenerJarLocation,
	/**
	 * The key for the optional <code>String</code>
	 * execution service listener fully qualified class
	 * name.
	 */
	ExecutionListenerClassname,
	/**
	 * The key for the <code>int</code> number of assist
	 * executor threads to be used by the assisted
	 * execution service. This number corresponds to
	 * the number of concurrent connections a runtime
	 * environment can handle.
	 * <p>
	 * This value is ignored if scalable service is
	 * used by setting <code>UseScalableService</code>
	 * to <code>true</code>.
	 */
	AssistedServiceExecutorCount,
	/**
	 * The key for the <code>int</code> maximum assist
	 * executor task buffer size. This value acts as
	 * a memory leak protection when the runtime reaches
	 * its maximum processing capacity, at which time,
	 * further requests will block until existing ones
	 * are completed.
	 * <p>
	 * This value is ignored if scalable service is
	 * used by setting <code>UseScalableService</code>
	 * to <code>true</code>.
	 */
	AssistedServiceExecutorMaxBufferSize,
	/**
	 * The key for <code>TimeData</code> eager-idling
	 * waiting time value used by assist executors in
	 * the assisted execution service. This value
	 * determines how much time an executor can stay
	 * idle before attempting to steal tasks from the
	 * other executors. This value should be typically
	 * set at 1/5 of the assist executor count to
	 * provide a reasonably good level of assisting
	 * without causing too much thrashing.
	 * <p>
	 * The format of this value should be [value unit].
	 * <p>
	 * This value is ignored if scalable service is
	 * used by setting <code>UseScalableService</code>
	 * to <code>true</code>.
	 */
	AssistedServiceExecutorIdleTime,
	/**
	 * The key for the <code>int</code> minimum number
	 * of executors the optional scalable service must
	 * maintain at all times. Theoretically, the higher
	 * this value is, the better the performance, but
	 * more memory is used at all times.
	 * <p>
	 * This value is ignored if assisted service is
	 * used by setting <code>UseScalableService</code>
	 * to <code>false</code>, which is the default.
	 */
	ScalableServiceExecutorMinimum,
	/**
	 * The key for the <code>int</code> maximum number
	 * of executors the optional scalable service can
	 * create. This value determines the upper limit
	 * for the total number of executors, which also
	 * determines the maximum number of concurrent
	 * connections the runtime environment can handle.
	 * <p>
	 * This value is ignored if assisted service is
	 * used by setting <code>UseScalableService</code>
	 * to <code>false</code>, which is the default.
	 */
	ScalableServiceExecutorMaximum,
	/**
	 * The key for <code>TimeData</code> timeout value
	 * of executors in the optional scalable service.
	 * This value determines how quickly additionally
	 * created executors should be terminated when the
	 * demand shrinks.
	 * <p>
	 * The format of this value should be [value unit].
	 * <p>
	 * This value is ignored if assisted service is
	 * used by setting <code>UseScalableService</code>
	 * to <code>false</code>, which is the default.
	 */
	ScalableServiceExecutorTimeout,
	/**
	 * The key for the optional <code>String</code>
	 * module auto-deployment file.
	 */
	AutoDeployFile;
}
