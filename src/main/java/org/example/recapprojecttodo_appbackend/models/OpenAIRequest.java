package org.example.recapprojecttodo_appbackend.models;

/*
    {
        "model": "gpt-4.1",
        "input": "Tell me a three sentence bedtime story about a unicorn."
    }
*/
public record OpenAIRequest(String model, String input) {

    public OpenAIRequest(String input) {
        this("gpt-4.1", input);
    }
}
