package hemera.core.structure.enumn;

/**
 * <code>EHttpMethod</code> defines the enumerations
 * of all types of Http methods.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public enum EHttpMethod {
	/**
	 * The options method.
	 */
	Options("OPTIONS"),
	/**
	 * The get method.
	 */
	Get("GET"),
	/**
	 * The head method.
	 */
	Head("HEAD"),
	/**
	 * The post method.
	 */
	Post("POST"),
	/**
	 * The put method.
	 */
	Put("PUT"),
	/**
	 * The delete method.
	 */
	Delete("DELETE"),
	/**
	 * The trace method.
	 */
	Trace("TRACE"),
	/**
	 * The connect method.
	 */
	Connect("CONNECT");
	
	/**
	 * The <code>String</code> value.
	 */
	public final String value;
	
	/**
	 * Constructor of <code>EHttpMethod</code>.
	 * @param value The <code>String</code> value.
	 */
	private EHttpMethod(final String value) {
		this.value = value;
	}
	
	/**
	 * Parse the given value into an enumeration.
	 * @param value The <code>String</code> value to
	 * parse.
	 * @return The <code>EHttpMethod</code> value.
	 */
	public static EHttpMethod parse(final String value) {
		if (value.equalsIgnoreCase(EHttpMethod.Options.value)) {
			return EHttpMethod.Options;
		} else if (value.equalsIgnoreCase(EHttpMethod.Get.value)) {
			return EHttpMethod.Get;
		} else if (value.equalsIgnoreCase(EHttpMethod.Head.value)) {
			return EHttpMethod.Head;
		} else if (value.equalsIgnoreCase(EHttpMethod.Post.value)) {
			return EHttpMethod.Post;
		} else if (value.equalsIgnoreCase(EHttpMethod.Put.value)) {
			return EHttpMethod.Put;
		} else if (value.equalsIgnoreCase(EHttpMethod.Delete.value)) {
			return EHttpMethod.Delete;
		} else if (value.equalsIgnoreCase(EHttpMethod.Trace.value)) {
			return EHttpMethod.Trace;
		} else if (value.equalsIgnoreCase(EHttpMethod.Connect.value)) {
			return EHttpMethod.Connect;
		} else {
			throw new IllegalArgumentException("Unsupported Http method: " + value);
		}
	}
}
