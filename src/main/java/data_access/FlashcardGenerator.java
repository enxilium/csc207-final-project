package data_access;

import entities.FlashcardSet;
import java.io.IOException;

public interface FlashcardGenerator {
    FlashcardSet generateForCourse(String courseName, String content) throws IOException;
}