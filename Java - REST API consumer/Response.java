package com.mypackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Response {

	private String url;
	private int responseCode;
	private String requestType;
	private String contentType;
	private JSONObject requestObject;
	private JSONArray requestArray;
	private String requestText;
	private JSONObject responseObject;
	private JSONArray responseArray;
	private String responseText;
	
	public Response(String url, int code, String type, Object requestBody, String responseBody) {
		this.url = url;
		this.responseCode = code;
		this.requestType = type;
		parseRequest(requestBody);
		parseResponse(responseBody);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Request Type  :: ").append(this.requestType).append("\n");
		sb.append("Destination   :: ").append(this.url).append("\n");
		sb.append("Request Body  :: ").append(this.stringifyBody()).append("\n");
		sb.append("Response Type :: ").append(this.contentType).append("\n");
		sb.append("Status Code   :: ").append(this.responseCode).append("\n");
		sb.append("Response      :: ").append(this.stringifyResponse()).append("\n");
		return sb.toString();
	}

	private void parseRequest(Object req) {
		if (req==null) return;

		try { requestObject = new JSONObject(req.toString()); }
		catch (JSONException e) { try { requestArray = new JSONArray(req.toString()); }
			catch (JSONException x) { requestText = req.toString(); }
		}
	}

	private void parseResponse(String json) {
		if (json==null) return;

		try { responseObject = new JSONObject(json); contentType = "JSONObject"; }
		catch (JSONException e) { try { responseArray = new JSONArray(json); contentType = "JSONArray"; }
			catch (JSONException x) { responseText = json; contentType = "String"; }
		}
	}

	private String stringifyBody() {
		if (requestObject!=null) return requestObject.toString();
		if (requestArray!=null) return requestArray.toString();
		if (requestText!=null) return requestText;
		return "";
	}

	private String stringifyResponse() {
		if (responseObject!=null) return responseObject.toString();
		if (responseArray!=null) return responseArray.toString();
		if (responseText!=null) return responseText;
		return "";
	}
	
	public String getURL() {
		return url;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getResponseType() {
		return contentType;
	}

	/**
	 *
	 * @param <Any> Return type is defined by the method's caller.
	 * @return {@link JSONObject}, {@link JSONArray} or {@link String}.
	 */
	public <Any> Any getRequestBody() {
		if (requestObject!=null) return (Any) requestObject;
		if (requestArray!=null) return (Any) requestArray;
		return (Any) requestText;
	}

	/**
	 *
	 * @param <Any> Return type is defined by the method's caller.
	 * @return {@link JSONObject}, {@link JSONArray} or {@link String}.
	 */
	public <Any> Any getResponse() {
		if (responseObject!=null) return (Any) responseObject;
		if (responseArray!=null) return (Any) responseArray;
		return (Any) responseText;
	}
	
}
