package Timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourseIdMapperTest {

    @BeforeEach
    void setUp() {
        // Clear static maps by using reflection or by testing fresh instances
        // Since maps are static, we'll test that getUuidForCourseId creates new mappings
        // and that getCourseIdForUuid retrieves them correctly
    }

    @Test
    void testGetUuidForCourseId() {
        String courseId = "CSC207";
        UUID uuid1 = CourseIdMapper.getUuidForCourseId(courseId);
        UUID uuid2 = CourseIdMapper.getUuidForCourseId(courseId);

        assertNotNull(uuid1);
        assertNotNull(uuid2);
        // Same course ID should return same UUID
        assertEquals(uuid1, uuid2);
    }

    @Test
    void testGetUuidForCourseIdCreatesNewMapping() {
        String courseId1 = "CSC207";
        String courseId2 = "PHL245";
        
        UUID uuid1 = CourseIdMapper.getUuidForCourseId(courseId1);
        UUID uuid2 = CourseIdMapper.getUuidForCourseId(courseId2);

        assertNotNull(uuid1);
        assertNotNull(uuid2);
        // Different course IDs should return different UUIDs
        assertNotEquals(uuid1, uuid2);
    }

    @Test
    void testGetUuidForCourseIdThrowsExceptionForNull() {
        assertThrows(IllegalArgumentException.class, () -> CourseIdMapper.getUuidForCourseId(null));
    }

    @Test
    void testGetUuidForCourseIdThrowsExceptionForEmpty() {
        assertThrows(IllegalArgumentException.class, () -> CourseIdMapper.getUuidForCourseId(""));
    }

    @Test
    void testGetCourseIdForUuid() {
        String courseId = "CSC207";
        UUID uuid = CourseIdMapper.getUuidForCourseId(courseId);
        
        String retrieved = CourseIdMapper.getCourseIdForUuid(uuid);
        assertEquals(courseId, retrieved);
    }

    @Test
    void testGetCourseIdForUuidReturnsNullForUnknown() {
        UUID unknownUuid = UUID.randomUUID();
        String result = CourseIdMapper.getCourseIdForUuid(unknownUuid);
        assertNull(result);
    }

    @Test
    void testGetCourseIdForUuidReturnsNullForNull() {
        String result = CourseIdMapper.getCourseIdForUuid(null);
        assertNull(result);
    }

    @Test
    void testHasUuid() {
        String courseId = "CSC207";
        
        // After getting UUID, should have it
        CourseIdMapper.getUuidForCourseId(courseId);
        boolean after = CourseIdMapper.hasUuid(courseId);
        
        // After calling getUuidForCourseId, it should return true
        assertTrue(after);
    }

    @Test
    void testHasUuidReturnsFalseForNull() {
        // hasUuid always returns false for null - this is expected behavior
        boolean result = CourseIdMapper.hasUuid(null);
        assertFalse(result);
    }

    @Test
    void testHasUuidReturnsFalseForUnknown() {
        assertFalse(CourseIdMapper.hasUuid("UNKNOWN_COURSE"));
    }

    @Test
    void testBidirectionalMapping() {
        String courseId1 = "CSC207";
        String courseId2 = "PHL245";
        
        UUID uuid1 = CourseIdMapper.getUuidForCourseId(courseId1);
        UUID uuid2 = CourseIdMapper.getUuidForCourseId(courseId2);
        
        // Test forward mapping
        assertEquals(courseId1, CourseIdMapper.getCourseIdForUuid(uuid1));
        assertEquals(courseId2, CourseIdMapper.getCourseIdForUuid(uuid2));
        
        // Test reverse mapping
        assertEquals(uuid1, CourseIdMapper.getUuidForCourseId(courseId1));
        assertEquals(uuid2, CourseIdMapper.getUuidForCourseId(courseId2));
    }
}

