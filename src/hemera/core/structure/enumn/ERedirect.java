package hemera.core.structure.enumn;

/**
 * <code>ERedirect</code> defines the enumerations of
 * <code>IRESTProcessor</code> redirect behavior.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public enum ERedirect {
	/**
	 * No redirect, directly invoke the paired instance
	 * of <code>IRESTProcessor</code>.
	 */
	Invoke,
	/**
	 * Redirect after invoking the paired instance of
	 * <code>IRESTProcessor</code>. If this value is
	 * used, the handler returned response will not be
	 * returned to the client.
	 */
	RedirectAfterInvoke,
	/**
	 * Redirect before invoking the paired instance of
	 * <code>IRESTProcessor</code>. If this value is
	 * used, the handler is not invoked in this request.
	 */
	RedirectBeforeInvoke;
}
