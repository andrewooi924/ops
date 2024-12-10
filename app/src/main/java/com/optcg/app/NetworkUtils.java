package com.optcg.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    public static String fetchHtml(String urlString) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Create URL and open connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // Timeout in ms
            connection.setReadTimeout(5000);

            // Check for success response
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                reader = new BufferedReader(isr);
                StringBuilder htmlContent = new StringBuilder();
                String line;

                // Read HTML content line by line
                while ((line = reader.readLine()) != null) {
                    htmlContent.append(line);
                }

                return htmlContent.toString();
            } else {
                throw new IOException("HTTP error code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null in case of an error
        } finally {
            // Clean up resources
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
