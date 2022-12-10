package cum.jesus.jesusclient.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Objects;

public class WebUtils {
    public static void download(String link, String location) throws IOException {
        File file = new File(location);
        if (!file.exists()) Files.copy(new URL(link).openStream(), file.toPath());
    }

    public static JsonElement getJson(String jsonUrl) {
        return (new JsonParser()).parse(Objects.requireNonNull(getInputStream(jsonUrl)));
    }

    public static String getString(String url) throws IOException {
        InputStreamReader input = getInputStream(url);
        BufferedReader websiteText = new BufferedReader(input);
        StringBuilder sb = new StringBuilder();

        String inputLine;
        while ((inputLine = websiteText.readLine()) != null)
            sb.append(inputLine);

        return sb.toString();
    }

    public static InputStreamReader getInputStream(String url) {
        try {
            URLConnection conn = (new URL(url)).openConnection();
            conn.setRequestProperty("UserAgent", "Mozilla/5.0");
            return new InputStreamReader(conn.getInputStream());
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
