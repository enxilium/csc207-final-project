package data_access;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.gson.Gson;
import entities.EvaluationData;
import entities.Flashcard;
import entities.FlashcardSet;
import entities.PDFFile;
import entities.TestData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import usecases.evaluate_test.EvaluateTestDataAccessInterface;
import usecases.mock_test_generation.MockTestGenerationTestDataAccessInterface;

/**
 * Data access implementation using Google's Gemini API.
 * Handles test generation, evaluation, and flashcard generation.
 */
public class GeminiApiDataAccess implements
    MockTestGenerationTestDataAccessInterface,
    EvaluateTestDataAccessInterface,
    FlashcardGenerator {

  private final String apiKey;
  private final Client client;
  private final GenerateContentConfig generationConfig;
  private final GenerateContentConfig evaluationConfig;
  private final GenerateContentConfig flashcardConfig;
  private final String generationPrompt;
  private final String evaluationPrompt;
  private final String flashcardPrompt;
  private final Gson gson;

  /**
   * Constructs a GeminiApiDataAccess instance and initializes
   * API client and configuration.
   */
  public GeminiApiDataAccess() {
    apiKey = System.getenv("GEMINI_API_KEY");
    client = Client.builder().apiKey(apiKey).build();
    gson = new Gson();

    // Test Generation Config
    ImmutableMap<String, Object> arrayOfStringSchema = ImmutableMap.of(
        "type", "array",
        "items", ImmutableMap.of("type", "string")
    );

    ImmutableMap<String, Object> mockTestSchema = ImmutableMap.of(
        "type", "object",
        "properties", ImmutableMap.of(
            "questions", arrayOfStringSchema,
            "answers", arrayOfStringSchema,
            "questionTypes", arrayOfStringSchema
        ),
        "required", ImmutableList.of("questions", "answers", "questionTypes")
    );

    generationConfig = GenerateContentConfig.builder()
        .responseMimeType("application/json")
        .candidateCount(1)
        .responseJsonSchema(mockTestSchema)
        .build();

    generationPrompt = """
        You are an expert AI Tutor and study assistant. Your primary task
        is to generate a high-quality mock test based on the content of
        the provided PDF document(s).

        The test must be structured as a valid JSON object following the
        required schema.

        Follow these instructions carefully:

        1.  **Analyze the Content:** Thoroughly analyze the key concepts,
            definitions, important facts, and main topics discussed in
            the provided document(s).

        2.  **Generate Diverse Questions:** Create a mix of question types.
            For the `questionTypes` array, use one of the following strings
            for each question: "Multiple Choice", "True/False",
            "Short Answer", or "Essay".

        3.  **Format Questions and Answers:**
            *   For "Multiple Choice" questions, format the question text
                to include the options (e.g., "What is the capital of
                France?\\nA. London\\nB. Berlin\\nC. Paris\\nD. Rome").
                The corresponding entry in the `answers` array should be
                the single correct letter (e.g., "C").
            *   For "True/False" questions, the answer should be the string
                "True" or "False".
            *   For "Short Answer" or "Essay" questions, the answer should
                be left blank as an empty string.

        4.  **Maintain Array Integrity:** It is critical that the three
            arrays (`questions`, `answers`, `questionTypes`) are of the same
            length. The item at index `i` in the `questions` array must
            correspond directly to the item at index `i` in the `answers`
            and `questionTypes` arrays.

        5.  **Focus on Relevance:** The questions must be directly
            answerable from the provided material. Do not include
            information from outside sources.

        Please now generate the mock test based on the attached
        document(s).
        """;

    // Evaluation Config
    ImmutableMap<String, Object> correctnessSchema = ImmutableMap.of(
        "type", "array",
        "items", ImmutableMap.of(
            "type", "string",
            "enum", ImmutableList.of("0", "0.5", "1")
        )
    );

    ImmutableMap<String, Object> evaluationSchema = ImmutableMap.of(
        "type", "object",
        "properties", ImmutableMap.of(
            "questions", arrayOfStringSchema,
            "answers", arrayOfStringSchema,
            "userAnswers", arrayOfStringSchema,
            "correctness", correctnessSchema,
            "feedback", arrayOfStringSchema,
            "score", ImmutableMap.of("type", "integer")
        ),
        "required", ImmutableList.of("questions", "answers",
            "userAnswers", "correctness", "feedback", "score")
    );

    evaluationConfig = GenerateContentConfig.builder()
        .responseMimeType("application/json")
        .candidateCount(1)
        .responseJsonSchema(evaluationSchema)
        .build();

    evaluationPrompt = """
        You are an expert AI Teaching Assistant. Your task is to evaluate
        a student's submitted answers for a mock test and provide a
        complete, structured JSON output.

        You will be given the following:
        1.  The original course material (as attached PDF documents),
            which you must use as the absolute source of truth.
        2.  A formatted list of test questions, the official correct
            answers, and the student's submitted answers.

        **CRITICAL INSTRUCTION:** For some questions, especially
        short-answer or essay types, the 'Correct Answer' field will be
        intentionally left empty. In these specific cases, you MUST use
        the provided course material (the PDF documents) as the sole
        reference to judge the semantic correctness of the student's answer.

        **Your Evaluation Steps for Each Question:**
        1.  **Analyze:** Compare the "Student's Answer" to the "Correct
            Answer". If the correct answer is empty, analyze the student's
            answer against the context provided in the PDF documents.
        2.  **Assign Correctness:** Assign a numerical correctness value
            as a string: '1' for correct, '0.5' for partially correct,
            or '0' for incorrect.
        3.  **Provide Feedback:** Write a brief, helpful explanation for
            your evaluation. If the student is incorrect or partially
            correct, explain why and gently guide them toward the correct
            concept. For correct answers, provide brief positive
            reinforcement.

        **Final Score Calculation:**
        After evaluating all questions, you MUST calculate the final
        score. The score is calculated by summing all the numerical
        `correctness` values, dividing by the total number of questions,
        and multiplying by 100. The final score must be a single integer
        (round to the nearest whole number).

        **Output Format:**
        Your entire output MUST be a single, valid JSON object that
        adheres to the required schema. It must contain the six required
        fields: 'questions', 'answers', 'userAnswers', 'correctness',
        'feedback', and 'score'. It is critical that all arrays have the
        same length.
        """;

    // Flashcard Config
    ImmutableMap<String, Object> flashcardSchema = ImmutableMap.of(
        "type", "object",
        "properties", ImmutableMap.of(
            "questions", ImmutableMap.of("type", "array",
                "items", ImmutableMap.of("type", "string")),
            "answers", ImmutableMap.of("type", "array",
                "items", ImmutableMap.of("type", "string"))
        ),
        "required", ImmutableList.of("questions", "answers")
    );

    flashcardConfig = GenerateContentConfig.builder()
        .responseMimeType("application/json")
        .candidateCount(1)
        .responseJsonSchema(flashcardSchema)
        .build();

    flashcardPrompt = """
        You are an AI tutor. Based on the provided content, generate 5
        short flashcards.
        Return JSON:
        {
            "questions": [],
            "answers": []
        }
        """;
  }

  @Override
  public TestData getTestData(List<PDFFile> courseMaterials) throws IOException {
    List<Part> partsList = new ArrayList<>();
    partsList.add(Part.fromText(generationPrompt));

    for (PDFFile pdfFile : courseMaterials) {
      Path pdfPath = pdfFile.getPath();
      byte[] pdfData = Files.readAllBytes(pdfPath);
      partsList.add(Part.fromBytes(pdfData, "application/pdf"));
    }

    Content content = Content.fromParts(partsList.toArray(new Part[0]));
    GenerateContentResponse response =
        client.models.generateContent("gemini-2.5-flash", content,
            generationConfig);

    return gson.fromJson(response.text(), TestData.class);
  }

  @Override
  public EvaluationData getEvaluationResults(List<PDFFile> courseMaterials,
      List<String> userAnswers, List<String> questions,
      List<String> answers) throws IOException {
    StringBuilder qaBuilder = new StringBuilder();
    qaBuilder.append("--- TEST DATA TO EVALUATE ---\n\n");
    for (int i = 0; i < questions.size(); i++) {
      qaBuilder.append("--- Question ").append(i + 1).append(" ---\n");
      qaBuilder.append("Question: ").append(questions.get(i)).append("\n");
      qaBuilder.append("Correct Answer: ").append(answers.get(i))
          .append("\n");
      qaBuilder.append("Student's Answer: ").append(userAnswers.get(i))
          .append("\n\n");
    }

    List<Part> partsList = new ArrayList<>();
    partsList.add(Part.fromText(evaluationPrompt));
    partsList.add(Part.fromText(qaBuilder.toString()));
    for (PDFFile pdfFile : courseMaterials) {
      byte[] pdfData = Files.readAllBytes(pdfFile.getPath());
      partsList.add(Part.fromBytes(pdfData, "application/pdf"));
    }

    Content content = Content.fromParts(partsList.toArray(new Part[0]));
    GenerateContentResponse response =
        client.models.generateContent("gemini-2.5-flash", content,
            evaluationConfig);

    EvaluationData result = gson.fromJson(response.text(),
        EvaluationData.class);
    if (result == null) {
      throw new IOException("Failed to parse a valid evaluation from the "
          + "API response.");
    }
    return result;
  }

  @Override
  public FlashcardSet generateForCourse(String courseName,
      String contentInput) throws IOException {
    List<Part> partsList = new ArrayList<>();
    partsList.add(Part.fromText(flashcardPrompt));

    File maybePdf = new File(contentInput);
    if (maybePdf.exists() && contentInput.toLowerCase().endsWith(".pdf")) {
      byte[] pdfBytes = Files.readAllBytes(maybePdf.toPath());
      partsList.add(Part.fromBytes(pdfBytes, "application/pdf"));
    } else {
      partsList.add(Part.fromText(contentInput));
    }

    Content request = Content.fromParts(partsList.toArray(new Part[0]));
    GenerateContentResponse response =
        client.models.generateContent("gemini-2.5-flash", request,
            flashcardConfig);

    FlashcardResponse parsed = gson.fromJson(response.text(),
        FlashcardResponse.class);
    if (parsed == null || parsed.questions == null
        || parsed.answers == null) {
      throw new IOException("Invalid response from Gemini.");
    }

    List<Flashcard> flashcards = new ArrayList<>();
    int size = Math.min(parsed.questions.size(), parsed.answers.size());
    for (int i = 0; i < size; i++) {
      flashcards.add(new Flashcard(parsed.questions.get(i),
          parsed.answers.get(i)));
    }

    return new FlashcardSet(courseName, flashcards);
  }

  /**
   * Helper class for deserializing flashcard response from Gemini API.
   */
  private static class FlashcardResponse {
    List<String> questions;
    List<String> answers;
  }
}
