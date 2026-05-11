package service;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AIService {

    private static final String API_URL = "http://localhost:11434/api/generate";

    public static String askAI(String prompt, String language) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 💡 On construit une instruction système pour la langue
            String fullPrompt = "Tu es un conseiller financier expert.\n" +
                    "- Donne des conseils clairs\n" +
                    "- Utilise un langage simple\n" +
                    "- Donne des actions concrètes\n" +
                    "- Réponds uniquement en " + language + "\n\n" +
                    prompt;

            // ✅ Construction JSON robuste
            JSONObject json = new JSONObject();
            json.put("model", "gemma3:1b");
            json.put("prompt", fullPrompt);
            json.put("stream", false);

            // Envoi en UTF-8 pour supporter les accents et l'Arabe
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Lecture de la réponse
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            // ✅ Extraction propre du texte
            JSONObject result = new JSONObject(response.toString());
            return result.getString("response");

        } catch (Exception e) {
            return "❌ Erreur de communication avec l'IA : " + e.getMessage();
        }
    }
}