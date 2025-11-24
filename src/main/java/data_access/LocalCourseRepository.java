package data_access;
import entities.*;
import entities.Course;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;
import usecases.evaluate_test.EvaluateTestCourseDataAccessInterface;
import usecases.mock_test_generation.MockTestGenerationCourseDataAccessInterface;

public class LocalCourseRepository implements usecases.ICourseRepository, MockTestGenerationCourseDataAccessInterface,
        EvaluateTestCourseDataAccessInterface {
    private List<Course> courses = null;
    private final String FILE_NAME = "courses.json";
    public LocalCourseRepository(){
        courses = new ArrayList<>();
        /*
        courses.add(new Course("CSC494", "Computer Science Project", "Computer Science Project"));
        courses.add(new Course("CSC458", "Computer Networking Systems", "Computer Networking Systems"));
        courses.add(new Course("CSC301", "Introduction to Software Engineering", "Introduction to Software Engineering"));
        courses.add(new Course("CSC454", "The Business of Software", "The Business of Software"));
        courses.add(new Course("CSC324", "Principles of Programming Languages", "Principles of Programming Languages"));
        courses.add(new Course("CSC384", "Introduction to Artificial Intelligence", "Introduction to Artificial Intelligence"));
        courses.add(new Course("CSC494", "Computer Science Project", "Computer Science Project"));
*/
        CreateFileIfNotExist();
    }
    @Override
    public void create(Course course) {
        courses.add(course);
        writeCourses(courses);
    }
    @Override
    public void update(Course course) {
        List<Course> courses = this.readCourses();
        Optional<entities.Course> foundObject = courses.stream()
                .filter(obj -> obj.getCourseId().equals(course.getCourseId()))
                .findFirst();
        if (foundObject.isPresent()) {
            Course foundCourse = foundObject.get();
            foundCourse.setCourseId(course.getCourseId());
            foundCourse.setName(course.getName());
            foundCourse.setDescription(course.getDescription());
        }
        writeCourses(courses);
    }

    @Override
    public Course findById(String courseId) {
        this.courses = readCourses();
        if (this.courses.size() == 0) {
            return null;
        }
        Optional<Course> foundObject = courses.stream()
                .filter(obj -> obj.getCourseId().equals(courseId))
                .findFirst();
        if (foundObject.isPresent()) {
            return foundObject.get();
        }else{
            return null;
        }
    }

    @Override
    public List<Course> findAll() {
        this.courses = readCourses();
        return this.courses;
    }

    @Override
    public void delete(String courseId) {
        this.courses = readCourses();
        if (this.courses.size() == 0) {
            return ;
        }

        this.courses.removeIf(obj -> obj.getCourseId().equals(courseId));
        writeCourses(courses);
    }


    private void CreateFileIfNotExist(){
        File file = new File(FILE_NAME);

        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
                writeCourses(this.courses);
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during file creation.");
            e.printStackTrace();
        }
    }

    private List<Course> readCourses(){
//        List<Course> courses = new ArrayList<>();
//        //JSONParser parser = new JSONParser();
//        try (String jsonString = Files.readString(Paths.get(FILE_NAME));
//        ) { // Assuming data.json exists
//           // Object obj = parser.parse(reader);
//
//            // If the root is a JSONObject
//           // if (obj instanceof JSONObject) {
//          //      JSONObject jsonObject = (JSONObject) obj;
//                // String name = (String) jsonObject.get("name");
//                // long age = (long) jsonObject.get("age");
//                //  System.out.println("Name: " + name + ", Age: " + age);
//           // }
//            // If the root is a JSONArray
//           // else if (obj instanceof JSONArray) {
//            JSONArray jsonArray = new JSONArray(jsonString);
//
//            //JSONArray jsonArray = (jsonString) obj;
//                for (Object element : jsonArray) {
//                    JSONObject jsonObject = (JSONObject) element;
//                    courses.add(new Course((String) jsonObject.get("courseId"),
//                            (String) jsonObject.get("name"),
//                            (String) jsonObject.get("description")));
//                    // Process each item in the array
//                    //System.out.println("Array Item: " + item.toJSONString());
//                }
//           // }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return courses;
//    }

        List<Course> courses = new ArrayList<>();

        try {
            // Read the whole file into a String (no try-with-resources needed)
            String jsonString = Files.readString(Paths.get(FILE_NAME));

            // Parse as a JSON array
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String courseId = jsonObject.getString("courseId");
                String name = jsonObject.getString("name");
                String description = jsonObject.getString("description");

                courses.add(new Course(courseId, name, description));
            }
        } catch (IOException e) {
            // File not found / can’t read → just start with empty list
            System.out.println("Could not read " + FILE_NAME + ", starting with empty course list.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    private void writeCourses(List<Course> courses){
        //JSONObject rootObject = new JSONObject();
        //rootObject.put("courses", courses);
        JSONArray objectArray = new JSONArray();
        courses.forEach(course -> {
            JSONObject obj = new JSONObject();
            obj.put("courseId", course.getCourseId());
            obj.put("name", course.getName());
            obj.put("description", course.getDescription());
            objectArray.put(obj);
        });


        // Write the JSON array to a file
        try (FileWriter file = new FileWriter(FILE_NAME)) {
            file.write(objectArray.toString());
            System.out.println("Successfully wrote JSON array to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PDFFile> getCourseMaterials(String courseId) {
        System.out.println("Searching for: " + courseId);
        System.out.println("Saved courses: " +  this.courses);
        for (Course course :  this.courses) {
            if (course.getCourseId().equals(courseId)) {
                System.out.println(course.getCourseId() + course.getName() + course.getUploadedFiles().get(0).getPath());
                return course.getUploadedFiles();
            }
        }
        // Return an empty list if the course is not found, preventing null pointer exceptions.
        return Collections.emptyList();
    }
}
