package data_access;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import entities.Course;
import entities.LectureNotes;
import entities.PDFFile;
import usecases.lecturenotes.NotesGeminiGateway;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

public class GeminiApiDataAccess implements NotesGeminiGateway {

    // Google Gen AI Java SDK client. It reads GEMINI_API_KEY from env by default.
    private final Client client;

    // Model name can be changed later if your team upgrades.
    private final String modelName = "gemini-2.5-flash";

    public GeminiApiDataAccess() {
        // Uses GEMINI_API_KEY environment variable automatically.
        this.client = new Client();
    }

    public GeminiApiDataAccess(String apiKey) {
        // Alternative constructor if your team wants to inject the API key explicitly.
        this.client = Client.builder().apiKey(apiKey).build();
    }

    @Override
    public LectureNotes generateNotes(Course course, String topic) throws Exception {
        String prompt = buildPrompt(course, topic);
        String notesText = callGemini(prompt);

        return new LectureNotes(
                course.getCourseId(),
                topic,
                notesText,
                LocalDateTime.now()
        );
    }

    // Build a text prompt using course ID, topic, and some content from uploaded PDFs.
    private String buildPrompt(Course course, String topic) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an AI study assistant. ")
                .append("Generate clear, concise, and well-structured lecture notes for course ")
                .append(course.getCourseId())
                .append(" on the topic: ").append(topic).append(".\n")
                .append("Use headings, bullet points, key concepts, and short examples.\n\n")
                .append("Here is the course material:\n");

        for (PDFFile pdf : course.getUploadedFiles()) {
            prompt.append("\n=== File: ")
                    .append(pdf.getPath().getFileName())
                    .append(" ===\n");

            try {
                String raw = Files.readString(pdf.getPath());
                if (raw.length() > 4000) {
                    raw = raw.substring(0, 4000);
                }
                prompt.append(raw);
            } catch (IOException e) {
                prompt.append("[Could not read file contents; only filename is provided.]");
            }
        }

        return prompt.toString();
    }

    // Call the Gemini model using the Google Gen AI Java SDK.
    private String callGemini(String prompt) throws IOException {
        GenerateContentResponse response =
                client.models.generateContent(modelName, prompt, null);

        return response.text();
    }
}