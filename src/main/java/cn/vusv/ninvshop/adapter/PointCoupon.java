package cn.vusv.ninvshop.adapter;

import cn.vusv.ninvshop.config.McrmbConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class PointCoupon {
    public static Map<String, Object> sendGet(String type, Map<String, String> param) {
        param.put("wname", param.get("wname").replace(" ", "_"));
        Map<String, Object> o1 = Map.of("sign", "", "sid", McrmbConfig.sid);
        Map<String, Object> data = objAssign(new Object[]{o1, param});
        data.put("sign", getSign(data, ""));
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String urlString = "http://api.mcrmb.com/Api/" + type + "?" + params.toString();
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "java request.");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while (true) {
                if ((line = reader.readLine()) == null) break;
                result.append(line);
            }
            reader.close();
            connection.disconnect();
            return parseJSON(result.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSign(Map<String, Object> obj, String str) {
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            str = str.concat(entry.getValue().toString());
        }
        return calculateMD5(str + McrmbConfig.key);
    }

    public static Map<String, Object> objAssign(Object[] arr) {
        Map<String, Object> obj = new HashMap<>();
        for (Object item : arr) {
            if (item instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) item;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    obj.put(entry.getKey().toString(), entry.getValue());
                }
            }
        }
        return obj;
    }

    public static String calculateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> parseJSON(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}