package com.semanticsquare.thrillio.util;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpConnect {
	public static String download(String sourceUrl) throws MalformedURLException, URISyntaxException {
		String out = null;
		System.out.println("Downloading: " + sourceUrl);
		URL url = new URI(sourceUrl).toURL();

		try {
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			int responseCode = con.getResponseCode();
			if (responseCode >= 200 && responseCode <= 300) {
				out = IOUtil.read(con.getInputStream());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}
}
