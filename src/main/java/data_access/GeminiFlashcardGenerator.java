package data_access;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.gson.Gson;
import entities.Flashcard;
import entities.FlashcardSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * GeminiFlashcardGenerator: supports topic string or PDF file.
 */
public class GeminiFlashcardGenerator implements FlashcardGenerator {

    private final String apiKey;
    private final Client client;
    private final GenerateContentConfig generationConfig;
    private final Gson gson;
    private final String flashcardPrompt;

    public GeminiFlashcardGenerator() {
        apiKey = System.getenv("GEMINI_API_KEY");
        client = Client.builder().apiKey(apiKey).build();

        ImmutableMap<String, Object> flashcardSchema = ImmutableMap.of(
                "type", "object",
                "properties", ImmutableMap.of(
                        "questions", ImmutableMap.of("type", "array", "items", ImmutableMap.of("type", "string")),
                        "answers", ImmutableMap.of("type", "array", "items", ImmutableMap.of("type", "string"))
                ),
                "required", ImmutableList.of("questions", "answers")
        );

        generationConfig = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .candidateCount(1)
                .responseJsonSchema(flashcardSchema)
                .build();

        flashcardPrompt = """
                You are an AI tutor. Based on the provided content, generate 5 short flashcards.
                Return JSON:
                {
                    "questions": [],
                    "answers": []
                }
                """;

        gson = new Gson();
    }

    @Override
    public FlashcardSet generateForCourse(String courseName, String contentInput) throws IOException {

        // DEBUG: Print input parameters
        System.out.println("=== GeminiFlashcardGenerator.generateForCourse ===");
        System.out.println("API Key exists: " + (apiKey != null && !apiKey.isEmpty()));
        System.out.println("Course name: " + courseName);
        System.out.println("Content input: [" + contentInput + "]");

        List<Part> partsList = new ArrayList<>();
        partsList.add(Part.fromText(flashcardPrompt));

        // Check whether contentInput is a PDF filename
        File maybePdf = new File(contentInput);

        // DEBUG: Print file info
        System.out.println("File exists: " + maybePdf.exists());
        System.out.println("Absolute path: " + maybePdf.getAbsolutePath());

        if (maybePdf.exists() && contentInput.toLowerCase().endsWith(".pdf")) {
            System.out.println("Reading PDF file...");
            byte[] pdfBytes = Files.readAllBytes(maybePdf.toPath());
            System.out.println("PDF size: " + pdfBytes.length + " bytes");

            partsList.add(
                    Part.fromBytes(pdfBytes, "application/pdf")
            );

        } else {
            System.out.println("Using text content instead of PDF");
            partsList.add(Part.fromText(contentInput));
        }

        System.out.println("Calling Gemini API...");
        Content request = Content.fromParts(partsList.toArray(new Part[0]));

        GenerateContentResponse response =
                client.models.generateContent("gemini-2.5-flash", request, generationConfig);

        // DEBUG: Print response
        System.out.println("Response received: " + response.text());

        FlashcardResponse parsed = gson.fromJson(response.text(), FlashcardResponse.class);
        if (parsed == null || parsed.questions == null || parsed.answers == null) {
            throw new IOException("Invalid response from Gemini.");
        }

        List<Flashcard> flashcards = new ArrayList<>();
        int size = Math.min(parsed.questions.size(), parsed.answers.size());
        for (int i = 0; i < size; i++) {
            flashcards.add(new Flashcard(parsed.questions.get(i), parsed.answers.get(i)));
        }

        System.out.println("Successfully generated " + flashcards.size() + " flashcards!");
        return new FlashcardSet(courseName, flashcards);
    }

    private static class FlashcardResponse {
        List<String> questions;
        List<String> answers;
    }
}