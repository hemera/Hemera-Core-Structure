package hemera.core.structure.interfaces;

import org.json.JSONObject;

/**
 * <code>IResponseData</code> defines the interface of
 * a data entry that can be returned via a response.
 * Typically database objects should implement this
 * to allow the corresponding response to return the
 * data in JSON format.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public interface IResponseData {

	/**
	 * Convert the data into JSON format for response.
	 * @return The <code>JSONObject</code> data.
	 * @throws Exception If any processing failed.
	 */
	public JSONObject toJSON() throws Exception;
}
