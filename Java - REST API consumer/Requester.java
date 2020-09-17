package com.mypackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Requester {

	private String endpoint;
	
	public Requester(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public Response post(String url, JSONObject body) throws IOException {
		return contact(url, "POST", body);
	}

	public Response post(String url, JSONArray body) throws IOException {
		return contact(url, "POST", body);
	}
	
	public Response put(String url, JSONObject body) throws IOException {
		return contact(url, "PUT", body);
	}

	public Response put(String url, JSONArray body) throws IOException {
		return contact(url, "PUT", body);
	}
	
	public Response get(String url) throws IOException {
		return contact(url, "GET", null);
	}
	
	public Response delete(String url) throws IOException {
		return contact(url, "DELETE", null);
	}
	
	private Response contact(String url, String type, Object body) throws IOException {
		
		URL _url = new URL(this.endpoint+url);
		HttpURLConnection conn = (HttpURLConnection) _url.openConnection();

		conn.setRequestMethod(type);
		
		if (body!=null) {
			conn.setRequestProperty("Content-type", "application/json");
			conn.setDoOutput(true);	
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(body.toString()); writer.flush(); writer.close();
		}
		
		String line;
		BufferedReader reader = null;
		boolean nullStream = false; // errorstream will be null if there's no response body
		try {
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (IOException e) {
			nullStream = conn.getErrorStream() == null;
			if (!nullStream) reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}	
		
		StringBuilder sb = new StringBuilder();
		if (!nullStream) {
			while ((line = reader.readLine()) != null) {
			    sb.append(line).append("\n");
			}
			reader.close();
		}

		return new Response(this.endpoint+url, conn.getResponseCode(), type, body, sb.length()==0 ? null : sb.toString());
	}
	
}
