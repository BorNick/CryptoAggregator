package base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

    /**
     * Sends request to server
     *
     * @param urlStr URL string
     * @param requestMethod HttpRequest method - "GET" - "POST"
     * @return JSON string response
     */
    public static String send(String urlStr, String requestMethod) {
        HttpURLConnection connection = null;
        String line;
        StringBuilder response = new StringBuilder();
        try {
            connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setRequestMethod(requestMethod);
            connection.addRequestProperty("User-Agent", "");
            connection.connect();
            connection.setDefaultUseCaches(false);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                while ((line = in.readLine()) != null) {
                    response.append(line);
                    response.append('\n');
                }
            } else {
                System.out.println("Error: " + connection.getResponseCode());
            }
            in.close();
        } catch (Throwable err) {
            err.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }
}
