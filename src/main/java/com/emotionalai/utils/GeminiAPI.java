package com.emotionalai.utils;

import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GeminiAPI {

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent";
    private static final String API_KEY = "AIzaSyCax7rNvirI3UgVNBqbeg-a3yJGtY8S-E0";
    private static final Gson gson = new Gson();

    public static String chat(String pythonResponse) {
        try {
            // Parse string: "Text: ... Emotion: ..., Confidence: ..."
            String text = "";
            String emotion = "";
            String confidence = "";
            try {
                String[] parts = pythonResponse.split(" Emotion: ");
                text = parts[0].replace("Text: ", "").trim();
                String[] parts2 = parts[1].split(", Confidence: ");
                emotion = parts2[0].trim();
                confidence = parts2[1].trim();
            } catch (Exception e) {
                return "Không parse được string PythonResponse: " + pythonResponse;
            }

            String prompt = "User vừa nói: \"" + text + "\". Cảm xúc: " + emotion +
                    ". Hãy đóng vai một người bạn đồng hành, đưa lời khuyên phù hợp.";

            return callGemini(prompt);

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi xử lý PythonResponse hoặc gọi Gemini API";
        }
    }

    private static String callGemini(String prompt) {
        try {
            URL url = new URL(API_URL + "?key=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            JsonObject contentPart = new JsonObject();
            contentPart.addProperty("text", prompt);

            JsonObject partWrapper = new JsonObject();
            JsonArray partsArray = new JsonArray();
            partsArray.add(contentPart);
            partWrapper.add("parts", partsArray);

            JsonArray contentsArray = new JsonArray();
            contentsArray.add(partWrapper);

            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", contentsArray);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            InputStream responseStream = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();
            Scanner s = new Scanner(responseStream).useDelimiter("\\A");
            String responseStr = s.hasNext() ? s.next() : "";
            System.out.println("Response code: " + responseCode);

            if (responseCode != 200) {
                System.out.println("Response body: " + responseStr);
                return "Gemini API lỗi: " + responseCode;
            }

            // ✅ Parse chính xác text phản hồi
            JsonObject responseJson = JsonParser.parseString(responseStr).getAsJsonObject();
            JsonArray candidates = responseJson.getAsJsonArray("candidates");
            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject contentObj = firstCandidate.getAsJsonObject("content");
                if (contentObj != null) {
                    JsonArray parts = contentObj.getAsJsonArray("parts");
                    if (parts != null && parts.size() > 0) {
                        JsonObject firstPart = parts.get(0).getAsJsonObject();
                        return firstPart.get("text").getAsString();
                    }
                }
            }

            return "Gemini trả về trống";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi gọi Gemini API";
        }
    }


}
