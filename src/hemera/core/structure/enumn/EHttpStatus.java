package hemera.core.structure.enumn;

/**
 * <code>EHttpStatus</code> defines the enumerations
 * of all the HTTP status that can be returned from a
 * response.
 *
 * @author Yi Wang (Neakor)
 * @version 1.0.0
 */
public enum EHttpStatus {
	/**
	 * The continue 100 status.
	 */
	C100_Continue(100),
	/**
	 * The switching protocols 101 status.
	 */
	C101_SwitchingProtocols(101),
	/**
	 * The OK 200 status.
	 */
	C200_OK(200),
	/**
	 * The created 201 status.
	 */
	C201_Created(201),
	/**
	 * The accepted 202 status.
	 */
	C202_Accepted(202),
	/**
	 * The non-authoritative information 203 status. 
	 */
	C203_NonAuthoritativeInformation(203),
	/**
	 * The no content 204 status.
	 */
	C204_NoContent(204),
	/**
	 * The reset content 205 status.
	 */
	C205_ResetContent(205),
	/**
	 * The partial content 206 status.
	 */
	C206_PartialContent(206),
	/**
	 * The multiple choices 300 status.
	 */
	C300_MultipleChoices(300),
	/**
	 * The moved permanently 301 status.
	 */
	C301_MovedPermanently(301),
	/**
	 * The found 302 status.
	 */
	C302_Found(302),
	/**
	 * The see other 303 status.
	 */
	C303_SeeOther(303),
	/**
	 * The not modified 304 status.
	 */
	C304_NotModified(304),
	/**
	 * The use proxy 305 status.
	 */
	C305_UseProxy(305),
	/**
	 * The temporary redirect 307 status.
	 */
	C307_TemporaryRedirect(307),
	/**
	 * The bad request 400 status.
	 */
	C400_BadRequest(400),
	/**
	 * The unauthorized 401 status.
	 */
	C401_Unauthorized(401),
	/**
	 * The payment required 402 status.
	 */
	C402_PaymentRequired(402),
	/**
	 * The forbidden 403 status.
	 */
	C403_Forbidden(403),
	/**
	 * The not found 404 status.
	 */
	C404_NotFound(404),
	/**
	 * The method not allowed 405 status.
	 */
	C405_MethodNotAllowed(405),
	/**
	 * The not acceptable 406 status.
	 */
	C406_NotAcceptable(406),
	/**
	 * The proxy authentication required 407 status.
	 */
	C407_ProxyAuthenticationRequired(407),
	/**
	 * The request timeout 408 status.
	 */
	C408_RequestTimeout(408),
	/**
	 * The conflict 409 status.
	 */
	C409_Conflict(409),
	/**
	 * The gone 410 status.
	 */
	C410_Gone(410),
	/**
	 * The length required 411 status.
	 */
	C411_LengthRequired(411),
	/**
	 * The precondition failed 412 status.
	 */
	C412_PreconditionFailed(412),
	/**
	 * The request entity too large 413 status.
	 */
	C413_RequestEntityTooLarge(413),
	/**
	 * The request URI too long 414 status.
	 */
	C414_RequestURITooLong(414),
	/**
	 * The unsupported media type 415 status.
	 */
	C415_UnsupportedMediaType(415),
	/**
	 * The requested range not satisfiable 416 status.
	 */
	C416_RequestedRangeNotSatisfiable(416),
	/**
	 * The expectation failed 417 status.
	 */
	C417_ExpectationFailed(417),
	/**
	 * The internal server error 500 status.
	 */
	C500_InternalServerError(500),
	/**
	 * The not implemented 501 status.
	 */
	C501_NotImplemented(501),
	/**
	 * The bad gateway 502 status.
	 */
	C502_BadGateway(502),
	/**
	 * The service unavailable 503 status.
	 */
	C503_ServiceUnavailable(503),
	/**
	 * The gateway timeout 504 status.
	 */
	C504_GatewayTimeout(504),
	/**
	 * The HTTP version not supported 505 status.
	 */
	C505_HTTPVersionNotSupported(505);
	
	/**
	 * The <code>int</code> status code.
	 */
	public final int code;
	
	/**
	 * Constructor of <code>EHttpStatus</code>.
	 * @param code The <code>int</code> code.
	 */
	private EHttpStatus(final int code) {
		this.code = code;
	}
}
