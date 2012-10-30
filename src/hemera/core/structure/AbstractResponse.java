package hemera.core.structure;

import org.json.JSONObject;

import hemera.core.structure.enumn.EHttpStatus;
import hemera.core.structure.interfaces.IResponse;

/**
 * <code>AbstractResponse</code> defines the abstraction
 * of a processing response that contains a response
 * status and a potential error message.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public abstract class AbstractResponse implements IResponse {
	/**
	 * The <code>EHttpStatus</code> value.
	 */
	private final EHttpStatus status;
	/**
	 * The <code>String</code> error message.
	 */
	private final String error;
	
	/**
	 * Constructor of <code>AbstractResponse</code>.
	 * <p>
	 * This constructor creates a success response.
	 */
	protected AbstractResponse() {
		this.status = EHttpStatus.C200_OK;
		this.error = null;
	}
	
	/**
	 * Constructor of <code>AbstractResponse</code>.
	 * @param status The error <code>EHttpStatus</code>.
	 * @param error The <code>String</code> error message.
	 */
	protected AbstractResponse(final EHttpStatus status, final String error) {
		this.status = status;
		if (this.status == EHttpStatus.C200_OK) {
			throw new IllegalArgumentException("This constructor should only be used for error response.");
		}
		this.error = error;
	}

	@Override
	public final EHttpStatus getStatus() {
		return this.status;
	}

	@Override
	public final JSONObject toJSON() throws Exception {
		final JSONObject data = new JSONObject();
		data.put("http_status", this.status.name());
		if (this.status == EHttpStatus.C200_OK) {
			this.insertData(data);
		} else {
			data.put("error", this.error);
		}
		return data;
	}
	
	/**
	 * Insert the necessary data.
	 * <p>
	 * This method is only invoked if the response is
	 * a success response.
	 * @param data The <code>JSONObject</code> to put
	 * data into.
	 * @throws Exception If any processing failed.
	 */
	protected abstract void insertData(final JSONObject data) throws Exception;
}
