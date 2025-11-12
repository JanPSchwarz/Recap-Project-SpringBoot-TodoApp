package org.example.recapprojecttodo_appbackend.service;

import org.example.recapprojecttodo_appbackend.models.OpenAIRequest;
import org.example.recapprojecttodo_appbackend.models.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenAiService {
    private final RestClient restClient;

    public OpenAiService(RestClient.Builder restClientBuilder, @Value("${OPEN_AI_API_KEY}") String openAiApiKey) {
        this.restClient = restClientBuilder.baseUrl("https://api.openai.com/v1/responses").defaultHeader("Authorization", "Bearer " + openAiApiKey).build();
    }

    public String checkGrammar(String stringToCheck) {
        if (stringToCheck == null || stringToCheck.isEmpty()) {
            return "";
        }
        String prompt = "Please check the grammar of the following string and return only the corrected sentence: " + stringToCheck;

        OpenAIRequest openAIRequest = new OpenAIRequest(prompt);

        try {
            String outputString = restClient.post()
                    .body(openAIRequest)
                    .retrieve()
                    .body(OpenAIResponse.class).output().getFirst().content().getFirst().text();

            return outputString + " (grammar checked by openAi)";
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return stringToCheck;
        }
    }
}
