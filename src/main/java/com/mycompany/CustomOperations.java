package com.mycompany;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomOperations {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomOperations.class);
	
	@MediaType(value = ANY, strict = false)
	@Alias("Get-Config")
	public String getCall(@Config CustomConfiguration c) {
		LOGGER.info("Sending a get request to database.");
		String response = null;
		String protocol = c.getProtocol().equals("HTTPS") ? "https://" : "http://";
		System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");
		try {
			URL url = new URL(protocol + c.getHost() + c.getBasepath() +"?environment_name=" + c.getEnvironmentName() + "&application_name="+c.getApplicationName());
			LOGGER.info("GET URL String " + url.toString());
			if (c.getProtocol().equals("HTTPS")) {
				LOGGER.info("Processing HTTPS GET request");
				SSLContext sslCtx = SSLContext.getInstance("TLS");
				TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {

					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				} };
				sslCtx.init(null, trustManager, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx.getSocketFactory());
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				con.setDoOutput(true);
				con.setReadTimeout(300000);
				// setting a connection time out of 3 minutes
				con.setConnectTimeout(180000);
				// setting cache usage to false
				con.setUseCaches(false);
				response = getHttpResponse(con);
				LOGGER.info("Response received from HTTPS GET service. ");
			} else {
				LOGGER.info("Processing HTTP GET request");
				URLConnection con = url.openConnection(); // setting a read time out of 5 minutes
				con.setDoOutput(true);
				con.setReadTimeout(300000);
				// setting a connection time out of 3 minutes
				con.setConnectTimeout(180000);
				// setting cache usage to false
				con.setUseCaches(false);
				response = getHttpResponse(con);
				LOGGER.info("Response received from HTTP GET service. ");
			}
		} catch (Exception e) {
			LOGGER.error("Error Occured: Error Details: " + e.getMessage());
			e.printStackTrace();
		}
		return response;
	}
	
	@MediaType(value = ANY, strict = false)
	@Alias("Post-Config")
	public String postCall(@Config CustomConfiguration c, @ParameterGroup(name = "customParams") CustomParameters p) {
		String response = null;
		String protocol = c.getProtocol().equals("HTTPS") ? "https://" : "http://";
		try {
			URL url = new URL(protocol + c.getHost() + c.getBasepath() +"?environment_name=" + c.getEnvironmentName() + "&application_name="+c.getApplicationName()+ "&application_version="+c.getApplicationVersion());
			//String jsonString = "{\"appname\": \"" + c.getApplicationName() + "\", \"env\": \"" + c.getEnvironmentName() + "\"}";
			String jsonString = p.getPostPayload().get("postPayload");
			LOGGER.info("POST URL String " + url.toString());
			if (c.getProtocol().equals("HTTPS")) {
				LOGGER.info("Processing HTTPS POST request ");
				SSLContext sslCtx = SSLContext.getInstance("TLS");
				TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {

					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				} };
				sslCtx.init(null, trustManager, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx.getSocketFactory());
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				con.setDoOutput(true);
				
				con.setRequestProperty("Accept-Charset", "UTF-8");
				con.setRequestProperty("Content-Type", "text/plain; charset, UTF-8");
				
				con.setReadTimeout(300000);
				// setting a connection time out of 3 minutes
				con.setConnectTimeout(180000);
				// setting cache usage to false
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json; utf-8");
				
			    con.setRequestProperty ("content-type", "text/plain");
							
				try (OutputStream os = con.getOutputStream()) {
					byte[] input = jsonString.getBytes("utf-8");
					os.write(input, 0, input.length);
					response = getHttpResponse(con);
				}
				LOGGER.info("Response received from HTTPS POST service.");
			} else {
				LOGGER.info("Processing HTTP POST request");
				HttpURLConnection con = (HttpURLConnection)url.openConnection(); // setting a read time out of 5 minutes
				con.setDoOutput(true);
				
				con.setRequestProperty("Content-Type", "text/plain; charset, UTF-8");
				
				con.setReadTimeout(300000);
				// setting a connection time out of 3 minutes
				con.setConnectTimeout(180000);
				// setting cache usage to false
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "text/plain; utf-8");
				try (OutputStream os = con.getOutputStream()) {
					byte[] input = jsonString.getBytes("utf-8");
					os.write(input, 0, input.length);
					response = getHttpResponse(con);
				}
				LOGGER.info("Response received from HTTP POST service.");
			}
		}catch (Exception e) {
			LOGGER.error("Error Occured: Error Details: " + e.getMessage());
			e.printStackTrace();

		}
		return response;
	}
	
	private String getHttpResponse(URLConnection con) throws UnsupportedEncodingException, IOException{
		String response = null;

		Scanner scanner = new Scanner(con.getInputStream());
	    scanner.useDelimiter("A");
	    response = scanner.next();
	    scanner.close();
	    return response;
	}
}