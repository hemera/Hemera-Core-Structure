package hemera.core.structure.interfaces;

/**
 * <code>IRequestType</code> defines the interface of
 * a type of requests processed by a processor class.
 * A <code>IRequestType</code> instance must be able
 * to provide an unique <code>String</code> identifier.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IRequestType {

	/**
	 * Retrieve the unique ID of this request type.
	 * @return The <code>String</code> identifier.
	 */
	public String getID();
}
