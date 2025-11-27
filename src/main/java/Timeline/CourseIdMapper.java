package Timeline;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class to map String course IDs (from Course entity) to UUIDs (for Timeline).
 * This allows Timeline to work with the course system that uses String IDs.
 */
public class CourseIdMapper {
    private static final Map<String, UUID> courseIdToUuid = new HashMap<>();
    private static final Map<UUID, String> uuidToCourseId = new HashMap<>();

    /**
     * Gets or creates a UUID for a given String course ID.
     * @param courseId The String course ID (e.g., "PHL245")
     * @return The corresponding UUID
     */
    public static UUID getUuidForCourseId(String courseId) {
        if (courseId == null || courseId.isEmpty()) {
            throw new IllegalArgumentException("Course ID cannot be null or empty");
        }
        return courseIdToUuid.computeIfAbsent(courseId, k -> {
            UUID uuid = UUID.randomUUID();
            uuidToCourseId.put(uuid, courseId);
            return uuid;
        });
    }

    /**
     * Gets the String course ID for a given UUID.
     * @param uuid The UUID
     * @return The corresponding String course ID, or null if not found
     */
    public static String getCourseIdForUuid(UUID uuid) {
        return uuidToCourseId.get(uuid);
    }

    /**
     * Checks if a UUID exists for a given course ID.
     * @param courseId The String course ID
     * @return true if a mapping exists
     */
    public static boolean hasUuid(String courseId) {
        return courseId != null && courseIdToUuid.containsKey(courseId);
    }
}

