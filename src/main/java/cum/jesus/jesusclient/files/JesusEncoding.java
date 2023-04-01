package cum.jesus.jesusclient.files;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.JesusClientNatives;
import cum.jesus.jesusclient.utils.HttpUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JesusEncoding {
    private static HashMap<Character, String> translationMap = new HashMap<>();

    static void load() throws IOException {
        List<String> tmp = new ArrayList<>();

        String res;
        try {
            res = HttpUtils.get(JesusClient.backendUrl + "/api/v2/encodingmap");
            if (res == null) throw new NullPointerException("res is null");

            FileUtils.writeStringToFile(JesusClient.INSTANCE.fileManager.map, Base64.getEncoder().encodeToString(res.getBytes()));
        } catch (Exception e) {
            res = new String(Base64.getDecoder().decode(FileUtils.readFileToString(JesusClient.INSTANCE.fileManager.map)));
        }

        try (BufferedReader br = new BufferedReader(new StringReader(res))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                //contentBuilder.append(sCurrentLine).append("\n");
                tmp.add(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s : tmp) {
            String[] alsoTmp = s.split(" : ");
            translationMap.put(alsoTmp[0].toCharArray()[0], alsoTmp[1]);
        }
    }

    /**
     * @param s The String to encode in JesusEncoding
     * @return The encoded String
    */
    public static String toString(String s) {
        StringBuilder sb = new StringBuilder();

        for (char c : s.toCharArray()) {
            sb.append(translationMap.get(c)).append(" ");
        }

        return sb.toString();
    }

    /**
     * @param s The String to decode in JesusEncoding
     * @return The decoded String
    */
    public static String fromString(String s) {
        String[] map = s.split(" ");

        StringBuilder sb = new StringBuilder();
        for (String str : map) {
            translationMap.forEach((key, val) -> {
                if (val.equals(str))
                    sb.append(key);
            });
        }

        return sb.toString();
    }
}
