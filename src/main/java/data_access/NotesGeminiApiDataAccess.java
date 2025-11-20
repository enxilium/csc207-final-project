package data_access;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.gson.Gson;
import entities.Course;
import entities.LectureNotes;
import entities.PDFFile;
import usecases.lecturenotes.NotesGenerationException;
import usecases.lecturenotes.NotesGeminiGateway;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotesGeminiApiDataAccess implements NotesGeminiGateway {

    private final Client client;
    private final String model = "gemini-2.5-flash";
    private final GenerateContentConfig notesConfig;
    private final Gson gson = new Gson();

    public NotesGeminiApiDataAccess() {
        client = Client.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build();

        // Simplified config: only enforce JSON mime type, no nested schema
        notesConfig = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .candidateCount(1)
                .build();
    }

    @Override
    public LectureNotes generateNotes(Course course, String topic)
            throws NotesGenerationException {
        try {
            System.out.println("Generating notes for courseId=" + course.getCourseId()
                    + ", topic=" + topic);

            List<Part> parts = new ArrayList<>();
            parts.add(Part.fromText(buildPrompt(course, topic)));

            List<PDFFile> files = course.getUploadedFiles();
            if (files == null) {
                System.out.println("No uploaded files for this course (uploadedFiles is null).");
                files = new ArrayList<>();
            } else {
                System.out.println("Number of uploaded files: " + files.size());
            }

            for (PDFFile f : files) {
                System.out.println("Attaching PDF: " + f.getPath());
                byte[] bytes = Files.readAllBytes(f.getPath());
                parts.add(Part.fromBytes(bytes, "application/pdf"));
            }

            Content content = Content.fromParts(parts.toArray(new Part[0]));
            GenerateContentResponse resp =
                    client.models.generateContent(model, content, notesConfig);

            String json = resp.text();
            System.out.println("Gemini lecture notes response: " + json);

            StructuredNotes structured = gson.fromJson(json, StructuredNotes.class);
            String formattedNotes = formatNotes(structured);

            return new LectureNotes(course.getCourseId(), topic, formattedNotes, LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotesGenerationException("Failed to generate lecture notes.", e);
        }
    }

    private String buildPrompt(Course course, String topic) {
        return """
                You are an assistant that generates structured, exam-focused lecture notes for university students.

                Use ONLY the attached PDF files as your source.
                Your goal is to:
                - Extract key ideas, definitions, formulas, and examples.
                - Organize them into clear units/chapters and sections.
                - Make it easy for students to review and study for tests.

                You MUST return a single valid JSON object with the following fields:

                {
                  "courseId": "<string>",
                  "topic": "<string>",
                  "units": [
                    {
                      "unitTitle": "<short title for this unit>",
                      "unitSummary": "<3–5 sentence overview of this unit>",
                      "learningObjectives": [
                        "<what a student should be able to do after this unit>"
                      ],
                      "sections": [
                        {
                          "sectionTitle": "<subtopic name>",
                          "bulletPoints": [
                            "<concise bullet point explaining a concept>",
                            "<another bullet point>",
                            "<include formulas or conditions when helpful>"
                          ]
                        }
                      ],
                      "reviewQuestions": [
                        "<short concept-check question>",
                        "<another question>"
                      ]
                    }
                  ],
                  "globalSummary": "<5–7 sentence high-level summary of the entire topic>",
                  "examTips": [
                    "<short tip about what is often tested or commonly confused>",
                    "<another exam-focused tip>"
                  ]
                }

                Rules:
                - Use concise, student-friendly language.
                - Make bullet points short and focused.
                - Base everything only on the attached PDFs.
                - Output ONLY the JSON object, with no extra text, markdown, or explanations.

                Here is the context for this request.
                Set the JSON fields courseId and topic to exactly these values:

                courseId: %s
                topic: %s
                """.formatted(course.getCourseId(), topic);
    }

    private String formatNotes(StructuredNotes notes) {
        if (notes == null) {
            return "No notes generated.";
        }

        StringBuilder sb = new StringBuilder();

        if (notes.topic != null) {
            sb.append("# ").append(notes.topic).append("\n\n");
        }

        if (notes.globalSummary != null && !notes.globalSummary.isEmpty()) {
            sb.append("Summary\n");
            sb.append(notes.globalSummary).append("\n\n");
        }

        if (notes.units != null && !notes.units.isEmpty()) {
            int unitIndex = 1;
            for (Unit u : notes.units) {
                sb.append("Unit ").append(unitIndex++).append(": ")
                        .append(defaultString(u.unitTitle)).append("\n");

                if (u.unitSummary != null && !u.unitSummary.isEmpty()) {
                    sb.append("  Overview: ").append(u.unitSummary).append("\n");
                }

                if (u.learningObjectives != null && !u.learningObjectives.isEmpty()) {
                    sb.append("  Learning objectives:\n");
                    for (String lo : u.learningObjectives) {
                        sb.append("    - ").append(lo).append("\n");
                    }
                }

                if (u.sections != null && !u.sections.isEmpty()) {
                    for (Section s : u.sections) {
                        sb.append("  ").append(defaultString(s.sectionTitle)).append(":\n");
                        if (s.bulletPoints != null) {
                            for (String bp : s.bulletPoints) {
                                sb.append("    • ").append(bp).append("\n");
                            }
                        }
                    }
                }

                if (u.reviewQuestions != null && !u.reviewQuestions.isEmpty()) {
                    sb.append("  Review questions:\n");
                    for (String q : u.reviewQuestions) {
                        sb.append("    ? ").append(q).append("\n");
                    }
                }

                sb.append("\n");
            }
        }

        if (notes.examTips != null && !notes.examTips.isEmpty()) {
            sb.append("Exam tips:\n");
            for (String tip : notes.examTips) {
                sb.append("  - ").append(tip).append("\n");
            }
        }

        return sb.toString().trim();
    }

    private String defaultString(String s) {
        return s == null ? "" : s;
    }

    // Internal classes to match the JSON structure from Gemini.
    private static class StructuredNotes {
        String courseId;
        String topic;
        List<Unit> units;
        String globalSummary;
        List<String> examTips;
    }

    private static class Unit {
        String unitTitle;
        String unitSummary;
        List<String> learningObjectives;
        List<Section> sections;
        List<String> reviewQuestions;
    }

    private static class Section {
        String sectionTitle;
        List<String> bulletPoints;
    }
}
